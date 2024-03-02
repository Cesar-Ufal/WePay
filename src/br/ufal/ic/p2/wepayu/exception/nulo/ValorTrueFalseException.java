package br.ufal.ic.p2.wepayu.exception.nulo;

public class ValorTrueFalseException extends NuloException {
    public ValorTrueFalseException(){
        super("Valor deve ser true ou false.");
    }
}
