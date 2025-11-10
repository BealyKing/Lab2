package org.vvgu.clientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MessageClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ввод адреса сервера
        System.out.print("Введите адрес сервера (IP или hostname): ");
        String serverAddress = scanner.nextLine();

        System.out.print("Введите порт сервера: ");
        int port = scanner.nextInt(); // Порт сервера

        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("\nПодключено к серверу: " + serverAddress + ":" + port);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true);

            BufferedReader userInput = new BufferedReader(
                    new InputStreamReader(System.in));

            // Поток для чтения сообщений от сервера
            Thread readThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.equalsIgnoreCase("exit")) {
                            System.out.println("\nСервер завершил соединение");
                            break;
                        }
                        System.out.println("\nServer: [" + serverAddress + ":" + port + "]: " + message);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Ошибка чтения: " + e.getMessage());
                    }
                }
            });
            readThread.start();

            // Отправка сообщений серверу
            System.out.println("\nКлиент готов к обмену сообщениями. Введите 'exit' для выхода.");
            String clientMessage;
            while ((clientMessage = userInput.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    // System.out.println("I am(Client): exit");
                    break;
                }
                out.println(clientMessage);
                // System.out.println("I am(Client): " + clientMessage);
            }

            // Завершение работы
            readThread.interrupt();
            System.out.println("Соединение закрыто");
        } catch (UnknownHostException e) {
            System.err.println("Неизвестный хост: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }
}