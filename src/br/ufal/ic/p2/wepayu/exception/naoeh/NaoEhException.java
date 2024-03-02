package br.ufal.ic.p2.wepayu.exception.naoeh;

public abstract class NaoEhException extends Exception {
    NaoEhException(String error){
        super(error);
    }
}
