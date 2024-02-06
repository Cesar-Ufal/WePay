package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utilidade.*;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Facade {
    private Executor executor;
    private final float taxaExtra = 1.5f;
    public Facade(){
        this.executor = new Executor();
    }

    public void zerarSistema() throws Exception {
        this.executor.executar(new ZeraSistema(null, null));
    }

    public void encerrarSistema()  {
        this.executor.encerrarSistema();
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        Sanitation.notNull(emp, Atributo.idEmpregado);
        return this.executor.getAtributoEmpregado(emp, Sanitation.getAtributo(atributo));
    }

    public String getEmpregadoPorNome(String nome, String indice) throws Exception{
        Sanitation.notNull(nome, Atributo.nome);
        return this.executor.getEmpregadoPorNome(nome, Integer.parseInt(indice));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        Sanitation.notNull(nome, Atributo.nome);
        Sanitation.notNull(endereco, Atributo.endereco);
        Sanitation.notNull(salario, Atributo.salario);
        float salarioFloat = Sanitation.toFloat(salario, TipoNumerico.salario);
        Empregado empregado = switch (Sanitation.getTipo(tipo)) {
            case horista -> new Horista(nome, endereco, salarioFloat);
            case assalariado -> new Assalariado(nome, endereco, salarioFloat);
            default -> throw new TipoNaoAplicavel();
        };
        String emp = empregado.getUuid();
        this.executor.executar(new CriaEmpregado(emp, empregado));
        return emp;
    }
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String taxaDeComissao) throws Exception {
        Sanitation.notNull(nome, Atributo.nome);
        Sanitation.notNull(endereco, Atributo.endereco);
        Sanitation.notNull(salario, Atributo.salario);
        Sanitation.notNull(taxaDeComissao, Atributo.comissao);
        float salarioFloat = Sanitation.toFloat(salario, TipoNumerico.salario);
        float comissaoFloat = Sanitation.toFloat(taxaDeComissao, TipoNumerico.comissao);
        if(Sanitation.getTipo(tipo) != Tipo.comissionado)
            throw new TipoNaoAplicavel();
        Empregado empregado =  new Comissionado(nome, endereco, salarioFloat, comissaoFloat);
        String emp = empregado.getUuid();
        this.executor.executar(new CriaEmpregado(emp, empregado));
        return emp;
    }
    public void removerEmpregado(String emp) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        this.executor.executar(new RemoveEmpregado(emp, null));
    }

    public void lancaCartao(String emp, String data, String hora) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Sanitation.isValid(data, TipoDate.Data);
        float horaFloat = Sanitation.toFloat(hora, TipoNumerico.hora);
        CartaoDePonto cartao = new CartaoDePonto(horaFloat, data);
        this.executor.executar(new LancaCartao(emp, cartao));
    }
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        LocalDate dataInic = Sanitation.isValid(dataInicial, TipoDate.DataInicial);
        LocalDate dataFin = Sanitation.isValid(dataFinal, TipoDate.DataFinal);
        Sanitation.ordemCronologica(dataInic, dataFin);
        return this.executor.getHoras(emp, dataInic, dataFin, true);
    }
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        LocalDate dataInic = Sanitation.isValid(dataInicial, TipoDate.DataInicial);
        LocalDate dataFin = Sanitation.isValid(dataFinal, TipoDate.DataFinal);
        Sanitation.ordemCronologica(dataInic, dataFin);
        return this.executor.getHoras(emp, dataInic, dataFin, false);
    }
    public void lancaVenda(String emp, String data, String valor) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Sanitation.isValid(data, TipoDate.Data);
        float valorFloat = Sanitation.toFloat(valor, TipoNumerico.valor);
        ResultadoDeVenda venda = new ResultadoDeVenda(valorFloat, data);
        this.executor.executar(new LancaVenda(emp, venda));
    }
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        LocalDate dataInic = Sanitation.isValid(dataInicial, TipoDate.DataInicial);
        LocalDate dataFin = Sanitation.isValid(dataFinal, TipoDate.DataFinal);
        Sanitation.ordemCronologica(dataInic, dataFin);
        return this.executor.getVendas(emp, dataInic, dataFin);
    }
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Atributo atributoEmpregado = Sanitation.getAtributo(atributo);
        if(! (atributoEmpregado == Atributo.sindicalizado && Sanitation.toBool(valor)))
            throw new TipoNaoAplicavel();
        Sanitation.notNull(idSindicato, Atributo.idSindicato);
        Sanitation.notNull(taxaSindical, Atributo.taxaSindical);
        float taxaFloat = Sanitation.toFloat(taxaSindical, TipoNumerico.taxaSindical);
        MembroSindicato membro = new MembroSindicato(idSindicato, taxaFloat);
        executor.executar(new AlteraEmpregado(emp, atributoEmpregado, null, membro));
    }
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Atributo atributoEmpregado = Sanitation.getAtributo(atributo);
        if(! (atributoEmpregado == Atributo.metodoPagamento && valor1.equals("banco")))
            throw new TipoNaoAplicavel();
        Sanitation.notNull(banco, Atributo.banco);
        Sanitation.notNull(agencia, Atributo.agencia);
        Sanitation.notNull(contaCorrente, Atributo.contaCorrente);
        Banco bancoNovo = new Banco(banco, agencia, contaCorrente);
        this.executor.executar(new AlteraEmpregado(emp, atributoEmpregado, null, bancoNovo));
    }
    public void alteraEmpregado(String emp, String atributo, String valor, String salarioComissao) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Atributo atributoEmpregado = Sanitation.getAtributo(atributo);
        if(atributoEmpregado != Atributo.tipo)
            throw new TipoNaoAplicavel();
        this.executor.executar(new AlteraEmpregado(emp, atributoEmpregado, salarioComissao, valor));
    }
    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Atributo atributoEmpregado = Sanitation.getAtributo(atributo);
        Sanitation.notNull(valor, atributoEmpregado);
        this.executor.executar(new AlteraEmpregado(emp, atributoEmpregado, null, valor));
    }
    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        Sanitation.notNull(membro, Atributo.membro);
        Sanitation.isValid(data, TipoDate.Data);
        float valorFloat = Sanitation.toFloat(valor, TipoNumerico.valor);
        TaxaServico servico = new TaxaServico(data, valorFloat);
        this.executor.executar(new LancaServico(membro, servico));
    }
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        LocalDate dataInic = Sanitation.isValid(dataInicial, TipoDate.DataInicial);
        LocalDate dataFin = Sanitation.isValid(dataFinal, TipoDate.DataFinal);
        Sanitation.ordemCronologica(dataInic, dataFin);
        return this.executor.getServico(emp, dataInic, dataFin);
    }

    public String totalFolha(String data) throws Exception{
        String texto = this.executor.rodaFolha(data);
        String afterTexto = "TOTAL FOLHA: ";
        int indice = texto.indexOf(afterTexto);
        return texto.substring(indice + afterTexto.length()).replace("\n", "");
    }
    public void rodaFolha(String data, String saida) throws Exception {
        this.executor.executar(new RodaFolha(data, saida));

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
