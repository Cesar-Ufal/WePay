package br.ufal.ic.p2.wepayu.exception.nulo;

public abstract class NuloException extends Exception {
    NuloException(String error){
        super(error);
    }
}
