package br.ufal.ic.p2.wepayu.Exception;

public class SalarioNaoNegativo extends Exception {
    public SalarioNaoNegativo(){
        super("Salario deve ser nao-negativo.");
    }
}
