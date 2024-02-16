package br.ufal.ic.p2.wepayu.utilidade.Operacoes;

import br.ufal.ic.p2.wepayu.models.Agendas.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.models.Sindicato.MembroSindicato;

import java.io.Serializable;
import java.util.Map;

public class ZeraSistema extends Operacao {
    private Map<String, String> listaDeMembros;
    private Map<String, Empregado> empregados;
    private Map<String, AgendaDePagamento> agendas;
    public ZeraSistema(Map<String, String> listaDeMembros, Map<String, Empregado> empregados, Map<String, AgendaDePagamento> agendas) {
        this.setId(null);
        this.setEmpregados(empregados);
        this.setListaDeMembros(listaDeMembros);
        this.setAgendas(agendas);
    }

    public Map<String, String> getListaDeMembros() {
        return listaDeMembros;
    }

    public void setListaDeMembros(Map<String, String> listaDeMembros) {
        this.listaDeMembros = listaDeMembros;
    }

    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    public void setEmpregados(Map<String, Empregado> empregados) {
        this.empregados = empregados;
    }

    public Map<String, AgendaDePagamento> getAgendas() {
        return agendas;
    }

    public void setAgendas(Map<String, AgendaDePagamento> agendas) {
        this.agendas = agendas;
    }
}
