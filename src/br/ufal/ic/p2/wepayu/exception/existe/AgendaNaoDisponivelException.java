package br.ufal.ic.p2.wepayu.exception.existe;

public class AgendaNaoDisponivelException extends ExisteException {
    public AgendaNaoDisponivelException(){
        super("Agenda de pagamento nao esta disponivel");
    }
}
