package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.time.DayOfWeek;

public class Semanal extends AgendaDePagamento implements Serializable {
    private int periodicidade;
    private int dayOfWeek;

    public Semanal(int periodicidade, int dayOfWeek) {
        this.periodicidade = periodicidade;
        this.dayOfWeek = dayOfWeek;
    }

    public Semanal(){

    }

    public int getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(int periodicidade) {
        this.periodicidade = periodicidade;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public DayOfWeek getSemana(){
        return switch (this.dayOfWeek) {
            case 1 -> DayOfWeek.MONDAY;
            case 2 -> DayOfWeek.TUESDAY;
            case 3 -> DayOfWeek.WEDNESDAY;
            case 4 -> DayOfWeek.THURSDAY;
            case 5 -> DayOfWeek.FRIDAY;
            case 6 -> DayOfWeek.SATURDAY;
            case 7 -> DayOfWeek.SUNDAY;
            default -> throw new IllegalStateException("Unexpected value: " + dayOfWeek);
        };
    }
}
