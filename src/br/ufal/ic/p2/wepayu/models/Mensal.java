package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.time.DayOfWeek;

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
