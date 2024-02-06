package br.ufal.ic.p2.wepayu.Exception;

public class DataInvalida extends Exception {
    public DataInvalida(String data){
        super("Data" + data + " invalida.");
    }
}
