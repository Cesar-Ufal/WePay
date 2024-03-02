package br.ufal.ic.p2.wepayu.exception.nulo;

public class MembroNuloException extends NuloException {
    public MembroNuloException(){
        super("Identificacao do membro nao pode ser nula.");
    }
}
