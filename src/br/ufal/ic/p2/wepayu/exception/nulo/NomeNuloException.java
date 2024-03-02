package br.ufal.ic.p2.wepayu.exception.nulo;

public class NomeNuloException extends NuloException {
    public NomeNuloException(){
        super("Nome nao pode ser nulo.");
    }
}
