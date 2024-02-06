package br.ufal.ic.p2.wepayu.Exception;

public class NaoComissionado extends Exception {
    public NaoComissionado(){
        super("Empregado nao eh comissionado.");
    }
}
