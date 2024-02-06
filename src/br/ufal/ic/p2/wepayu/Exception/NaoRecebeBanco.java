package br.ufal.ic.p2.wepayu.Exception;

public class NaoRecebeBanco extends Exception {
    public NaoRecebeBanco(){
        super("Empregado nao recebe em banco.");
    }
}
