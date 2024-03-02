package br.ufal.ic.p2.wepayu.exception.existe;

public class EmpregadoNaoExisteException extends ExisteException{
    public EmpregadoNaoExisteException(){
        super("Empregado nao existe.");
    }
}
