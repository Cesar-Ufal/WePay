package br.ufal.ic.p2.wepayu.utilidade.operacoes;

import br.ufal.ic.p2.wepayu.utilidade.Atributo;

import java.io.Serializable;

public class AlteraEmpregado extends Operacao implements Serializable {
    private Atributo atributo;
    private Object valorAntigo;
    private Object valorNovo;
    public AlteraEmpregado(String emp, Atributo atributo, Object valorAntigo, Object valorNovo) {
        this.setAtributo(atributo);
        this.setValorAntigo(valorAntigo);
        this.setValorNovo(valorNovo);
        this.setId(emp);
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public Object getValorAntigo() {
        return valorAntigo;
    }

    public void setValorAntigo(Object valorAntigo) {
        this.valorAntigo = valorAntigo;
    }

    public Object getValorNovo() {
        return valorNovo;
    }

    public void setValorNovo(Object valorNovo) {
        this.valorNovo = valorNovo;
    }
}
