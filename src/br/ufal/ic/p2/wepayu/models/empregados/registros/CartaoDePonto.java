package br.ufal.ic.p2.wepayu.models.empregados.registros;

import br.ufal.ic.p2.wepayu.exception.invalido.InvalidoException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoDate;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;

public class CartaoDePonto implements Serializable {
    private float horas;
    private String data;

    public CartaoDePonto(String horas, String data) throws NumericoException, NuloException, InvalidoException {
        this.setHoras(horas);
        this.setData(data);
    }

    public CartaoDePonto(float horas, String data) throws NumericoException, NuloException, InvalidoException {
        this.setHoras(horas);
        this.setData(data);
    }

    public CartaoDePonto(){

    }

    public float getHoras() {
        return horas;
    }

    public void setHoras(float horas) throws NumericoException, NuloException {
        Sanitation.numberValid(horas, TipoNumerico.hora);
        this.horas = horas;
    }

    public void setHoras(String horasString) throws NuloException, NumericoException {
        float horas = Sanitation.toFloat(horasString, TipoNumerico.hora);
        this.setHoras(horas);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) throws InvalidoException {
        Sanitation.isValid(data, TipoDate.Data);
        this.data = data;
    }
}
