package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.NaoComissionado;
import br.ufal.ic.p2.wepayu.Exception.NaoRecebeBanco;

import java.util.UUID;

public abstract class Empregado {
    private String uuid;
    private String nome;
    private String endereco;
    private String idMembro;
    private String dataContratacao;
    private String tipo;
    private String agenda;
    private MetodoPagamento metodoPagamento;
    public Empregado(String nome, String endereco) {
        this.uuid = UUID.randomUUID().toString();
        setNome(nome);
        setEndereco(endereco);
        this.idMembro = null;
        this.metodoPagamento = new EmMaos();
    }
    public Empregado(){}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getIdMembro() {
        return this.idMembro;
    }

    public boolean ehSindicalizado() {
        return this.idMembro != null;
    }

    public void setIdMembro(String idMembro) {
        this.idMembro = idMembro;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
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

    public float getSalario(){
        if(this instanceof Horista)
            return ((Horista) this).getSalarioPorHora();
        if(this instanceof Assalariado)
            return ((Assalariado) this).getSalarioMensal();
        return ((Comissionado) this).getSalarioMensal();
    }

    public void setSalario(float salario){
        if(this instanceof Horista)
            ((Horista) this).setSalarioPorHora(salario);
        if(this instanceof Assalariado)
            ((Assalariado) this).setSalarioMensal(salario);
        if(this instanceof Comissionado)
            ((Comissionado) this).setSalarioMensal(salario);
    }

    public float getComissao() throws NaoComissionado {
        if(this instanceof Comissionado)
            return ((Comissionado) this).getTaxaDeComissao();
        throw new NaoComissionado();
    }

    public String getBanco() throws NaoRecebeBanco {
        if(this.metodoPagamento instanceof Banco)
            return ((Banco) this.metodoPagamento).getBanco();
        throw new NaoRecebeBanco();
    }

    public String getAgencia() throws NaoRecebeBanco {
        if(this.metodoPagamento instanceof Banco)
            return ((Banco) this.metodoPagamento).getAgencia();
        throw new NaoRecebeBanco();
    }

    public String getContaCorrente() throws NaoRecebeBanco {
        if(this.metodoPagamento instanceof Banco)
            return ((Banco) this.metodoPagamento).getContaCorrente();
        throw new NaoRecebeBanco();
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }
}
