package br.ufal.ic.p2.wepayu.models.Empregados;

import br.ufal.ic.p2.wepayu.models.Empregados.Registros.CartaoDePonto;

import java.io.Serializable;
import java.util.ArrayList;

public class Horista extends Empregado implements Serializable{
    private float salarioPorHora;
    private ArrayList<CartaoDePonto> cartoes;

    public Horista(String nome, String endereco, float salarioPorHora) {
        super(nome, endereco);
        setSalarioPorHora(salarioPorHora);
        this.setCartoes(new ArrayList<>());
        this.setTipo("horista");
        this.setDataContratacao(null);
        this.setAgenda("semanal 5");
    }

    public Horista(Empregado empregado){
        this(empregado.getNome(), empregado.getEndereco(), empregado.getSalario());
        this.setMembroSindicato(empregado.getMembroSindicato());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
    }

    public Horista(Empregado empregado, float salarioPorHora){
        this(empregado.getNome(), empregado.getEndereco(), salarioPorHora);
        this.setMembroSindicato(empregado.getMembroSindicato());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
    }

    public Horista() {

    }

    public void setSalarioPorHora(float salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
    }

    public float getSalarioPorHora(){
        return this.salarioPorHora;
    }

    public ArrayList<CartaoDePonto> getCartoes() {
        return cartoes;
    }

    public void setCartoes(ArrayList<CartaoDePonto> cartoes) {
        this.cartoes = cartoes;
    }

    public void addCartao(CartaoDePonto cartao){
        if(cartoes.isEmpty())
            this.setDataContratacao(cartao.getData());
        this.cartoes.add(cartao);
    }

    public void removeCartao(CartaoDePonto cartao){
        this.cartoes.remove(cartao);
    }

}
