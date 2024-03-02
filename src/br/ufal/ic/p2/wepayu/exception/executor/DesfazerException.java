package br.ufal.ic.p2.wepayu.exception.executor;

public class DesfazerException extends ExecutorException {
    public DesfazerException(){
        super("Nao ha comando a desfazer.");
    }
}
