package br.ufal.ic.p2.wepayu.exception.existe;

public class AgendaJaExisteException extends ExisteException {
    public AgendaJaExisteException(){
        super("Agenda de pagamentos ja existe");
    }
}
