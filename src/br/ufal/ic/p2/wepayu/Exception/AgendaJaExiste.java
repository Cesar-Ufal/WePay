package br.ufal.ic.p2.wepayu.Exception;

public class AgendaJaExiste extends Exception {
    public AgendaJaExiste(){
        super("Agenda de pagamentos ja existe");
    }
}
