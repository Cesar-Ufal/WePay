package br.ufal.ic.p2.wepayu.models.Empregados;

import br.ufal.ic.p2.wepayu.Exception.NaoComissionado;
import br.ufal.ic.p2.wepayu.Exception.NaoRecebeBanco;
import br.ufal.ic.p2.wepayu.Exception.NaoSindicalizado;
import br.ufal.ic.p2.wepayu.models.Empregados.Pagamentos.Banco;
import br.ufal.ic.p2.wepayu.models.Empregados.Pagamentos.EmMaos;
import br.ufal.ic.p2.wepayu.models.Empregados.Pagamentos.MetodoPagamento;
import br.ufal.ic.p2.wepayu.models.Sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.models.Sindicato.TaxaServico;

public abstract class Empregado {

    private String nome;
    private String endereco;
    private MembroSindicato membroSindicato;
    private String dataContratacao;
    private String tipo;
    private String agenda;
    private MetodoPagamento metodoPagamento;
    public Empregado(String nome, String endereco) {
        setNome(nome);
        setEndereco(endereco);
        this.membroSindicato = null;
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

    public MembroSindicato getMembroSindicato() {
        return this.membroSindicato;
    }

    public boolean ehSindicalizado() {
        return this.membroSindicato != null;
    }

    public void setMembroSindicato(MembroSindicato membroSindicato) {
        this.membroSindicato = membroSindicato;
    }

    public String getIdSindicato() throws NaoSindicalizado {
        if(this.membroSindicato == null)
            throw new NaoSindicalizado();
        return this.membroSindicato.getIdMembro();
    }

    public float getTaxaSindical() throws NaoSindicalizado {
        if(this.membroSindicato == null)
            throw new NaoSindicalizado();
        return this.membroSindicato.getTaxaSindical();
    }

    public void addServico(TaxaServico servico) throws NaoSindicalizado {
        if(this.membroSindicato == null)
            throw new NaoSindicalizado();
        this.membroSindicato.addTaxa(servico);
    }

    public void removeServico(TaxaServico servico) throws NaoSindicalizado {
        if(this.membroSindicato == null)
            throw new NaoSindicalizado();
        this.membroSindicato.removeTaxa(servico);
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

    public void setTComissao(float taxaDeComissao) throws NaoComissionado {
        if(this instanceof Comissionado)
            ((Comissionado) this).setTaxaDeComissao(taxaDeComissao);
        else
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
