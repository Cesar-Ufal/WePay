package br.ufal.ic.p2.wepayu.exception.nulo;

public class IdSindicatoNuloException extends NuloException {
    public IdSindicatoNuloException(){
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
