package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Requisicao implements Runnable {

    public Requisicao(ServerSocket serverSocket, Integer success, Semaphore mutex, ArrayList<Reserva> reservas) throws IOException, InterruptedException {

        // construtor de requisição exige um objeto, server socket, objeto integet, um objeto mutex, um array de objetos Reserva armazenados em reserva
        Socket socket = serverSocket.accept(); /* The accept() method  is used to accept the incoming request to the socket  */
        InputStream inputStream = socket.getInputStream(); /* returns an input stream for the given socket. */

        byte[] buffer = new byte[2048]; /*It stores a reference to an object containing a value*/
        int len = inputStream.read(buffer); /* guarda o comprimento do objeto socket lido pelo buffer  */

        String requisicao = new String(buffer, 0, len);
        /*requisição recebe, buffer
        offset ( first char to use from the array.), e o comprimento
        do objeto socket lido pelo buffer */

    // lines recebe o objeto da requisição
        String[] lines = requisicao.split("\n");
        String[] linha0 = lines[0].split(" "); // regular expression GET

        System.out.println("Requisicao Method: " + linha0[0] + "+  Loaded Resource path: " + linha0[1]);
        OutputStream out = socket.getOutputStream();
        File file = new File("resources" + File.separator + linha0[1]);

        if (linha0[1].equals("/")) {
            file = new File("resources" + File.separator + "index.html");
        } else if (linha0[1].startsWith("/solicitar")) {
            file = new File("resources" + File.separator + "solicitar.html");
        } else if (linha0[1].startsWith("/finalizar")) {
            mutex.acquire();
            String nome = Service.getNomeStr(linha0[1]);
            int numAssento = Service.getAssentoInt(linha0[1]);
            String data = Service.getDateTime();
            if (!reservas.isEmpty()) {
                if (Reserva.verificarLugares(numAssento, reservas)) {
                    success = 0;
                } else {
                    Reserva novaReserva = new Reserva(numAssento, true, nome, data);
                    reservas.add(novaReserva);
                    new GerenciaLog(socket, requisicao, data);
                    success = 1;
                }
            } else {
                System.out.println("Nova reserva");
                Reserva novaReserva = new Reserva(numAssento, true, nome, data);
                reservas.add(novaReserva);
                new GerenciaLog(socket, requisicao, data);
                success = 1;
            }
            mutex.release();
            file = new File("resources" + File.separator + "index.html");



        } else if (linha0[1].equals("/js/index.js")) {
            String js = Service.montaJS(reservas);
            out.write(js.getBytes());
        } else if (linha0[1].equals("/js/solicitar.js")) {
            // onibus de solicitar
            String js = Service.montaJSSolicitar(reservas);
            out.write(js.getBytes());
        }

        if (file.exists() && !linha0[1].equals("/js/index.js") && !linha0[1].equals("/js/solicitar.js")) {

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                String mimeType = Files.probeContentType(file.toPath());


                out.write(("HTTP/1.1 200 OK\n" +
                        "Content-Type: " + mimeType + ";charset=UTF-8\n\n").getBytes());

                if (linha0[1].startsWith("/finalizar")) {
                    switch (success) {
                        case 1 -> out.write("<script type='text/javascript'>alert('Seu pedido foi realizado com sucesso')</script>".getBytes(StandardCharsets.UTF_8));
                        case 0 -> out.write("<script type='text/javascript'>alert('Opa :(, houve um problema com seu pedido, pedimos que escolha outro assento')</script>".getBytes(StandardCharsets.UTF_8));
                        default -> {
                        }
                    }
                }

                len = fileInputStream.read(buffer);

                while (len > 0) {
                    out.write(buffer, 0, len);
                    len = fileInputStream.read(buffer);
                }
            }
        }


        out.flush();
        socket.close();
    }

    /* thread da requisição  */
    @Override
    public void run() {
        System.out.println("Requisicao run! Resource requested !");
    }
}
