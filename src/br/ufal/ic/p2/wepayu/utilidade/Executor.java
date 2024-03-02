package br.ufal.ic.p2.wepayu.utilidade;

import br.ufal.ic.p2.wepayu.exception.CronologicaException;
import br.ufal.ic.p2.wepayu.exception.executor.DesfazerException;
import br.ufal.ic.p2.wepayu.exception.executor.EncerrarSistemaException;
import br.ufal.ic.p2.wepayu.exception.existe.AgendaNaoDisponivelException;
import br.ufal.ic.p2.wepayu.exception.existe.ExisteException;
import br.ufal.ic.p2.wepayu.exception.invalido.*;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoComissionadoException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoEhException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoHoristaException;
import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.models.agendas.*;
import br.ufal.ic.p2.wepayu.models.empregados.*;
import br.ufal.ic.p2.wepayu.models.empregados.pagamentos.*;
import br.ufal.ic.p2.wepayu.models.empregados.registros.*;
import br.ufal.ic.p2.wepayu.models.SistemaDeFolha;
import br.ufal.ic.p2.wepayu.utilidade.operacoes.*;
import br.ufal.ic.p2.wepayu.models.sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.models.sindicato.TaxaServico;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.UUID;

public class Executor {
    private SistemaDeFolha folha;
    private Stack<Operacao> undoStack;
    private Stack<Operacao> redoStack;

