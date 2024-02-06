package br.ufal.ic.p2.wepayu.Exception;

public class ContaNulo extends Exception {
    public ContaNulo(){
        super("Conta corrente nao pode ser nulo.");
    }
}
