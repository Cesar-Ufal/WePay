package br.ufal.ic.p2.wepayu.exception;

public class CronologicaException extends Exception {
    public CronologicaException(){
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
