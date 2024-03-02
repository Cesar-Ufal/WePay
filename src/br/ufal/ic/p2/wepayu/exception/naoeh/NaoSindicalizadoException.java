package br.ufal.ic.p2.wepayu.exception.naoeh;

public class NaoSindicalizadoException extends NaoEhException {
    public NaoSindicalizadoException(){
        super("Empregado nao eh sindicalizado.");
    }
}
