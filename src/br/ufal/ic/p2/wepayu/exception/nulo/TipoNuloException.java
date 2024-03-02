package br.ufal.ic.p2.wepayu.exception.nulo;

public class TipoNuloException extends NuloException {
    public TipoNuloException(){
        super("Tipo nao pode ser nulo.");
    }
}
