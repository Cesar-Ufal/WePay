package br.ufal.ic.p2.wepayu.models.Empregados.Pagamentos;

public class Banco extends MetodoPagamento{
    private String banco;
    private String agencia;
    private String contaCorrente;

    public Banco(String banco, String agencia, String contaCorrente) {
        this.setMetodo("banco");
        this.setBanco(banco);
        this.setAgencia(agencia);
        this.setContaCorrente(contaCorrente);
    }
    public Banco(){

    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }
}
