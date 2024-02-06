package br.ufal.ic.p2.wepayu.Exception;

public class BancoNulo extends Exception {
    public BancoNulo(){
        super("Banco nao pode ser nulo.");
    }
}
