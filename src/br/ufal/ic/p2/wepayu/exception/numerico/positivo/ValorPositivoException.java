package br.ufal.ic.p2.wepayu.exception.numerico.positivo;

public class ValorPositivoException extends PositivoException {
    public ValorPositivoException(){
        super("Valor deve ser positivo.");
    }
}