    public Executor() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        try {
            FileInputStream fis = new FileInputStream("data\\data.xml");
            XMLDecoder decoder = new XMLDecoder(fis);
            this.folha = (SistemaDeFolha) decoder.readObject();
            decoder.close();
            fis.close();
        }catch (Exception e){
            this.folha = new SistemaDeFolha();
        }
    }

    public void encerrarSistema(){
        try {
            FileOutputStream fos = new FileOutputStream("data\\data.xml");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            XMLEncoder xmlEncoder = new XMLEncoder(bos);
            xmlEncoder.writeObject(this.folha);
            xmlEncoder.close();
        } catch (Exception e){
            System.out.println("Você não deveria estar vendo isso, erro inesperado!");
        }
        this.folha = null;
        undoStack = null;
        redoStack = null;
    }

    public void zerarSistema() throws ExisteException, NuloException, NumericoException, IOException, NaoEhException {
        this.executar(new ZeraSistema(this.folha.getListaDeMembros(), this.folha.getEmpregados(), this.folha.getAgendas()));
    }

    public String getAtributoEmpregado(String emp, String atributoString) throws NuloException, ExisteException, NaoEhException {
        Empregado empregado = this.folha.getEmpregadoById(emp);
        Atributo atributo = Sanitation.getAtributo(atributoString);
        return empregado.getString(atributo);
    }

    public String getEmpregadoPorNome(String nome, String indice) throws NuloException, NaoEhException {
        return this.folha.getEmpregadoByName(nome, Integer.parseInt(indice));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String taxaDeComissao) throws NuloException, NumericoException, InvalidoException, ExisteException, IOException, NaoEhException {
        Empregado empregado = switch (tipo) {
            case "horista" -> new Horista(nome, endereco, salario, taxaDeComissao);
            case "assalariado" -> new Assalariado(nome, endereco, salario, taxaDeComissao);
            case "comissionado" -> new Comissionado(nome, endereco, salario, taxaDeComissao);
            default -> throw new TipoInvalidoException();
        };
        String emp = UUID.randomUUID().toString();
        this.executar(new CriaEmpregado(emp, empregado));
        return emp;
    }

    public void removerEmpregado(String emp) throws ExisteException, NuloException, NumericoException, IOException, NaoEhException {
        this.executar(new RemoveEmpregado(emp, this.folha.getEmpregadoById(emp)));
    }

    public void lanca(String emp, String data, String valor, String tipoString) throws NumericoException, NuloException, InvalidoException, ExisteException, IOException, NaoEhException {
        TipoLancamento tipo = TipoLancamento.valueOf(tipoString);
        Operacao operacao = switch (tipo){
            case cartao -> new LancaCartao(emp, new CartaoDePonto(valor, data));
            case venda -> new LancaVenda(emp, new ResultadoDeVenda(valor, data));
            case servico -> new LancaServico(emp, new TaxaServico(data, valor));
        };
        this.executar(operacao);
    }

    public String get(String emp, String dataInicialString, String dataFinalString, String tipoString) throws ExisteException, NuloException, CronologicaException, NaoEhException, InvalidoException {
        TipoGet tipo = TipoGet.valueOf(tipoString);
        LocalDate dataInicial = Sanitation.isValid(dataInicialString, TipoDate.DataInicial);
        LocalDate dataFinal = Sanitation.isValid(dataFinalString, TipoDate.DataFinal);
        Empregado empregado = this.folha.getEmpregadoById(emp);
        float valor =  switch(tipo) {
            case horasNormais, horasExtras -> empregado.getHoras(dataInicial, dataFinal, tipo);
            case vendasRealizadas -> empregado.getVendas(dataInicial, dataFinal);
            default -> empregado.getServico(dataInicial, dataFinal);
        };
        if(tipo == TipoGet.horasNormais || tipo == TipoGet.horasExtras)
            return Sanitation.toString(valor, TipoNumerico.hora);
        return Sanitation.toString(valor, TipoNumerico.valor);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String salario, String taxaSindical, String contaCorrente) throws InvalidoException, NaoEhException, ExisteException, NuloException, NumericoException, IOException {
        Atributo atributoEmpregado = Sanitation.getAtributo(atributo);
        Sanitation.notNull(valor, atributoEmpregado);
        Empregado empregado = this.folha.getEmpregadoById(emp);
        Operacao operacao = null;
        switch (atributoEmpregado){
            case agendaPagamento -> {
                if(salario == null && taxaSindical == null && contaCorrente == null){
                    if(! this.folha.agendaExiste(valor))
                        throw new AgendaNaoDisponivelException();
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getString(atributoEmpregado), valor);
                }
            }
            case nome, endereco, salario, comissao -> {
                if(salario == null && taxaSindical == null && contaCorrente == null)
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getString(atributoEmpregado), valor);
            }
            case tipo -> {
                if(contaCorrente == null){
                    Empregado novoEmpregado = empregado.mudaTipo(valor, salario, taxaSindical);
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado, novoEmpregado);
                }
            }
            case sindicalizado -> {
                if(salario == null && taxaSindical == null && contaCorrente == null && valor.equals("false")) {
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMembroSindicato(), null);
                } else if(contaCorrente == null && valor.equals("true")){
                    MembroSindicato membroNovo = this.folha.criaNovoMembroSindicato(salario, taxaSindical);
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMembroSindicato(), membroNovo);
                }
            }
            case metodoPagamento -> {
                if(salario == null && taxaSindical == null && contaCorrente == null){
                    MetodoPagamento pagamento = switch (valor){
                        case "correios" -> new Correios();
                        case "emMaos" -> new EmMaos();
                        case "banco" -> throw new TipoNaoAplicavelException();
                        default -> throw new MetodoPagamentoInvalidoException();
                    };
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMetodoPagamento(), pagamento);
                } else if(valor.equals("banco")){
                    Banco bancoNovo = new Banco(salario, taxaSindical, contaCorrente);
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMetodoPagamento(), bancoNovo);
                }
            }
        }
        if(operacao == null)
            throw new TipoInvalidoException();
        this.executar(operacao);
    }

    public void executar(Operacao operacao) throws IOException, NuloException, NaoEhException, NumericoException, ExisteException {
        String emp = operacao.getId();
        switch (operacao) {
            case ZeraSistema ignored -> {
                this.folha = new SistemaDeFolha();
                new File("data\\data.xml").delete();
            }
            case CriaEmpregado criaEmpregado -> this.folha.addEmpregado(emp, criaEmpregado.getEmpregado());
            case RemoveEmpregado ignored -> this.folha.removeEmpregado(emp);
            case RodaFolha rodaFolha -> {
                BufferedWriter writer = new BufferedWriter(new FileWriter(rodaFolha.getSaida()));
                writer.write(emp);
                writer.close();
            }
            case LancaCartao lancaCartao -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Horista horista;
                try{
                    horista = (Horista) empregado;
                } catch (Exception e){
                    throw new NaoHoristaException();
                }
                horista.addCartao(lancaCartao.getCartao());
            }
            case LancaVenda lancaVenda -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Comissionado comissionado;
                try{
                    comissionado = (Comissionado) empregado;
                } catch (Exception e){
                    throw new NaoComissionadoException();
                }
                comissionado.addVenda(lancaVenda.getVenda());
            }
            case LancaServico lancaServico ->{
                Sanitation.notNull(emp, Atributo.membro);
                Empregado empregado = this.folha.getEmpregadoIdPeloMembro(emp);
                empregado.addServico(lancaServico.getServico());
            }
            case AlteraEmpregado alteraEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Atributo atributo = alteraEmpregado.getAtributo();
                switch (atributo){
                    case nome, endereco, salario, comissao, metodoPagamento, agendaPagamento ->
                            empregado.setString(atributo, alteraEmpregado.getValorNovo());
                    case tipo -> this.folha.replace(emp, (Empregado) alteraEmpregado.getValorNovo());
                    case sindicalizado -> this.folha.addMembro(emp, (MembroSindicato) alteraEmpregado.getValorNovo());
                }
            }
            default -> {
            }
        }
        undoStack.push(operacao);
    }

    public String rodaFolha(String data, String saida) throws Exception {
        StringBuilder texto = null;
        if(saida != null)
            texto = new StringBuilder();
        LocalDate diaAtual = Sanitation.isValid(data, TipoDate.Data);
        LocalDate dataFinal = diaAtual.plusDays(1);
        LocalDate ultimoDiaUtil = diaAtual.with(TemporalAdjusters.lastDayOfMonth());
        int diasNoMes = ultimoDiaUtil.getDayOfMonth();
        if(ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY)
            ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
        else if(ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY)
            ultimoDiaUtil = ultimoDiaUtil.minusDays(2);
        int diaUltimoDiaUtil = ultimoDiaUtil.getDayOfMonth();
        DayOfWeek semana = diaAtual.getDayOfWeek();
        int dia = diaAtual.getDayOfMonth();
        float horistaHorasTotais = 0;
        float horistaExtrasTotais = 0;
        float horistaBrutoTotais = 0;
        float horistaDescontosTotais = 0;
        float horistaLiquidoTotais = 0;
        float assalariadoBrutoTotais = 0;
        float assalariadoDescontosTotais = 0;
        float assalariadoLiquidoTotais = 0;
        float comissionadoFixoTotais = 0;
        float comissionadoVendasTotais = 0;
        float comissionadoComissaoTotais = 0;
        float comissionadoBrutoTotais = 0;
        float comissionadoDescontosTotais = 0;
        float comissionadoLiquidoTotais = 0;
        float horas;
        float extra;
        float salarioBruto;
        float descontos;
        float salarioLiquido;
        float fixo;
        float vendas;
        float comissao;
        int between = 0;
        boolean seraPago;
        SortedSet<String> horistas = null;
        if(saida != null)
            horistas = new TreeSet<>();
        SortedSet<String> assalariados = null;
        if(saida != null)
            assalariados = new TreeSet<>();
        SortedSet<String> comissionados = null;
        if(saida != null)
            comissionados = new TreeSet<>();
        for(Empregado empregado : folha.getEmpregados().values()){
            seraPago = false;
            LocalDate contratacao;
            if(empregado.getDataContratacao() == null)
                contratacao = Sanitation.toDate("1/1/2000");
            else
                contratacao = Sanitation.toDate(empregado.getDataContratacao());
            if(! contratacao.isAfter(diaAtual)){
                AgendaDePagamento agendaDePagamento = this.folha.getAgendaByEmpregado(empregado);
                long periodicidade = 0;
                switch (agendaDePagamento) {
                    case Mensal mensal -> {
                        int diaPagamento = mensal.getDia() == 0 ? diaUltimoDiaUtil : mensal.getDia();
                        if (diaPagamento == dia) {
                            seraPago = true;
                            periodicidade = 4;
                            between = diasNoMes;
                        }
                    }
                    case Semanal semanal -> {
                        DayOfWeek dayOfWeek = semanal.getSemana();
                        if (dayOfWeek == semana) {
                            periodicidade = semanal.getPeriodicidade();
                            long diasContratado = ChronoUnit.DAYS.between(contratacao, dataFinal) - 1;
                            if (diasContratado % (7 * periodicidade) > 7 * (periodicidade - 1)) {
                                seraPago = true;
                                between = (int) (7 * periodicidade);
                            }
                        }
                    }
                    default ->
                            System.out.println("Você não deveria estar vendo isso...");
                }
                if(seraPago){
                    LocalDate dataInicial = dataFinal.minusDays(between);
                    descontos = empregado.getDivida();
                    if(empregado.getMembroSindicato() != null){
                        MembroSindicato membro = empregado.getMembroSindicato();
                        for(TaxaServico taxa : membro.getTaxas()) {
                            LocalDate dataTaxa = Sanitation.toDate(taxa.getData());
                            if (Sanitation.between(dataTaxa, dataInicial, dataFinal))
                                descontos += taxa.getValor();
                        }
                        descontos += membro.getTaxaSindical() * between;
                    }
                    String metodoPagamento = null;
                    if(saida != null)
                        metodoPagamento = switch (empregado.getMetodoPagamento()){
                            case EmMaos ignored -> "Em maos";
                            case Correios ignored -> String.format("Correios, %s", empregado.getEndereco());
                            case Banco banco -> String.format("%s, Ag. %s CC %s", banco.getBanco(), banco.getAgencia(), banco.getContaCorrente());
                            default -> "";
                        };
                    switch (empregado){
                        case Horista horista -> {
                            horas = 0;
                            extra = 0;
                            for(CartaoDePonto cartao : horista.getCartoes()){
                                LocalDate dataCartao = Sanitation.toDate(cartao.getData());
                                if(Sanitation.between(dataCartao, dataInicial, dataFinal)){
                                    horas += cartao.getHoras() > 8 ? 8 : cartao.getHoras();
                                    extra += cartao.getHoras() > 8 ? cartao.getHoras() - 8 : 0;
                                }
                            }
                            horistaHorasTotais += horas;
                            horistaExtrasTotais += extra;
                            salarioBruto = (horas + 1.5f * extra) * empregado.getSalario();
                            horistaBrutoTotais += salarioBruto;
                            if(descontos > salarioBruto){
                                if(saida != null) {
                                    empregado.setDivida(descontos - salarioBruto);
                                }
                                descontos = salarioBruto;
                            } else {
                                if(saida != null) {
                                    if (empregado.getDivida() > salarioBruto - descontos) {
                                        empregado.setDivida(descontos - salarioBruto);
                                    } else {
                                        empregado.setDivida(0);
                                    }
                                }
                            }
                            horistaDescontosTotais += descontos;
                            salarioLiquido = salarioBruto - descontos;
                            horistaLiquidoTotais += salarioLiquido;
                            if(saida != null)
                                horistas.add(
                                        String.format("%-36.36s %5.5s %5.5s %13.13s %9.9s %15.15s %-38.38s",
                                                empregado.getNome(),
                                                Sanitation.toString(horas, TipoNumerico.hora),
                                                Sanitation.toString(extra, TipoNumerico.hora),
                                                Sanitation.toString(salarioBruto, TipoNumerico.salario),
                                                Sanitation.toString(descontos, TipoNumerico.salario),
                                                Sanitation.toString(salarioLiquido, TipoNumerico.salario),
                                                metodoPagamento)
                                );
                        }
                        case Assalariado assalariado -> {
                            if(agendaDePagamento instanceof Semanal)
                                salarioBruto = (float) Math.floor(assalariado.getSalarioMensal() * periodicidade * 1200 / 52) / 100;
                            else
                                salarioBruto = assalariado.getSalarioMensal();
                            assalariadoBrutoTotais += salarioBruto;
                            if(descontos > salarioBruto){
                                if(saida != null)
                                    empregado.setDivida(descontos - salarioBruto);
                                descontos = salarioBruto;
                            } else {
                                if(saida != null) {
                                    if (empregado.getDivida() > salarioBruto - descontos) {
                                        empregado.setDivida(descontos - salarioBruto);
                                    } else {
                                        empregado.setDivida(0);
                                    }
                                }
                            }
                            assalariadoDescontosTotais += descontos;
                            salarioLiquido = salarioBruto - descontos;
                            assalariadoLiquidoTotais += salarioLiquido;
                            if(saida != null)
                                assalariados.add(
                                        String.format("%-48.48s %13.13s %9.9s %15.15s %-38.38s",
                                        empregado.getNome(),
                                        Sanitation.toString(salarioBruto, TipoNumerico.salario),
                                        Sanitation.toString(descontos, TipoNumerico.salario),
                                        Sanitation.toString(salarioLiquido, TipoNumerico.salario),
                                        metodoPagamento)
                                );
                        }
                        case Comissionado comissionado -> {
                            if(agendaDePagamento instanceof Semanal)
                                fixo = (float) Math.floor(comissionado.getSalario() * periodicidade * 1200 / 52) / 100.0f;
                            else
                                fixo = comissionado.getSalario();
                            comissionadoFixoTotais += fixo;
                            vendas = 0;
                            for(ResultadoDeVenda venda : comissionado.getVendas()){
                                LocalDate dataVendas = Sanitation.toDate(venda.getData());
                                if(Sanitation.between(dataVendas, dataInicial, dataFinal)){
                                    vendas += (float) Math.floor(venda.getValor() * 100) / 100;
                                }
                            }
                            comissionadoVendasTotais += vendas;
                            comissao = (float) Math.floor(vendas * comissionado.getTaxaDeComissao() * 100) / 100;
                            comissionadoComissaoTotais += comissao;
                            salarioBruto = fixo + comissao;
                            comissionadoBrutoTotais += salarioBruto;
                            if(descontos > salarioBruto){
                                if(saida != null)
                                    empregado.setDivida(descontos - salarioBruto);
                                descontos = salarioBruto;
                            } else {
                                if(saida != null) {
                                    if (empregado.getDivida() > salarioBruto - descontos) {
                                        empregado.setDivida(descontos - salarioBruto);
                                    } else {
                                        empregado.setDivida(0);
                                    }
                                }
                            }
                            comissionadoDescontosTotais += descontos;
                            salarioLiquido = salarioBruto - descontos;
                            comissionadoLiquidoTotais += salarioLiquido;
                            if(saida != null)
                                comissionados.add(
                                        String.format("%-21.21s %8.8s %8.8s %8.8s %13.13s %9.9s %15.15s %-38.38s",
                                                empregado.getNome(),
                                                Sanitation.toString(fixo, TipoNumerico.salario),
                                                Sanitation.toString(vendas, TipoNumerico.salario),
                                                Sanitation.toString(comissao, TipoNumerico.salario),
                                                Sanitation.toString(salarioBruto, TipoNumerico.salario),
                                                Sanitation.toString(descontos, TipoNumerico.salario),
                                                Sanitation.toString(salarioLiquido, TipoNumerico.salario),
                                                metodoPagamento)
                                );
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + empregado);
                    }
                }
            }
        }

        float totalBruto = horistaBrutoTotais + assalariadoBrutoTotais + comissionadoBrutoTotais;
        if(saida == null)
            return Sanitation.toString(totalBruto, TipoNumerico.salario);

        texto.append("FOLHA DE PAGAMENTO DO DIA ").append(Sanitation.toString(diaAtual, TipoNumerico.dataFile));
        texto.append("\n====================================\n\n");
        texto.append("===============================================================================================================================\n");
        texto.append("===================== HORISTAS ================================================================================================\n");
        texto.append("===============================================================================================================================\n");
        texto.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        texto.append("==================================== ===== ===== ============= ========= =============== ======================================\n");
        for(String linha : horistas)
            texto.append(linha).append("\n");
        texto.append(String.format("\n%-36.36s %5.5s %5.5s %13.13s %9.9s %15.15s\n\n",
                "TOTAL HORISTAS",
                Sanitation.toString(horistaHorasTotais, TipoNumerico.hora),
                Sanitation.toString(horistaExtrasTotais, TipoNumerico.hora),
                Sanitation.toString(horistaBrutoTotais, TipoNumerico.salario),
                Sanitation.toString(horistaDescontosTotais, TipoNumerico.salario),
                Sanitation.toString(horistaLiquidoTotais, TipoNumerico.salario)));

        texto.append("===============================================================================================================================\n");
        texto.append("===================== ASSALARIADOS ============================================================================================\n");
        texto.append("===============================================================================================================================\n");
        texto.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        texto.append("================================================ ============= ========= =============== ======================================\n");
        for(String linha : assalariados)
            texto.append(linha).append("\n");
        texto.append(String.format("\n%-48.48s %13.13s %9.9s %15.15s\n\n",
                "TOTAL ASSALARIADOS",
                Sanitation.toString(assalariadoBrutoTotais, TipoNumerico.salario),
                Sanitation.toString(assalariadoDescontosTotais, TipoNumerico.salario),
                Sanitation.toString(assalariadoLiquidoTotais, TipoNumerico.salario)));
        texto.append("===============================================================================================================================\n");
        texto.append("===================== COMISSIONADOS ===========================================================================================\n");
        texto.append("===============================================================================================================================\n");
        texto.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
        texto.append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");
        for(String linha : comissionados)
            texto.append(linha).append("\n");
        texto.append(String.format("\n%-21.21s %8.8s %8.8s %8.8s %13.13s %9.9s %15.15s\n\n",
                "TOTAL COMISSIONADOS",
                Sanitation.toString(comissionadoFixoTotais, TipoNumerico.salario),
                Sanitation.toString(comissionadoVendasTotais, TipoNumerico.salario),
                Sanitation.toString(comissionadoComissaoTotais, TipoNumerico.salario),
                Sanitation.toString(comissionadoBrutoTotais, TipoNumerico.salario),
                Sanitation.toString(comissionadoDescontosTotais, TipoNumerico.salario),
                Sanitation.toString(comissionadoLiquidoTotais, TipoNumerico.salario)));
        texto.append("TOTAL FOLHA: ").append(Sanitation.toString(totalBruto, TipoNumerico.salario)).append("\n");
        this.executar(new RodaFolha(texto.toString(), saida));
        return "";
    }

    public void undo() throws Exception {
        if(redoStack == null)
            throw new EncerrarSistemaException();
        if(undoStack.isEmpty())
            throw new DesfazerException();
        Operacao operacao = undoStack.pop();
        String emp = operacao.getId();
        switch (operacao) {
            case ZeraSistema zeraSistema -> {
                this.folha.setEmpregados(zeraSistema.getEmpregados());
                this.folha.setListaDeMembros(zeraSistema.getListaDeMembros());
                this.folha.setAgendas(zeraSistema.getAgendas());
            }
            case RodaFolha rodaFolha -> new File(rodaFolha.getSaida()).delete();
            case CriaEmpregado ignored -> this.folha.removeEmpregado(emp);
            case RemoveEmpregado removeEmpregado -> this.folha.addEmpregado(emp, removeEmpregado.getEmpregado());
            case LancaCartao lancaCartao -> {
                Horista horista = (Horista) this.folha.getEmpregadoById(emp);
                horista.removeCartao(lancaCartao.getCartao());
            }
            case LancaVenda lancaVenda -> {
                Comissionado comissionado = (Comissionado) this.folha.getEmpregadoById(emp);
                comissionado.removeVenda(lancaVenda.getVenda());
            }
            case LancaServico lancaServico ->{
                Empregado empregado = this.folha.getEmpregadoIdPeloMembro(emp);
                empregado.removeServico(lancaServico.getServico());
            }
            case AlteraEmpregado alteraEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Atributo atributo = alteraEmpregado.getAtributo();
                switch (atributo){
                    case nome, endereco, salario, comissao, metodoPagamento, agendaPagamento ->
                            empregado.setString(atributo, alteraEmpregado.getValorAntigo());
                    case tipo -> this.folha.replace(emp, (Empregado) alteraEmpregado.getValorAntigo());
                    case sindicalizado -> this.folha.addMembro(emp, (MembroSindicato) alteraEmpregado.getValorAntigo());
                }
            }
            default -> {
            }
        }
        redoStack.push(operacao);
    }

    public void redo() throws Exception {
        if(redoStack == null)
            throw new EncerrarSistemaException();
        if(redoStack.isEmpty())
            throw new DesfazerException();
        this.executar(redoStack.pop());
    }

    public String getNumeroDeEmpregados(){
        return Integer.toString(this.folha.getEmpregados().size());
    }

    public void criaAgendaPagamentos(String descricao) throws ExisteException, InvalidoException {
        this.folha.criaAgenda(descricao);
    }
    
}
