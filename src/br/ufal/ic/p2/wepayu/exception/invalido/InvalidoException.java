package br.ufal.ic.p2.wepayu.exception.invalido;

public abstract class InvalidoException extends Exception {
    InvalidoException(String error){
        super(error);
    }
}
