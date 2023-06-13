package com.example.checkers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serwer oczekuje na połączenie...");

            Socket socket = serverSocket.accept();
            System.out.println("Połączono z klientem.");

            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Utwórz wątek do odbierania obiektów od klienta
            Thread receivingThread = new Thread(new ReceivingThread(objectInputStream));
            receivingThread.start();

            // Wysyłanie obiektów do klienta

            while (true) {
                Packet packet = new Packet(1 , 1, 1, 1, false, false);
                objectOutputStream.writeObject(packet);
                System.out.println("Wysłano: " + packet.startcol + " " + packet.startrow);
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Wątek do odbierania obiektów od klienta
    private static class ReceivingThread implements Runnable {
        private ObjectInputStream objectInputStream;

        public ReceivingThread(ObjectInputStream objectInputStream) {
            this.objectInputStream = objectInputStream;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Packet packet = (Packet) objectInputStream.readObject();
                    System.out.println("Otrzymano od klienta: " + packet.startcol + " " + packet.startrow);
                }
            } catch (IOException | ClassNotFoundException ignored) {}
        }
    }
}
