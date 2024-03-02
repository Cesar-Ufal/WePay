package br.ufal.ic.p2.wepayu.models.empregados;

import br.ufal.ic.p2.wepayu.exception.invalido.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.exception.invalido.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoEhException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.utilidade.Atributo;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;

public class Assalariado extends Empregado implements Serializable{
    private float salarioMensal;

    public Assalariado(String nome, String endereco, String salarioMensal, String taxaDeComissao) throws NuloException, NumericoException, TipoNaoAplicavelException {
        super(nome, endereco);
        if(taxaDeComissao != null)
            throw new TipoNaoAplicavelException();
        this.setSalario(salarioMensal);
        this.setDataContratacao("1/1/2005");
        this.setTipo("assalariado");
        this.setAgenda("mensal $");
    }

    public Assalariado(String nome, String endereco, float salarioMensal) throws NuloException, NumericoException {
        super(nome, endereco);
        this.setSalarioMensal(salarioMensal);
        this.setDataContratacao("1/1/2005");
        this.setTipo("assalariado");
        this.setAgenda("mensal $");
    }

    public Assalariado() {

    }

    public Assalariado(Empregado empregado, String salario, String taxaDeComissao) throws NuloException, NumericoException, TipoInvalidoException, NaoEhException {
        super(empregado.getNome(), empregado.getEndereco());
        if(taxaDeComissao != null)
            throw new TipoInvalidoException();
        if(salario == null)
            this.setSalario(empregado.getString(Atributo.salario));
        else
            this.setSalario(salario);
        this.setMembroSindicato(empregado.getMembroSindicato());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
        this.setDataContratacao(empregado.getDataContratacao());
        this.setDivida(empregado.getDivida());
        this.setTipo("assalariado");
        this.setAgenda("mensal $");
    }

    public void setSalarioMensal(float salarioMensal) throws NuloException, NumericoException {
        Sanitation.numberValid(salarioMensal, TipoNumerico.salario);
        this.salarioMensal = salarioMensal;
    }

    public void setSalario(float salario) throws NuloException, NumericoException {
        this.setSalarioMensal(salario);
    }

    public void setSalario(String salario) throws NuloException, NumericoException {
        Sanitation.notNull(salario, Atributo.salario);
        float salarioMensal = Sanitation.toFloat(salario, TipoNumerico.salario);
        this.setSalarioMensal(salarioMensal);
    }

    public float getSalarioMensal(){
        return this.salarioMensal;
    }

    public float getSalario() {
        return this.getSalarioMensal();
    }

}
