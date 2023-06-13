package com.example.checkers;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private static ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String host;
    private int port;
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(host, port);
            System.out.println("Połączono z serwerem.");

            // Wysyłanie obiektów do serwera
            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Utwórz wątek do odbierania obiektów od serwera
            Thread receivingThread = new Thread(new ReceivingThread(objectInputStream));
            receivingThread.start();

//            while (true) {
//                // Poczekaj na kliknięcie myszą przez gracza
//                // Możesz użyć odpowiednich metod związanych z GUI, aby oczekiwać na kliknięcie myszą.
//                // Przykład:
//                // Jeśli kliknięto na przycisk, możesz dodać odpowiednią logikę do obsługi zdarzenia kliknięcia przycisku.
//                // Wewnątrz metody obsługi kliknięcia przycisku możesz utworzyć obiekt klasy Person i wysłać go do serwera.
//
////                Packet packet = new Packet(2 , 2, 2, 2, false);
////                objectOutputStream.writeObject(packet);
////                System.out.println("Wysłano do serwera: " + packet.startcol + " " + packet.startrow);
//                Thread.sleep(500);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    public synchronized static void sendPacket(Packet packet) throws IOException {
        objectOutputStream.writeObject(packet);
        System.out.println("Wysłano do serwera: " + packet.startcol + " " + packet.startrow);
    }

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
                    System.out.println("Otrzymano od serwera: " + packet.startcol + " " + packet.startrow);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
