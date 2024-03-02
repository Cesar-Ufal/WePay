package br.ufal.ic.p2.wepayu.exception.numerico.negativo;

public class SalarioNegativoException extends NegativoException {
    public SalarioNegativoException(){
        super("Salario deve ser nao-negativo.");
    }
}
