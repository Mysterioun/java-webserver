package server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Service {

    /*status code indicates that the request has been processed successfully on the server, and the
    * type of file that needs to be updated/
    /* and javascript type (text/javascript) + source () */
    public static String getHeader(){
        return "HTTP/1.1 200 OK\n Content-Type: text/javascript; charset=UTF-8\n\n";
    }

    /* monta o status dos assentos
     * e também as tabelas na página index.html */
    public static String montaJS(ArrayList<Reserva> reservas){

        String header = getHeader();
        /*criamos um lopp para percorrer as reservasconcatenamos header na variavel*/
        String body = ""; // poderia ser nulo também, whatever it makes sense to you

        /*  para todas as reservas do arraylist */
        for (Reserva res : reservas) {
            /*se estiver reservado*/
            if(res.isReservado()) {
                /* primeiro pega os elementos por Id do HTML e marca com a classe CSS ".ocupado" com fundo vermelho */
                body = body + " document.getElementById('" + res.getNumAssento() + "').classList.add('ocupado')\n";
                /* joga o numAssento, nome e dateTime na tabela  */
                body = body + " $('#teste').append('<tr> <th>"+res.getNumAssento()+"</th><td>"+res.getNome()+"</td><td>"+res.getData()+"</td></tr>')\n";
            }
        }
        /*concatena o que vier pela String "body" no getHeader */
        return header.concat(body);
    }

    /* monta o status apenas
    dos assentos na página solicitar.html para auxiliar */
    public static String montaJSSolicitar(ArrayList<Reserva> reservas){
        String header = getHeader();
        System.out.println("Booking Ticket");
        String body = ""; /*null maybe, or whatever*/

        /*para todas as reservas do arraylist*/
        for (Reserva res : reservas) {
            /*se estiver reservado*/
            if(res.isReservado()) {
                /*remove do select a opção que ja foi reservada*/
                body = body +" $('#"+res.getNumAssento()+"').remove(); ";
                /*css color change */
                body = body +" $('.icon0"+res.getNumAssento()+"').addClass('ocupado'); "; // para os assentos reservados, muda cor para red
            }
        }
        /*concatena expreesion language*/
        return header.concat(body);
    }

    /*monta string do nome digitado no formulário
     * e envia para a primeira função*/
    public static String getNomeStr(String nome){
        /*string char array "finUrl declared"*/
        String[] finUrl = nome.split("=");
        String[] nomeRet = finUrl[1].split("&");
        /* inserção do nome */
        System.out.println("nome inserido");
        /* */
        return nomeRet[0].toString();

    }

    /*montando dados do assento escolhido no select do formulário
     * e envia para a primeira função*/
    public static Integer getAssentoInt(String num){
        String[] finUrl = num.split("="); // regular expression
        System.out.println("opção de assento escolhida");
        return Integer.parseInt(finUrl[2].toString());
    }

    /* montando a data para a tabela
     * e envia para a primeira função*/
    public static  String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println("dateTime succesfully stamped"); // datatempo foi carimbado
        return dateFormat.format(date);
    }
}