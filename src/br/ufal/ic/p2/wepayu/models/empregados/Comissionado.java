package br.ufal.ic.p2.wepayu.models.empregados;

import br.ufal.ic.p2.wepayu.exception.CronologicaException;
import br.ufal.ic.p2.wepayu.exception.invalido.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.exception.invalido.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoComissionadoException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoEhException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.models.empregados.registros.ResultadoDeVenda;
import br.ufal.ic.p2.wepayu.utilidade.Atributo;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoGet;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Comissionado extends Empregado implements Serializable{
    private float salarioMensal;
    private float taxaDeComissao;
    private ArrayList<ResultadoDeVenda> vendas;

    public Comissionado(String nome, String endereco, String salarioMensal, String taxaDeComissao) throws NuloException, NumericoException, TipoNaoAplicavelException {
        super(nome, endereco);
        if(taxaDeComissao == null)
            throw new TipoNaoAplicavelException();
        this.setSalario(salarioMensal);
        this.setTaxaDeComissao(taxaDeComissao);
        this.setVendas(new ArrayList<>());
        this.setDataContratacao("1/1/2005");
        this.setTipo("comissionado");
        this.setAgenda("semanal 2 5");
    }

    public Comissionado(String nome, String endereco, float salarioMensal, float taxaDeComissao) throws NuloException, NumericoException {
        super(nome, endereco);
        setSalarioMensal(salarioMensal);
        setTaxaDeComissao(taxaDeComissao);
        this.setVendas(new ArrayList<>());
        this.setDataContratacao("1/1/2005");
        this.setTipo("comissionado");
        this.setAgenda("semanal 2 5");
    }

    public Comissionado() {

    }

    public Comissionado(Empregado empregado, String salario, String taxaDeComissao) throws NuloException, NumericoException, TipoInvalidoException, NaoEhException {
        super(empregado.getNome(), empregado.getEndereco());
        if(salario == null)
            throw new TipoInvalidoException();
        if(taxaDeComissao == null){
            taxaDeComissao = salario;
            salario = null;
        }
        if(salario == null)
            this.setSalario(empregado.getString(Atributo.salario));
        else
            this.setSalario(salario);
        this.setTaxaDeComissao(taxaDeComissao);
        this.setMembroSindicato(empregado.getMembroSindicato());
        this.setMetodoPagamento(empregado.getMetodoPagamento());
        this.setDataContratacao(empregado.getDataContratacao());
        this.setDivida(empregado.getDivida());
        this.setVendas(new ArrayList<>());
        this.setTipo("comissionado");
        this.setAgenda("semanal 2 5");
    }

    public void setSalarioMensal(float salarioMensal) throws NuloException, NumericoException{
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

    public void setTaxaDeComissao(float taxaDeComissao) throws NumericoException, NuloException {
        Sanitation.numberValid(taxaDeComissao, TipoNumerico.comissao);
        this.taxaDeComissao = taxaDeComissao;
    }

    @Override
    public void setComissao(float taxaDeComissao) throws NumericoException, NuloException {
        this.setTaxaDeComissao(taxaDeComissao);
    }

    public void setTaxaDeComissao(String taxaDeComissaoString) throws NumericoException, NuloException {
        Sanitation.notNull(taxaDeComissaoString, Atributo.comissao);
        float taxaDeComissao = Sanitation.toFloat(taxaDeComissaoString, TipoNumerico.comissao);
        this.setTaxaDeComissao(taxaDeComissao);
    }

    public void setComissao(String taxaDeComissao) throws NumericoException, NuloException {
        this.setTaxaDeComissao(taxaDeComissao);
    }

    public float getTaxaDeComissao(){
        return this.taxaDeComissao;
    }

    @Override
    public float getTComissao() throws NaoEhException {
        return getTaxaDeComissao();
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

    @Override
    public float getVendas(LocalDate dataInicial, LocalDate dataFinal) throws CronologicaException {
        Sanitation.ordemCronologica(dataInicial, dataFinal);
        float vendas = 0;
        for (ResultadoDeVenda venda : this.getVendas()) {
            LocalDate dataCartao = Sanitation.toDate(venda.getData());
            if (Sanitation.between(dataCartao, dataInicial, dataFinal))
                vendas += venda.getValor();
        }
        return vendas;
    }

}
