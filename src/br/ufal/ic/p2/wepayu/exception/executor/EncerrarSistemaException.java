package br.ufal.ic.p2.wepayu.exception.executor;

public class EncerrarSistemaException extends ExecutorException {
    public EncerrarSistemaException(){
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}
