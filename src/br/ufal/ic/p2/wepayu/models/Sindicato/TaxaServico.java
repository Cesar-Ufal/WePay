package br.ufal.ic.p2.wepayu.models.Sindicato;

import java.io.Serializable;

public class TaxaServico implements Serializable {
    private String data;
    private float valor;

    public TaxaServico(String data, float valor) {
        this();
        this.setData(data);
        this.setValor(valor);
    }
    public TaxaServico(){

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }
}
