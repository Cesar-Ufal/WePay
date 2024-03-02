package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exception.existe.*;
import br.ufal.ic.p2.wepayu.exception.invalido.DescricaoAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.exception.invalido.InvalidoException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoEhException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoEmpregadoNomeException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoSindicalizadoException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.models.agendas.AgendaDePagamento;
import br.ufal.ic.p2.wepayu.models.agendas.Mensal;
import br.ufal.ic.p2.wepayu.models.agendas.Semanal;
import br.ufal.ic.p2.wepayu.models.empregados.Empregado;
import br.ufal.ic.p2.wepayu.models.sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.utilidade.Atributo;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;

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

    public MembroSindicato criaNovoMembroSindicato(String idSindicato, String taxaSindical) throws ExisteException, NumericoException, NuloException {
        Sanitation.notNull(idSindicato, Atributo.idSindicato);
        if(this.listaDeMembros.containsKey(idSindicato))
            throw new MesmaIdentificacaoSindicalException();
        return new MembroSindicato(idSindicato, taxaSindical);
    }

    public void addMembro(String emp, MembroSindicato membro) throws NaoEhException {
        Empregado empregado = this.empregados.get(emp);
        if(empregado.getMembroSindicato() != null)
            this.listaDeMembros.remove(empregado.getIdSindicato());
        empregado.setMembroSindicato(membro);
        if(membro != null)
            this.listaDeMembros.put(membro.getIdMembro(), emp);
    }

    public Empregado getEmpregadoIdPeloMembro(String idMembro) throws ExisteException, NuloException {
        if(! (this.listaDeMembros.containsKey(idMembro)))
            throw new MembroNaoExisteException();
        return this.getEmpregadoById(this.listaDeMembros.get(idMembro));
    }

    public Empregado getEmpregadoById(String idEmpregado) throws ExisteException, NuloException {
        Sanitation.notNull(idEmpregado, Atributo.idEmpregado);
        if(! (this.empregados.containsKey(idEmpregado)))
            throw new EmpregadoNaoExisteException();
        return this.empregados.get(idEmpregado);
    }

    public String getEmpregadoByName(String nome, int indice) throws NaoEhException, NuloException {
        Sanitation.notNull(nome, Atributo.nome);
        for(Map.Entry<String, Empregado> empregado: this.empregados.entrySet()){
            if(nome.equals(empregado.getValue().getNome()) && --indice == 0)
                return empregado.getKey();
        }
        throw new NaoEmpregadoNomeException();
    }

    public void removeEmpregado(String idEmpregado) {
        this.empregados.remove(idEmpregado);
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

    public void criaAgenda(String descricao) throws InvalidoException, ExisteException {
        if(this.agendas.containsKey(descricao))
            throw new AgendaJaExisteException();
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
            throw new DescricaoAgendaInvalidaException();
        }
        throw new DescricaoAgendaInvalidaException();
    }

    public boolean agendaExiste(String agenda){
        return this.agendas.containsKey(agenda);
    }

}
