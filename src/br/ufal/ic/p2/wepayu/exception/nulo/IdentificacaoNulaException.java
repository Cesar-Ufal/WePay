package br.ufal.ic.p2.wepayu.exception.nulo;

public class IdentificacaoNulaException extends NuloException {
    public IdentificacaoNulaException(){
        super("Identificacao do empregado nao pode ser nula.");
    }
}
