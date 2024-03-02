package br.ufal.ic.p2.wepayu.exception.numerico.positivo;

public class HoraPositivaException extends PositivoException {
    public HoraPositivaException(){
        super("Horas devem ser positivas.");
    }
}
