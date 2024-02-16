package br.ufal.ic.p2.wepayu.models.Empregados.Registros;

import java.io.Serializable;

public class ResultadoDeVenda implements Serializable {
    private float valor;
    private String data;

    public ResultadoDeVenda(float valor, String data) {
        this();
        this.setValor(valor);
        this.setData(data);
    }
    public ResultadoDeVenda(){

    }
    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
