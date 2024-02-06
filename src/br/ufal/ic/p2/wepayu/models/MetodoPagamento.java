package br.ufal.ic.p2.wepayu.models;

abstract public class MetodoPagamento {
    private String metodo;

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
}
