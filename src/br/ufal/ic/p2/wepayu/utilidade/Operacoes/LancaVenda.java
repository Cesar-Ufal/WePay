package br.ufal.ic.p2.wepayu.utilidade.Operacoes;

import br.ufal.ic.p2.wepayu.models.Empregados.Registros.ResultadoDeVenda;

public class LancaVenda extends Operacao {
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
