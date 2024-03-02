package br.ufal.ic.p2.wepayu.models.sindicato;

import br.ufal.ic.p2.wepayu.exception.invalido.InvalidoException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoDate;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;

public class TaxaServico implements Serializable {
    private String data;
    private float valor;

    public TaxaServico(String data, String valorString) throws NuloException, NumericoException, InvalidoException {
        float valor = Sanitation.toFloat(valorString, TipoNumerico.valor);
        this.setData(data);
        this.setValor(valor);
    }

    public TaxaServico(String data, float valor) throws NumericoException, NuloException, InvalidoException {
        this.setData(data);
        this.setValor(valor);
    }
    public TaxaServico(){

    }

    public String getData() {
        return data;
    }

    public void setData(String data) throws InvalidoException {
        Sanitation.isValid(data, TipoDate.Data);
        this.data = data;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) throws NumericoException, NuloException {
        Sanitation.numberValid(valor, TipoNumerico.valor);
        this.valor = valor;
    }
}
