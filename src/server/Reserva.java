package server;

import java.util.ArrayList;

public class Reserva {

    /*atributos*/
    private int numAssento;
    private boolean reservado;
    private String nome;
    private String data;

    /*construtor*/
    public Reserva(int numAssento, boolean reservado, String nome, String data) {
        this.numAssento = numAssento;
        this.reservado = reservado;
        this.nome = nome;
        this.data = data;
    }

    /*metodos getters and setters*/
    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }

    public int getNumAssento() {
        return numAssento;
    }

    public void setNumAssento(int numAssento) {
        this.numAssento = numAssento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /* função em linha 58 de Requisição */
    /* para todas as reservas, se ja houver algo reservado, retorno com true, caso contrario, false*/
    public static boolean verificarLugares(int numAssento, ArrayList<Reserva> res){
        for (Reserva reservas : res) {
            // para todos objetos Reserva armazenados na variável reservas
            if (reservas.getNumAssento() == numAssento){
                return true;
            }
        }
        return false;
    }
}