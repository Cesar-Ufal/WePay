package br.ufal.ic.p2.wepayu.Exception;

public class ComissaoNaoNegativa extends Exception {
    public ComissaoNaoNegativa(){
        super("Comissao deve ser nao-negativa.");
    }
}
