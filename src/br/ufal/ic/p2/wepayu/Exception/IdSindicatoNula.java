package br.ufal.ic.p2.wepayu.Exception;

public class IdSindicatoNula extends Exception {
    public IdSindicatoNula(){
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
