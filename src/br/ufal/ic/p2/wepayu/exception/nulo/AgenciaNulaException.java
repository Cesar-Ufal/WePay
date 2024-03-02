package br.ufal.ic.p2.wepayu.exception.nulo;

public class AgenciaNulaException extends NuloException {
    public AgenciaNulaException(){
        super("Agencia nao pode ser nulo.");
    }
}
