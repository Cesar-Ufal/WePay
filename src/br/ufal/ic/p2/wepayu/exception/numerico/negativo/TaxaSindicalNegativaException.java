package br.ufal.ic.p2.wepayu.exception.numerico.negativo;

public class TaxaSindicalNegativaException extends NegativoException {
    public TaxaSindicalNegativaException(){
        super("Taxa sindical deve ser nao-negativa.");
    }
}
