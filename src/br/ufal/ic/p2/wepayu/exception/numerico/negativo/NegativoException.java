package br.ufal.ic.p2.wepayu.exception.numerico.negativo;

import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;

public abstract class NegativoException extends NumericoException {
    NegativoException(String error){
        super(error);
    }
}
