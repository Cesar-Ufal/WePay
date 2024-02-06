package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

public class LancaServico extends Operacao implements Serializable {
    private TaxaServico servico;
    public LancaServico(String emp, TaxaServico servico) {
        this.setId(emp);
        this.setServico(servico);
    }

    public LancaServico(){

    }

    public TaxaServico getServico() {
        return this.servico;
    }

    public void setServico(TaxaServico servico) {
        this.servico = servico;
    }

}
