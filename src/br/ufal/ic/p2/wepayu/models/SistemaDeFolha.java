package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Agendas.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.Agendas.Mensal;
import br.ufal.ic.p2.wepayu.models.Agendas.Semanal;
import br.ufal.ic.p2.wepayu.models.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.models.Sindicato.MembroSindicato;

import java.util.*;

public class SistemaDeFolha {
    private Map<String, String> listaDeMembros;
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

    public void addEmpregado(String emp, Empregado empregado){
        this.empregados.put(emp, empregado);
    }

    public void replace(String emp, Empregado empregado2){
        this.empregados.replace(emp, empregado2);
    }

    public Map<String, String> getListaDeMembros() {
        return listaDeMembros;
    }

    public void setListaDeMembros(Map<String, String> listaDeMembros) {
        this.listaDeMembros = listaDeMembros;
    }

    public MembroSindicato criaNovoMembroSindicato(String idSindicato, float taxaSindical) throws MesmaIdentificacaoSindical {
        if(this.listaDeMembros.containsKey(idSindicato))
            throw new MesmaIdentificacaoSindical();
        return new MembroSindicato(idSindicato, taxaSindical);
    }

    public void addMembro(String emp, MembroSindicato membro) {
        Empregado empregado = this.empregados.get(emp);
        if(empregado.getMembroSindicato() instanceof MembroSindicato membroSindicato)
            this.listaDeMembros.remove(membroSindicato.getIdMembro());
        empregado.setMembroSindicato(membro);
        if(membro instanceof MembroSindicato membroSindicato)
            this.listaDeMembros.put(membroSindicato.getIdMembro(), emp);
    }

    public String getEmpregadoIdPeloMembro(String idMembro) throws MembroNaoExisteException {
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
        for(Map.Entry<String, Empregado> empregado: this.empregados.entrySet()){
            if(nome.equals(empregado.getValue().getNome()) && --indice == 0)
                return empregado.getKey();
        }
        throw new NaoEmpregadoNome();
    }

    public void removeEmpregado(String idEmpregado) {
        this.empregados.remove(idEmpregado);
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

    public void criaAgenda(String descricao) throws AgendaJaExiste, DescricaoAgendaInvalida {
        if(this.agendas.containsKey(descricao))
            throw new AgendaJaExiste();
        String[] parametros = descricao.split(" ");
        try {
            if (parametros[0].equals("mensal")) {
                if(parametros.length == 2){
                    if(parametros[1].equals("$")) {
                        this.agendas.put(descricao, new Mensal(0));
                        return;
                    }
                    int dia = Integer.parseInt(parametros[1]);
                    if(dia > 0 && dia < 29) {
                        this.agendas.put(descricao, new Mensal(dia));
                        return;
                    }
                }
            } else if (parametros[0].equals("semanal")) {
                if(parametros.length == 2){
                    int semana = Integer.parseInt(parametros[1]);
                    if(semana > 0 && semana < 8) {
                        this.agendas.put(descricao, new Semanal(1, semana));
                        return;
                    }
                }else if(parametros.length == 3){
                    int periodicidade = Integer.parseInt(parametros[1]);
                    int semana = Integer.parseInt(parametros[2]);
                    if(periodicidade > 0 && periodicidade < 53 && semana > 0 && semana < 8) {
                        this.agendas.put(descricao, new Semanal(periodicidade, semana));
                        return;
                    }
                }
            }
        }catch (Exception e) {
            throw new DescricaoAgendaInvalida();
        }
        throw new DescricaoAgendaInvalida();
    }

    public boolean agendaExiste(String agenda){
        return this.agendas.containsKey(agenda);
    }

    public void addAgenda(String descricao, AgendaDePagamento agenda){
        this.agendas.put(descricao, agenda);
    }

}
