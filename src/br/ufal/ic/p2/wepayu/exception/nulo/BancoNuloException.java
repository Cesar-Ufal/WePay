package br.ufal.ic.p2.wepayu.exception.nulo;

public class BancoNuloException extends NuloException {
    public BancoNuloException(){
        super("Banco nao pode ser nulo.");
    }
}
