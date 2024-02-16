package br.ufal.ic.p2.wepayu.models.Agendas;

import java.io.Serializable;

public class Mensal extends AgendaDePagamento implements Serializable {
    private int dia;

    public Mensal(int dia) {
        this.dia = dia;
    }

    public Mensal(){

    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }
}
