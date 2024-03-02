package br.ufal.ic.p2.wepayu.models.empregados;

import br.ufal.ic.p2.wepayu.exception.CronologicaException;
import br.ufal.ic.p2.wepayu.exception.invalido.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.exception.naoeh.*;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.models.empregados.pagamentos.EmMaos;
import br.ufal.ic.p2.wepayu.models.empregados.pagamentos.MetodoPagamento;
import br.ufal.ic.p2.wepayu.models.sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.models.sindicato.TaxaServico;
import br.ufal.ic.p2.wepayu.utilidade.*;

import java.time.LocalDate;

public abstract class Empregado {

    private String nome;
    private String endereco;
    private MembroSindicato membroSindicato;
    private String dataContratacao;
    private String tipo;
    private String agenda;
    private float divida;
    private MetodoPagamento metodoPagamento;
    public Empregado(String nome, String endereco) throws NuloException {
        this.setNome(nome);
        this.setEndereco(endereco);
        this.membroSindicato = null;
        this.metodoPagamento = new EmMaos();
        this.setDivida(0);
    }

    public Empregado(){}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws NuloException {
        Sanitation.notNull(nome, Atributo.nome);
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) throws NuloException {
        Sanitation.notNull(endereco, Atributo.endereco);
        this.endereco = endereco;
    }

    public MembroSindicato getMembroSindicato() {
        return this.membroSindicato;
    }

    public MembroSindicato getMembro() throws NaoEhException {
        if(this.membroSindicato == null)
            throw new NaoSindicalizadoException();
        return this.membroSindicato;
    }

    public void setMembroSindicato(MembroSindicato membroSindicato) {
        this.membroSindicato = membroSindicato;
    }

    public String getIdSindicato() throws NaoEhException {
        return this.getMembro().getIdMembro();
    }

    public float getTaxaSindical() throws NaoEhException {
        return this.getMembro().getTaxaSindical();
    }

    public void addServico(TaxaServico servico) throws NaoEhException {
        this.getMembro().addTaxa(servico);
    }

    public void removeServico(TaxaServico servico) throws NaoEhException {;
        this.getMembro().removeTaxa(servico);
    }

    public MetodoPagamento getMetodoPagamento() {
        return this.metodoPagamento;
    }

    public String getMetodo() {
        return this.metodoPagamento.getMetodo();
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getDataContratacao() {
        return this.dataContratacao;
    }

    public void setDataContratacao(String dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public abstract float getSalario();

    public abstract void setSalario(float salario) throws NuloException, NumericoException;

    public abstract void setSalario(String salario) throws NuloException, NumericoException;

    public float getTComissao() throws NaoEhException {
        throw new NaoComissionadoException();
    }

    public void setComissao(float taxaDeComissao) throws NaoEhException, NumericoException, NuloException {
        throw new NaoComissionadoException();
    }

    public void setComissao(String taxaDeComissao) throws NaoEhException, NumericoException, NuloException {
        throw new NaoComissionadoException();
    }

    public String getBanco() throws NaoEhException {
        return this.metodoPagamento.getBanco();
    }

    public String getAgencia() throws NaoEhException {
        return this.metodoPagamento.getAgencia();
    }

    public String getContaCorrente() throws NaoEhException {
        return this.metodoPagamento.getContaCorrente();
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getString(Atributo atributo) throws NaoEhException {
        return switch (atributo){
            case nome -> this.getNome();
            case endereco -> this.getEndereco();
            case sindicalizado -> this.membroSindicato != null ? "true" : "false";
            case tipo -> this.getTipo();
            case salario -> Sanitation.toString(this.getSalario(), TipoNumerico.salario);
            case comissao -> Sanitation.toString(this.getTComissao(), TipoNumerico.comissao);
            case metodoPagamento -> this.getMetodo();
            case idSindicato -> this.getIdSindicato();
            case taxaSindical -> Sanitation.toString(this.getTaxaSindical(), TipoNumerico.taxaSindical);
            case banco -> this.getBanco();
            case agencia -> this.getAgencia();
            case contaCorrente -> this.getContaCorrente();
            case agendaPagamento -> this.getAgenda();
            default -> "";
        };
    }

    public void setString(Atributo atributo, Object valor) throws NaoEhException, NuloException, NumericoException {
        switch (atributo){
            case nome -> this.setNome((String) valor);
            case endereco -> this.setEndereco((String) valor);
            case sindicalizado -> this.setMembroSindicato((MembroSindicato) valor);
            case salario -> this.setSalario((String) valor);
            case comissao -> this.setComissao((String) valor);
            case metodoPagamento -> this.setMetodoPagamento((MetodoPagamento) valor);
            case taxaSindical -> this.getMembroSindicato().setTaxaSindical((String) valor);
            case agendaPagamento -> this.setAgenda((String) valor);
        };
    }

    public Empregado mudaTipo(String tipo, String salario, String taxaComissao) throws NuloException, NumericoException, TipoInvalidoException, NaoEhException {
        return switch(tipo){
            case "horista" -> new Horista(this, salario, taxaComissao);
            case "assalariado" -> new Assalariado(this, salario, taxaComissao);
            case "comissionado" -> new Comissionado(this, salario, taxaComissao);
            default -> throw new TipoInvalidoException();
        };
    }

    public float getHoras(LocalDate dataInicial, LocalDate dataFinal, TipoGet tipo) throws NaoHoristaException, CronologicaException {
        Sanitation.ordemCronologica(dataInicial, dataFinal);
        throw new NaoHoristaException();
    }

    public float getVendas(LocalDate dataInicial, LocalDate dataFinal) throws CronologicaException, NaoComissionadoException {
        Sanitation.ordemCronologica(dataInicial, dataFinal);
        throw new NaoComissionadoException();
    }

    public float getServico(LocalDate dataInicial, LocalDate dataFinal) throws CronologicaException, NaoEhException {
        Sanitation.ordemCronologica(dataInicial, dataFinal);
        MembroSindicato membro = this.getMembro();
        float valor = 0;
        for (TaxaServico taxa : membro.getTaxas()) {
            LocalDate dataTaxa = Sanitation.toDate(taxa.getData());
            if (Sanitation.between(dataTaxa, dataInicial, dataFinal))
                valor += taxa.getValor();
        }
        return valor;
    }

    public float getDivida() {
        return divida;
    }

    public void setDivida(float divida) {
        this.divida = divida;
    }
}
