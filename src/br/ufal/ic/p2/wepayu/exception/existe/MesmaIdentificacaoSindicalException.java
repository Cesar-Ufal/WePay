package br.ufal.ic.p2.wepayu.exception.existe;

public class MesmaIdentificacaoSindicalException extends ExisteException{
    public MesmaIdentificacaoSindicalException(){
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
