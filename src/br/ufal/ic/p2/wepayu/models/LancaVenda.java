package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

public class LancaVenda extends Operacao implements Serializable {
    private ResultadoDeVenda venda;
    public LancaVenda(String emp, ResultadoDeVenda venda) {
        this.setId(emp);
        this.setVenda(venda);
    }

    public ResultadoDeVenda getVenda() {
        return this.venda;
    }

    public void setVenda(ResultadoDeVenda venda) {
        this.venda = venda;
    }

}
