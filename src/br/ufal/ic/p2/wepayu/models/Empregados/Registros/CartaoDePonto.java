package br.ufal.ic.p2.wepayu.models.Empregados.Registros;

import java.io.Serializable;

public class CartaoDePonto implements Serializable {
    private float horas;
    private String data;

    public CartaoDePonto(float horas, String data) {
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
