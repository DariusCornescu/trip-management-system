package com.darius.project.networking;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private final int PORT = 5556;
    private ServerSocket serverSocket;
    private final CopyOnWriteArrayList<ClientHandler> clients;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("🚀 Server started on port " + PORT);
        } catch (IOException e) {
            System.err.println("❌ Error starting server: " + e.getMessage());
        }
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                pool.execute(handler);
                System.out.println("✅ New client connected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                System.err.println("❌ Error accepting client: " + e.getMessage());
            }
        }
    }

    public void broadcastUpdate(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public static void main(String[] args) {
        new Server().start();
    }
}