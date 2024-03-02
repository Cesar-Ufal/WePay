package br.ufal.ic.p2.wepayu.utilidade.operacoes;

import br.ufal.ic.p2.wepayu.models.empregados.Empregado;

import java.io.Serializable;

public class RemoveEmpregado extends Operacao implements Serializable {
    private Empregado empregado;

    public RemoveEmpregado(String emp, Empregado empregado) {
        this.setId(emp);
        this.setEmpregado(empregado);
    }

    public Empregado getEmpregado() {
        return empregado;
    }

    public void setEmpregado(Empregado empregado) {
        this.empregado = empregado;
    }
}
