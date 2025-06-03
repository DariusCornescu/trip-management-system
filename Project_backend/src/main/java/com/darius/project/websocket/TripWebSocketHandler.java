package com.darius.project.websocket;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.*;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.util.Map;

public class TripWebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TripWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        LOGGER.info("WebSocket connection established: {}", session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws IOException {
        LOGGER.debug("Received message: {}", message.getPayload());

        try {
            Map<String, Object> payload = objectMapper.readValue( message.getPayload(), new TypeReference<>() {} );
            String type = (String) payload.get("type");
            Object data = payload.get("data");
            broadcastMessage(type, data, session.getId());

        } catch (IOException ioEx) {
            LOGGER.error("I/O error when parsing or handling the incoming text message", ioEx);
            throw ioEx;
        } catch (RuntimeException rex) {

            LOGGER.error("Unexpected error handling WebSocket message", rex);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        LOGGER.info("WebSocket connection closed: {}, status: {}", session.getId(), status);
        sessions.remove(session.getId());
    }

    public void broadcastMessage(@NonNull String type, @NonNull Object data, @NonNull String excludeSessionId) {
        Map<String, Object> envelope = Map.of("type", type, "data", data);
        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(envelope);
        } catch (IOException e) {
            LOGGER.error("Error serializing broadcast message", e);
            return;
        }

        sessions.forEach((id, wsSession) -> {
            if (!id.equals(excludeSessionId) && wsSession.isOpen()) {
                try {
                    wsSession.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException sendEx) {
                    LOGGER.error("Error sending message to session {}", id, sendEx);
                }
            }
        });
    }
}
