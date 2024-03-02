package br.ufal.ic.p2.wepayu.exception.existe;

public class MembroNaoExisteException extends ExisteException{
    public MembroNaoExisteException(){
        super("Membro nao existe.");
    }
}
