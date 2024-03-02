package br.ufal.ic.p2.wepayu.exception.nulo;

public class ComissaoNulaException extends NuloException {
    public ComissaoNulaException(){
        super("Comissao nao pode ser nula.");
    }
}
