package br.ufal.ic.p2.wepayu.exception.numerico;

public class SalarioNumericoException extends NumericoException {
    public SalarioNumericoException(){
        super("Salario deve ser numerico.");
    }
}
