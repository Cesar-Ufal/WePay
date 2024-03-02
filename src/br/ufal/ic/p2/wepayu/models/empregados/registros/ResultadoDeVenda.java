package br.ufal.ic.p2.wepayu.models.empregados.registros;

import br.ufal.ic.p2.wepayu.exception.invalido.InvalidoException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoDate;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;

public class ResultadoDeVenda implements Serializable {
    private float valor;
    private String data;

    public ResultadoDeVenda(String valorString, String data) throws NuloException, NumericoException, InvalidoException {
        float valor = Sanitation.toFloat(valorString, TipoNumerico.valor);
        this.setValor(valor);
        this.setData(data);
    }

    public ResultadoDeVenda(float valor, String data) throws NumericoException, NuloException, InvalidoException {
        this.setValor(valor);
        this.setData(data);
    }

    public ResultadoDeVenda(){

    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) throws NumericoException, NuloException {
        Sanitation.numberValid(valor, TipoNumerico.valor);
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) throws InvalidoException {
        Sanitation.isValid(data, TipoDate.Data);
        this.data = data;
    }
}
