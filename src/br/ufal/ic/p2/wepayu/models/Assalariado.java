package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utilidade.Sanitation;

import java.io.Serializable;

public class Assalariado extends Empregado implements Serializable{
    private float salarioMensal;
    public Assalariado(String nome, String endereco, float salarioMensal) {
        super(nome, endereco);
        setSalarioMensal(salarioMensal);
        this.setDataContratacao(Sanitation.incialDate);
        this.setTipo("assalariado");
        this.setAgenda("mensal $");
    }
    public Assalariado() {
    }
    public Assalariado(Empregado empregado){
        this(empregado.getNome(), empregado.getEndereco(), empregado.getSalario());
        this.setUuid(empregado.getUuid());
        this.setIdMembro(empregado.getIdMembro());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
    }
    public Assalariado(Empregado empregado, float salarioPorHora){
        this(empregado.getNome(), empregado.getEndereco(), salarioPorHora);
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
}
