package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

import java.util.*;

public class SistemaDeFolha {
    private Map<String, MembroSindicato> listaDeMembros;
    private Map<String, Empregado> empregados;
    private Map<String, AgendaDePagamento> agendas;

    public SistemaDeFolha() {
        this.setEmpregados(new LinkedHashMap<>());
        this.setListaDeMembros(new HashMap<>());
        this.setAgendas(new HashMap<>());
        this.agendas.put("semanal 5", new Semanal(1, 5));
        this.agendas.put("semanal 2 5", new Semanal(2, 5));
        this.agendas.put("mensal $", new Mensal(0));
    }

    public Map<String, Empregado>  getEmpregados() {
        return empregados;
    }

    public void setEmpregados(Map<String, Empregado>  empregados) {
        this.empregados = empregados;
    }
    public void add(Empregado empregado){
        this.empregados.put(empregado.getUuid(), empregado);
    }

    public void addMembro(MembroSindicato membro){
        this.listaDeMembros.put(membro.getIdMembro(), membro);
    }

    public void replace(Empregado empregado1, Empregado empregado2){
        this.empregados.replace(empregado1.getUuid(), empregado2);
    }
    public Map<String, MembroSindicato> getListaDeMembros() {
        return listaDeMembros;
    }

    public void setListaDeMembros(Map<String, MembroSindicato> listaDeMembros) {
        this.listaDeMembros = listaDeMembros;
    }
    public MembroSindicato getMembroById(String idMembro) throws MembroNaoExisteException, NaoSindicalizado {
        if(idMembro == null)
            throw new NaoSindicalizado();
        if(! (this.listaDeMembros.containsKey(idMembro)))
            throw new MembroNaoExisteException();
        return this.listaDeMembros.get(idMembro);
    }
    public Empregado getEmpregadoById(String idEmpregado) throws EmpregadoNaoExisteException {
        if(! (this.empregados.containsKey(idEmpregado)))
            throw new EmpregadoNaoExisteException();
        return this.empregados.get(idEmpregado);
    }

    public String getEmpregadoByName(String nome, int indice) throws NaoEmpregadoNome {
        for(Empregado empregado: this.empregados.values()){
            if(nome.equals(empregado.getNome()) && --indice == 0)
                return empregado.getUuid();
        }
        throw new NaoEmpregadoNome();
    }
    public void remove(String idEmpregado) {
        this.empregados.remove(idEmpregado);
    }

    public boolean membroExiste(String membroId){
        return this.listaDeMembros.containsKey(membroId);
    }

    public void removeMembro(String membroId){
        this.listaDeMembros.remove(membroId);
    }

    public void setAgendas(Map<String, AgendaDePagamento> agendas) {
        this.agendas = agendas;
    }

    public Map<String, AgendaDePagamento> getAgendas() {
        return this.agendas;
    }

    public AgendaDePagamento getAgendaByEmpregado(Empregado empregado) {
        return this.agendas.get(empregado.getAgenda());
    }
    public boolean agendaExiste(String agenda){
        return this.agendas.containsKey(agenda);
    }
    public void addAgenda(String descricao, AgendaDePagamento agenda){
        this.agendas.put(descricao, agenda);
    }
}
