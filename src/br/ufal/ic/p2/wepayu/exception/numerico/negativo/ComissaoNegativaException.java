package br.ufal.ic.p2.wepayu.exception.numerico.negativo;

public class ComissaoNegativaException extends NegativoException {
    public ComissaoNegativaException(){
        super("Comissao deve ser nao-negativa.");
    }
}
