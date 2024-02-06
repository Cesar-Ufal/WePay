package br.ufal.ic.p2.wepayu.Exception;

public class ComissaoNula extends Exception {
    public ComissaoNula(){
        super("Comissao nao pode ser nula.");
    }
}
