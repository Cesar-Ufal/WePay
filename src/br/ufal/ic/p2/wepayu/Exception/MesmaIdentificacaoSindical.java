package br.ufal.ic.p2.wepayu.Exception;

public class MesmaIdentificacaoSindical extends Exception{
    public MesmaIdentificacaoSindical(){
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
