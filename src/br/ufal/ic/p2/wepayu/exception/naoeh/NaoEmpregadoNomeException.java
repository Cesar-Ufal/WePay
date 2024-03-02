package br.ufal.ic.p2.wepayu.exception.naoeh;

public class NaoEmpregadoNomeException extends NaoEhException {
    public NaoEmpregadoNomeException(){
        super("Nao ha empregado com esse nome.");
    }
}
