package br.ufal.ic.p2.wepayu.Exception;

public class NomeNulo extends Exception {
    public NomeNulo(){
        super("Nome nao pode ser nulo.");
    }
}
