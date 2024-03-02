package br.ufal.ic.p2.wepayu.exception.numerico.positivo;

import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;

public abstract class PositivoException extends NumericoException {
    PositivoException(String error){
        super(error);
    }
}
