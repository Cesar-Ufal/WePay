package br.ufal.ic.p2.wepayu.exception.numerico;

public class ComissaoNumericaException extends NumericoException {
    public ComissaoNumericaException(){
        super("Comissao deve ser numerica.");
    }
}
