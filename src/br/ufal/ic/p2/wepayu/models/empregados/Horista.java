package br.ufal.ic.p2.wepayu.models.empregados;

import br.ufal.ic.p2.wepayu.exception.CronologicaException;
import br.ufal.ic.p2.wepayu.exception.invalido.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.exception.invalido.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoEhException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.models.empregados.registros.CartaoDePonto;
import br.ufal.ic.p2.wepayu.utilidade.Atributo;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoGet;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Horista extends Empregado implements Serializable{
    private float salarioPorHora;
    private ArrayList<CartaoDePonto> cartoes;

    public Horista(String nome, String endereco, String salarioPorHora, String taxaDeComissaoString) throws NuloException, TipoNaoAplicavelException, NumericoException {
        super(nome, endereco);
        if(taxaDeComissaoString != null)
            throw new TipoNaoAplicavelException();
        this.setSalario(salarioPorHora);
        this.setCartoes(new ArrayList<>());
        this.setTipo("horista");
        this.setDataContratacao(null);
        this.setAgenda("semanal 5");
    }

    public Horista(String nome, String endereco, float salarioPorHora) throws NuloException, NumericoException {
        super(nome, endereco);
        this.setSalarioPorHora(salarioPorHora);
        this.setCartoes(new ArrayList<>());
        this.setTipo("horista");
        this.setDataContratacao(null);
        this.setAgenda("semanal 5");
    }

    public Horista(Empregado empregado, String salario, String taxaDeComissao) throws NuloException, NumericoException, NaoEhException, TipoInvalidoException {
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
        this.setCartoes(new ArrayList<>());
        this.setTipo("horista");
        this.setAgenda("semanal 5");
    }

    public Horista() {

    }

    public void setSalarioPorHora(float salarioPorHora) throws NuloException, NumericoException {
        Sanitation.numberValid(salarioPorHora, TipoNumerico.salario);
        this.salarioPorHora = salarioPorHora;
    }

    public void setSalario(float salario) throws NuloException, NumericoException {
        this.setSalarioPorHora(salario);
    }

    public void setSalario(String salario) throws NuloException, NumericoException {
        Sanitation.notNull(salario, Atributo.salario);
        float salarioPorHora = Sanitation.toFloat(salario, TipoNumerico.salario);
        this.setSalarioPorHora(salarioPorHora);
    }

    @Override
    public float getHoras(LocalDate dataInicial, LocalDate dataFinal, TipoGet tipo) throws CronologicaException {
        Sanitation.ordemCronologica(dataInicial, dataFinal);
        float horas = 0;
        for (CartaoDePonto cartao : this.getCartoes()) {
            LocalDate dataCartao = Sanitation.toDate(cartao.getData());
            if (Sanitation.between(dataCartao, dataInicial, dataFinal)) {
                if (tipo == TipoGet.horasNormais)
                    horas += cartao.getHoras() > 8 ? 8 : cartao.getHoras();
                else
                    horas += cartao.getHoras() > 8 ? cartao.getHoras() - 8 : 0;
            }
        }
        return horas;
    }

    public float getSalarioPorHora(){
        return this.salarioPorHora;
    }

    public float getSalario() {
        return this.getSalarioPorHora();
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
