package org.vvgu.clientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageServer {
    public static void main(String[] args) {
        int port = 12345; // Порт для прослушивания

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен и ожидает подключения...");
            System.out.println("Адрес сервера: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Порт: " + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("\nКлиент подключен: " +
                    clientSocket.getInetAddress().getHostAddress() + ":" +
                    clientSocket.getPort());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream()),true);

            BufferedReader userInput = new BufferedReader(
                    new InputStreamReader(System.in));

            // Поток для чтения сообщений от клиента
            Thread readThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.equalsIgnoreCase("exit")) {
                            System.out.println("\nКлиент завершил соединение");
                            break;
                        }
                        System.out.println("\nClient [" + clientSocket.getInetAddress().getHostAddress() +
                                ":" + clientSocket.getPort() + "]: " + message);
                    }
                } catch (IOException e) {
                    if (!clientSocket.isClosed()) {
                        System.err.println("Ошибка чтения: " + e.getMessage());
                    }
                }
            });
            readThread.start();

            // Отправка сообщений клиенту
            System.out.println("\nСервер готов к обмену сообщениями. Введите 'exit' для выхода.");
            String serverMessage;
            while ((serverMessage = userInput.readLine()) != null) {
                if (serverMessage.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    // System.out.println("I am(Server): exit");
                    break;
                }
                out.println(serverMessage);
                // System.out.println("I am(Server): " + serverMessage);
            }

            // Завершение работы
            readThread.interrupt();
            clientSocket.close();
            System.out.println("Соединение закрыто");
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        }
    }
}