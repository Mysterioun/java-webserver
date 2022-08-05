package server;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Servidor {

    public static Semaphore mutex = new Semaphore(1);
    public static ArrayList <Reserva> reservas = new ArrayList();
    public static Integer success = 0; // um valor inteiro qualquer

    public static void main(String[] args) throws IOException {

        System.out.println("Servidor ->");

        System.out.println(success);
        ServerSocket serverSocket = new ServerSocket(90);

        while (true) {
            try {
                new Thread(new Requisicao(serverSocket, success, mutex, reservas)).start();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error on accept socket!");
            }
        }
    }
}