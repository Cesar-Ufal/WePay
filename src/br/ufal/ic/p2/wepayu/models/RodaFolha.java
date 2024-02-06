package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

public class RodaFolha extends Operacao implements Serializable {

    private String saida;
    public RodaFolha(String emp, String saida) {
        this.setSaida(saida);
        this.setId(emp);
    }

    public String getSaida() {
        return saida;
    }

    public void setSaida(String saida) {
        this.saida = saida;
    }
}
