package br.ufal.ic.p2.wepayu.exception.invalido;

public class DataInvalidaException extends InvalidoException {
    public DataInvalidaException(String data){
        super("Data" + data + " invalida.");
    }
}
