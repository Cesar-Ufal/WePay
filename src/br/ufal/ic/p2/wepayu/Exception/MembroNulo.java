package br.ufal.ic.p2.wepayu.Exception;

public class MembroNulo extends Exception {
    public MembroNulo(){
        super("Identificacao do membro nao pode ser nula.");
    }
}
