package br.ufal.ic.p2.wepayu.Exception;

public class HoraPositiva extends Exception {
    public HoraPositiva(){
        super("Horas devem ser positivas.");
    }
}
