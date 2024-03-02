package br.ufal.ic.p2.wepayu.exception.nulo;

public class AgendaNulaException extends NuloException {
    public AgendaNulaException(){
        super("Agenda de pagamento nao pode ser nula.");
    }
}
