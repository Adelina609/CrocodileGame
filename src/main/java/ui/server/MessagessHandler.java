package ui.server;

import ui.painter.sPainter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MessagessHandler implements Runnable {

    private sPainter server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private Socket clientSocket = null;
    private static int clients_count = 0;
    private String chosenWord;

    public MessagessHandler(Socket socket, sPainter server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
            chosenWord = server.getChosenWord();
            System.out.println(chosenWord);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToAllClients("Новый участник вошёл в чат!");
                server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
                break;
            }

            while (true) {
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase(chosenWord)) {
                        clientMessage = "Слово отгадано! Победитель: ";
                    }
                    server.sendMessageToAllClients(clientMessage);
                }
            }
        }
        finally {
            this.close();
        }
    }
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void close() {
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}
