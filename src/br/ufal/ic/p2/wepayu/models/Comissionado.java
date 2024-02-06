package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utilidade.Sanitation;

import java.io.Serializable;
import java.util.ArrayList;

public class Comissionado extends Empregado implements Serializable{
    private float salarioMensal;
    private float taxaDeComissao;
    private ArrayList<ResultadoDeVenda> vendas;

    public Comissionado(String nome, String endereco, float salarioMensal, float taxaDeComissao) {
        super(nome, endereco);
        setSalarioMensal(salarioMensal);
        setTaxaDeComissao(taxaDeComissao);
        this.setVendas(new ArrayList<>());
        this.setDataContratacao(Sanitation.incialDate);
        this.setTipo("comissionado");
        this.setAgenda("semanal 2 5");
    }
    public Comissionado() {
    }

    public Comissionado(Empregado empregado, float taxaDeComissao){
        this(empregado.getNome(), empregado.getEndereco(), empregado.getSalario(), taxaDeComissao);
        this.setUuid(empregado.getUuid());
        this.setIdMembro(empregado.getIdMembro());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
    }
    public void setSalarioMensal(float salarioMensal) {
        this.salarioMensal = salarioMensal;
    }
    public float getSalarioMensal(){
        return this.salarioMensal;
    }
    public void setTaxaDeComissao(float taxaDeComissao) {
        this.taxaDeComissao = taxaDeComissao;
    }
    public float getTaxaDeComissao(){
        return this.taxaDeComissao;
    }

    public ArrayList<ResultadoDeVenda> getVendas() {
        return vendas;
    }

    public void setVendas(ArrayList<ResultadoDeVenda> vendas) {
        this.vendas = vendas;
    }

    public void addVenda(ResultadoDeVenda venda){
        this.vendas.add(venda);
    }

    public void removeVenda(ResultadoDeVenda venda){
        this.vendas.remove(venda);
    }
}
