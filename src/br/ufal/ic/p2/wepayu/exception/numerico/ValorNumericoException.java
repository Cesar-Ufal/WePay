package br.ufal.ic.p2.wepayu.exception.numerico;

public class ValorNumericoException extends NumericoException {
    public ValorNumericoException(){
        super("Valor deve ser numerico.");
    }
}
