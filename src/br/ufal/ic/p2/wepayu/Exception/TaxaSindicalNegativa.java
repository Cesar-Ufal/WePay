package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicalNegativa extends Exception {
    public TaxaSindicalNegativa(){
        super("Taxa sindical deve ser nao-negativa.");
    }
}
