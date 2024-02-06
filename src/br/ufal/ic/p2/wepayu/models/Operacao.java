package br.ufal.ic.p2.wepayu.models;

abstract public class Operacao {
    private String id;

    public Operacao(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
