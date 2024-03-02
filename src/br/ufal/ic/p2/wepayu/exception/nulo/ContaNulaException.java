package br.ufal.ic.p2.wepayu.exception.nulo;

public class ContaNulaException extends NuloException {
    public ContaNulaException(){
        super("Conta corrente nao pode ser nulo.");
    }
}
