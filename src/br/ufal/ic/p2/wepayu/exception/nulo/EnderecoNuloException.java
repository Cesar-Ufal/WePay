package br.ufal.ic.p2.wepayu.exception.nulo;

public class EnderecoNuloException extends NuloException {
    public EnderecoNuloException(){
        super("Endereco nao pode ser nulo.");
    }
}
