package br.ufal.ic.p2.wepayu.models.Empregados.Registros;

import br.ufal.ic.p2.wepayu.Exception.DataInvalida;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;

import java.io.Serializable;
import java.time.LocalDate;

public class CartaoDePonto implements Serializable {
    private float horas;
    private String data;

    public CartaoDePonto(float horas, String data) throws DataInvalida {
        this();
        this.setHoras(horas);
        this.setData(data);
    }
    public CartaoDePonto(){

    }

    public float getHoras() {
        return horas;
    }

    public void setHoras(float horas) {
        this.horas = horas;
    }

    public String getData() {
        return data;
    }

    public void setData(String data)  {
        this.data = data;
    }
}
