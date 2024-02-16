package br.ufal.ic.p2.wepayu.models.Empregados;

import java.io.Serializable;

public class Assalariado extends Empregado implements Serializable{
    private float salarioMensal;

    public Assalariado(String nome, String endereco, float salarioMensal) {
        super(nome, endereco);
        setSalarioMensal(salarioMensal);
        this.setDataContratacao("1/1/2005");
        this.setTipo("assalariado");
        this.setAgenda("mensal $");
    }

    public Assalariado() {

    }

    public Assalariado(Empregado empregado){
        this(empregado.getNome(), empregado.getEndereco(), empregado.getSalario());
        this.setMembroSindicato(empregado.getMembroSindicato());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
    }

    public Assalariado(Empregado empregado, float salarioPorHora){
        this(empregado.getNome(), empregado.getEndereco(), salarioPorHora);
        this.setMembroSindicato(empregado.getMembroSindicato());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
    }

    public void setSalarioMensal(float salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    public float getSalarioMensal(){
        return this.salarioMensal;
    }

}
