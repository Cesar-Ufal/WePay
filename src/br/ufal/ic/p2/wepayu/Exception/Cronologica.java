package br.ufal.ic.p2.wepayu.Exception;

public class Cronologica extends Exception {
    public Cronologica(){
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
