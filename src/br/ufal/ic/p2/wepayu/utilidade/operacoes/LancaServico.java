package br.ufal.ic.p2.wepayu.utilidade.operacoes;

import br.ufal.ic.p2.wepayu.models.sindicato.TaxaServico;

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
