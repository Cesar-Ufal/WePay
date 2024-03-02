package br.ufal.ic.p2.wepayu.exception.nulo;

public class TaxaSindicalNulaException extends NuloException {
    public TaxaSindicalNulaException(){
        super("Taxa sindical nao pode ser nula.");
    }
}
