package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.util.Map;

public class ZeraSistema extends Operacao implements Serializable {
    private Map<String, MembroSindicato> listaDeMembros;
    private Map<String, Empregado> empregados;
    public ZeraSistema(Map<String, MembroSindicato> listaDeMembros, Map<String, Empregado> empregados) {
        this.setId(null);
        this.setEmpregados(empregados);
        this.setListaDeMembros(listaDeMembros);
    }

    public ZeraSistema() {
    }
    public Map<String, MembroSindicato> getListaDeMembros() {
        return listaDeMembros;
    }

    public void setListaDeMembros(Map<String, MembroSindicato> listaDeMembros) {
        this.listaDeMembros = listaDeMembros;
    }

    public Map<String, Empregado> getEmpregados() {
        return empregados;
    }

    public void setEmpregados(Map<String, Empregado> empregados) {
        this.empregados = empregados;
    }
}
