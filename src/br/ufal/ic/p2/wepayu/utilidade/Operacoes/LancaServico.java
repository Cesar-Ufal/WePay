package br.ufal.ic.p2.wepayu.utilidade.Operacoes;

import br.ufal.ic.p2.wepayu.models.Sindicato.TaxaServico;

import java.io.Serializable;

public class LancaServico extends Operacao {
    private TaxaServico servico;
    public LancaServico(String emp, TaxaServico servico) {
        this.setId(emp);
        this.setServico(servico);
    }

    public TaxaServico getServico() {
        return this.servico;
    }

    public void setServico(TaxaServico servico) {
        this.servico = servico;
    }

}
