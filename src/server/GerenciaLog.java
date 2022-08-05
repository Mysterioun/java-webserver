package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class GerenciaLog {

    /*inteiro guarda 1000*/
    private static int tamBuf = 1000;
    /*You could use a byte array to store a collection of binary data ( byte[] ),
    for example, the contents of a file.*/
    private static byte[] logBuffer = new byte[tamBuf]; /*byte array logBuffer*/

    private Socket Socket;
    private InetAddress ipReservado;


    Semaphore vazio = new Semaphore(tamBuf);
    Semaphore cheio = new Semaphore(0); /*sem uma permissao*/
    Semaphore mutex = new Semaphore(1); /*uma permissao*/

    private static int iStatic;
    private static File file = new File("log.txt"); /*cria arquivo*/
    private String requisicao;
    private String dataHora;

    public GerenciaLog(Socket socket, String requisicao, String dataHora) {
        this.dataHora = dataHora;
        this.requisicao = requisicao;
        Socket = socket;

        try {
            if (file.createNewFile()) {
                System.out.println("Arquivo criado: " + file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new GerenciaLog.ProduzLog()).start(); // processa
        new Thread(new GerenciaLog.ArmazenaLog()).start(); // armazena

    }

    private class ProduzLog implements Runnable {

        @Override
        public void run() {
            ipReservado = Socket.getInetAddress();
            String ipString = ipReservado.toString();
            String[] endereco = requisicao.split("Host");

            String logString = "\nIp:" + ipString + " Endereco: " + endereco[0] + " Data e hora: " + dataHora + " \n";

            byte[] logBype = logString.getBytes();

            try {
                vazio.acquire(logBype.length);
                mutex.acquire();
            } catch (InterruptedException e) {

            }
            System.out.println("gravou");

            for (int i = 0; i < logBype.length; i++) {
                logBuffer[iStatic] = logBype[i];
                iStatic++;
            }

            mutex.release();
            cheio.release();
        }
    }

    private class ArmazenaLog implements Runnable {

        @Override
        public void run() {
            try {
                cheio.acquire();
                mutex.acquire();
            } catch (InterruptedException e) {
                // TODO: handle exception
                /*codigo acima auto-gerado*/
            }

            String logString = new String(logBuffer, 0, iStatic);
            int tamanhoUsado = iStatic;
            iStatic = 0;
            logBuffer = new byte[tamBuf];

            System.out.println("consumiu");
            mutex.release();
            vazio.release(tamanhoUsado);

            try {
                FileWriter myWriter = new FileWriter("log.txt", true);
                myWriter.write(logString);
                myWriter.close();
            } catch (IOException e) {
                System.out.println("Erro ao gravar os dados");
                e.printStackTrace();
            }
        }
    }
}