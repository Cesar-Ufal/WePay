package br.ufal.ic.p2.wepayu.exception.executor;

public abstract class ExecutorException extends Exception {
    ExecutorException(String error){
        super(error);
    }
}
