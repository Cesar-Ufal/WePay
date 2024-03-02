package br.ufal.ic.p2.wepayu.exception.existe;

public abstract class ExisteException extends Exception {
    ExisteException(String error){
        super(error);
    }
}
