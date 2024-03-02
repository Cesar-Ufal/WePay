package br.ufal.ic.p2.wepayu.models.empregados.pagamentos;

import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.utilidade.Atributo;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;

public class Banco extends MetodoPagamento{
    private String banco;
    private String agencia;
    private String contaCorrente;

    public Banco(String banco, String agencia, String contaCorrente) throws NuloException {
        this.setMetodo("banco");
        this.setBanco(banco);
        this.setAgencia(agencia);
        this.setContaCorrente(contaCorrente);
    }
    public Banco(){

    }

    @Override
    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) throws NuloException {
        Sanitation.notNull(banco, Atributo.banco);
        this.banco = banco;
    }

    @Override
    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) throws NuloException {
        Sanitation.notNull(agencia, Atributo.agencia);
        this.agencia = agencia;
    }

    @Override
    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) throws NuloException {
        Sanitation.notNull(contaCorrente, Atributo.contaCorrente);
        this.contaCorrente = contaCorrente;
    }
}
