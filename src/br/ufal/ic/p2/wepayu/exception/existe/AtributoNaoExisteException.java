package br.ufal.ic.p2.wepayu.exception.existe;

public class AtributoNaoExisteException extends ExisteException {
    public AtributoNaoExisteException(){
        super("Atributo nao existe.");
    }
}
