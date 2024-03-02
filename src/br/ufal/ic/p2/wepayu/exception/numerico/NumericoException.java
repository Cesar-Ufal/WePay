package br.ufal.ic.p2.wepayu.exception.numerico;

public abstract class NumericoException extends Exception {
    public NumericoException(String error){
        super(error);
    }
}
