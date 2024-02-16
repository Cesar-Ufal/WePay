package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.utilidade.Executor;
import br.ufal.ic.p2.wepayu.utilidade.TipoGet;
import br.ufal.ic.p2.wepayu.utilidade.TipoLancamento;

public class Facade {

    private final Executor executor;

    public Facade(){
        this.executor = new Executor();
    }

    public void zerarSistema() throws Exception {
        this.executor.zerarSistema();
    }

    public void encerrarSistema() {
        this.executor.encerrarSistema();
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        return this.executor.getAtributoEmpregado(emp, atributo);
    }

    public String getEmpregadoPorNome(String nome, String indice) throws Exception{
        return this.executor.getEmpregadoPorNome(nome, indice);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        return this.executor.criarEmpregado(nome, endereco, tipo, salario, null);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String taxaDeComissao) throws Exception {
        return this.executor.criarEmpregado(nome, endereco, tipo, salario, taxaDeComissao);
    }

    public void removerEmpregado(String emp) throws Exception{
        this.executor.removerEmpregado(emp);
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception{
        return this.executor.get(emp, dataInicial, dataFinal, TipoGet.horasNormais);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception{
        return this.executor.get(emp, dataInicial, dataFinal, TipoGet.horasExtras);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception{
        return this.executor.get(emp, dataInicial, dataFinal, TipoGet.vendasRealizadas);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception{
        return this.executor.get(emp, dataInicial, dataFinal, TipoGet.taxaSindicato);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        this.executor.lanca(membro, data, valor, TipoLancamento.servico);
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception{
        this.executor.lanca(emp, data, valor, TipoLancamento.venda);
    }

    public void lancaCartao(String emp, String data, String hora) throws Exception{
        this.executor.lanca(emp, data, hora, TipoLancamento.cartao);
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception{
        this.executor.alteraEmpregado(emp, atributo, valor, null, null,null);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String salarioComissao) throws Exception{
        this.executor.alteraEmpregado(emp, atributo, valor, salarioComissao, null, null);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception{
        this.executor.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical, null);
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception{
        this.executor.alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente);
    }

    public String totalFolha(String data) throws Exception{
        return this.executor.rodaFolha(data, null);
    }

    public void rodaFolha(String data, String saida) throws Exception {
        this.executor.rodaFolha(data, saida);
    }

    public void undo() throws Exception {
        this.executor.undo();
    }

    public void redo() throws Exception {
        this.executor.redo();
    }

    public String getNumeroDeEmpregados(){
        return this.executor.getNumeroDeEmpregados();
    }

    public void criarAgendaDePagamentos(String descricao) throws DescricaoAgendaInvalida, AgendaJaExiste {
        this.executor.criaAgendaPagamentos(descricao);
    }

}
