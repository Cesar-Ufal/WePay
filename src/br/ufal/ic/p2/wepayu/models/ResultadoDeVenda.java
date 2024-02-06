package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.DataInvalida;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoDate;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;
import java.time.LocalDate;

public class ResultadoDeVenda implements Serializable {
    private float valor;
    private String data;

    public ResultadoDeVenda(float valor, String data) throws DataInvalida {
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
