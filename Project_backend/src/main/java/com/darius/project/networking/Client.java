package com.darius.project.networking;
import javafx.application.Platform;
import org.slf4j.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static Client instance;
    private BufferedReader in;
    private PrintWriter out;
    private OnMessageReceivedListener listener;
    private boolean connected = false;

    public interface OnMessageReceivedListener {  void onMessageReceived(String message); }

    @SuppressWarnings("resource")
    private Client() {
        LOGGER.info("Initializing Client singleton");

        try {
            Socket socket = new Socket("localhost", 5556);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            LOGGER.info("Successfully connected to server at {}", socket.getRemoteSocketAddress());

            Thread listenerThread = new Thread(this::listenToServer);
            listenerThread.setName("ServerListenerThread");
            listenerThread.setDaemon(true);
            listenerThread.start();

        } catch (ConnectException ce) {
            LOGGER.error("Server is not running on localhost:5556. Please start the server first.");
            System.err.println("❌ Server is not running on localhost:5556. Please start the server first.");
        } catch (IOException e) {
            LOGGER.error("Error connecting to server: {}", e.getMessage(), e);
            System.err.println("❌ Error connecting to server: " + e.getMessage());
        }
    }

    public static Client getInstance() {
        if (instance == null) {
            LOGGER.debug("Creating new Client instance");
            instance = new Client();
        }
        return instance;
    }

    private void listenToServer() {
        LOGGER.info("Server listener thread started");
        try {
            String line;
            while ((line = in.readLine()) != null) {
                LOGGER.debug("Received message from server: {}", line);
                if (listener != null) {
                    String finalLine = line;
                    Platform.runLater(() -> {
                        try {
                            listener.onMessageReceived(finalLine);
                        } catch (Exception e) {
                            LOGGER.error("Error in message listener callback: {}", e.getMessage(), e);
                        }
                    });
                } else {
                    LOGGER.warn("Received message but no listener is registered: {}", line);
                }
            }
            LOGGER.info("Server connection closed (end of stream)");
        } catch (IOException e) {
            connected = false;
            LOGGER.error("Disconnected from server: {}", e.getMessage(), e);
            System.err.println("❌ Disconnected from server: " + e.getMessage());
        }
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        LOGGER.debug("Setting message received listener");
        this.listener = listener;
    }

    public void sendMessage(String message) {
        if (!connected) {
            LOGGER.error("Cannot send message - not connected to server");
            return;
        }
        if (message.startsWith("LOGIN#")) {
            LOGGER.info("Sending message: LOGIN# [credentials hidden]");
        } else {
            LOGGER.info("Sending message: {}", message);
        }

        out.println(message);

        if (out.checkError()) {
            LOGGER.error("Error occurred while sending message");
            connected = false;
        }
    }
}