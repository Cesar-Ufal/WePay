package br.ufal.ic.p2.wepayu.utilidade;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Agendas.*;
import br.ufal.ic.p2.wepayu.models.Empregados.*;
import br.ufal.ic.p2.wepayu.models.Empregados.Pagamentos.*;
import br.ufal.ic.p2.wepayu.models.Empregados.Registros.*;
import br.ufal.ic.p2.wepayu.models.SistemaDeFolha;
import br.ufal.ic.p2.wepayu.utilidade.Operacoes.*;
import br.ufal.ic.p2.wepayu.models.Sindicato.MembroSindicato;
import br.ufal.ic.p2.wepayu.models.Sindicato.TaxaServico;

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

    public void zerarSistema() throws Exception {
        this.executar(new ZeraSistema(this.folha.getListaDeMembros(), this.folha.getEmpregados(), this.folha.getAgendas()));
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Empregado empregado = this.folha.getEmpregadoById(emp);
        return switch (Sanitation.getAtributo(atributo)) {
            case nome -> empregado.getNome();
            case endereco -> empregado.getEndereco();
            case sindicalizado -> empregado.ehSindicalizado() ? "true" : "false";
            case tipo -> empregado.getTipo();
            case salario -> Sanitation.toString(empregado.getSalario(), TipoNumerico.salario);
            case comissao -> Sanitation.toString(empregado.getComissao(), TipoNumerico.comissao);
            case metodoPagamento -> empregado.getMetodo();
            case idSindicato -> empregado.getIdSindicato();
            case taxaSindical -> Sanitation.toString(empregado.getTaxaSindical(), TipoNumerico.taxaSindical);
            case banco -> empregado.getBanco();
            case agencia -> empregado.getAgencia();
            case contaCorrente -> empregado.getContaCorrente();
            case agendaPagamento -> empregado.getAgenda();
            default -> "";
        };
    }

    public String getEmpregadoPorNome(String nome, String indice) throws Exception {
        Sanitation.notNull(nome, Atributo.nome);
        return this.folha.getEmpregadoByName(nome, Integer.parseInt(indice));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String taxaDeComissao) throws Exception {
        Sanitation.notNull(nome, Atributo.nome);
        Sanitation.notNull(endereco, Atributo.endereco);
        Sanitation.notNull(salario, Atributo.salario);
        float salarioFloat = Sanitation.toFloat(salario, TipoNumerico.salario);
        Empregado empregado;
        if(taxaDeComissao != null) {
            Sanitation.notNull(taxaDeComissao, Atributo.comissao);
            float comissaoFloat = Sanitation.toFloat(taxaDeComissao, TipoNumerico.comissao);
            if(! tipo.equals("comissionado"))
                throw new TipoNaoAplicavel();
            empregado = new Comissionado(nome, endereco, salarioFloat, comissaoFloat);
        } else {
            empregado = switch (tipo) {
                case "horista" -> new Horista(nome, endereco, salarioFloat);
                case "assalariado" -> new Assalariado(nome, endereco, salarioFloat);
                case "comissionado" -> throw new TipoNaoAplicavel();
                default -> throw new TipoInvalido();
            };
        }
        String emp = UUID.randomUUID().toString();
        this.executar(new CriaEmpregado(emp, empregado));
        return emp;
    }

    public void removerEmpregado(String emp) throws Exception {
        Sanitation.notNull(emp, Atributo.idEmpregado);
        this.executar(new RemoveEmpregado(emp, this.folha.getEmpregadoById(emp)));
    }

    public void lanca(String id, String data, String valor, TipoLancamento tipo) throws Exception {
        Sanitation.isValid(data, TipoDate.Data);
        Operacao operacao;
        switch (tipo){
            case cartao -> {
                float valorFloat = Sanitation.toFloat(valor, TipoNumerico.hora);
                Sanitation.notNull(id, Atributo.idEmpregado);
                Empregado empregado = this.folha.getEmpregadoById(id);
                if (!(empregado instanceof Horista))
                    throw new NaoHorista();
                operacao = new LancaCartao(id, new CartaoDePonto(valorFloat, data));
            }
            case venda -> {
                float valorFloat = Sanitation.toFloat(valor, TipoNumerico.valor);
                Sanitation.notNull(id, Atributo.idEmpregado);
                Empregado empregado = this.folha.getEmpregadoById(id);
                if (!(empregado instanceof Comissionado))
                    throw new NaoComissionado();
                operacao = new LancaVenda(id, new ResultadoDeVenda(valorFloat, data));
            }
            case servico -> {
                float valorFloat = Sanitation.toFloat(valor, TipoNumerico.valor);
                Sanitation.notNull(id, Atributo.membro);
                id = this.folha.getEmpregadoIdPeloMembro(id);
                operacao = new LancaServico(id, new TaxaServico(data, valorFloat));
            }
            default -> throw new IllegalStateException("Unexpected value: " + tipo);
        }
        this.executar(operacao);
    }

    public String get(String emp, String dataInicialString, String dataFinalString, TipoGet tipo) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        LocalDate dataInicial = Sanitation.isValid(dataInicialString, TipoDate.DataInicial);
        LocalDate dataFinal = Sanitation.isValid(dataFinalString, TipoDate.DataFinal);
        Sanitation.ordemCronologica(dataInicial, dataFinal);
        Empregado empregado = this.folha.getEmpregadoById(emp);
        switch(tipo){
            case horasNormais, horasExtras -> {
                if(! (empregado instanceof Horista))
                    throw new NaoHorista();
                float horas = 0;
                for(CartaoDePonto cartao : ((Horista) empregado).getCartoes()) {
                    LocalDate dataCartao = Sanitation.toDate(cartao.getData());
                    if (Sanitation.between(dataCartao, dataInicial, dataFinal)) {
                        if (tipo == TipoGet.horasNormais)
                            horas += cartao.getHoras() > 8 ? 8 : cartao.getHoras();
                        else
                            horas += cartao.getHoras() > 8 ? cartao.getHoras() - 8 : 0;
                    }
                }
                return Sanitation.toString(horas, TipoNumerico.hora);
            }
            case vendasRealizadas -> {
                if(!(empregado instanceof Comissionado))
                    throw new NaoComissionado();
                float vendas = 0;
                for(ResultadoDeVenda venda : ((Comissionado) empregado).getVendas()) {
                    LocalDate dataCartao = Sanitation.toDate(venda.getData());
                    if (Sanitation.between(dataCartao, dataInicial, dataFinal))
                        vendas += venda.getValor();
                }
                return Sanitation.toString(vendas, TipoNumerico.valor);
            }
            case taxaSindicato -> {
                if(! empregado.ehSindicalizado())
                    throw new NaoSindicalizado();
                MembroSindicato membro = empregado.getMembroSindicato();
                float valor = 0;
                for(TaxaServico taxa : membro.getTaxas()){
                    LocalDate dataTaxa = Sanitation.toDate(taxa.getData());
                    if(Sanitation.between(dataTaxa, dataInicial, dataFinal))
                        valor += taxa.getValor();
                }
                return Sanitation.toString(valor, TipoNumerico.valor);
            }
            default -> throw new IllegalStateException("Unexpected value: " + tipo);
        }
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String salario, String taxaSindical, String contaCorrente) throws Exception{
        Sanitation.notNull(emp, Atributo.idEmpregado);
        Atributo atributoEmpregado = Sanitation.getAtributo(atributo);
        Sanitation.notNull(valor, atributoEmpregado);
        Empregado empregado = this.folha.getEmpregadoById(emp);
        Operacao operacao = null;
        switch (atributoEmpregado){
            case nome -> {
                if(salario == null && taxaSindical == null && contaCorrente == null)
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getNome(), valor);
            }
            case endereco -> {
                if(salario == null && taxaSindical == null && contaCorrente == null)
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getEndereco(), valor);
            }
            case tipo -> {
                if(taxaSindical == null && contaCorrente == null){
                    Empregado empregadoNovo;
                    float salarioFloat = -1;
                    if(salario != null)
                        salarioFloat = Sanitation.toFloat(salario, TipoNumerico.comissao);
                    switch (valor){
                        case "horista":
                            if(salarioFloat < 0)
                                empregadoNovo = new Horista(empregado);
                            else
                                empregadoNovo = new Horista(empregado, salarioFloat);
                            break;
                        case "assalariado":
                            if(salarioFloat < 0)
                                empregadoNovo = new Assalariado(empregado);
                            else
                                empregadoNovo = new Assalariado(empregado, salarioFloat);
                            break;
                        case "comissionado":
                            if(salarioFloat < 0)
                                throw new TipoInvalido();
                            else
                                empregadoNovo = new Comissionado(empregado, salarioFloat);
                            break;
                        default:
                            throw new TipoInvalido();
                   }
                   operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado, empregadoNovo);
                }
            }
            case salario -> {
                if(salario == null && taxaSindical == null && contaCorrente == null) {
                    float salarioFloat = Sanitation.toFloat(valor, TipoNumerico.salario);
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getSalario(), salarioFloat);
                }
            }
            case comissao -> {
                if(salario == null && taxaSindical == null && contaCorrente == null) {
                    float comissaoFloat = Sanitation.toFloat(valor, TipoNumerico.comissao);
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getComissao(), comissaoFloat);
                }
            }
            case sindicalizado -> {
                if(salario == null && taxaSindical == null && contaCorrente == null && valor.equals("false")) {
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMembroSindicato(), null);
                } else if(contaCorrente == null && valor.equals("true")){
                    Sanitation.notNull(salario, Atributo.idSindicato);
                    Sanitation.notNull(taxaSindical, Atributo.taxaSindical);
                    float taxa = Sanitation.toFloat(taxaSindical, TipoNumerico.taxaSindical);
                    MembroSindicato membro = this.folha.criaNovoMembroSindicato(salario, taxa);
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMembroSindicato(), membro);
                }
            }
            case metodoPagamento -> {
                if(salario == null && taxaSindical == null && contaCorrente == null){
                    MetodoPagamento pagamento = switch (valor){
                        case "correios" -> new Correios();
                        case "emMaos" -> new EmMaos();
                        case "banco" -> throw new TipoNaoAplicavel();
                        default -> throw new MetodoPagamentoInvalido();
                    };
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMetodoPagamento(), pagamento);
                } else if(valor.equals("banco")){
                    Sanitation.notNull(salario, Atributo.banco);
                    Sanitation.notNull(taxaSindical, Atributo.agencia);
                    Sanitation.notNull(contaCorrente, Atributo.contaCorrente);
                    Banco bancoNovo = new Banco(salario, taxaSindical, contaCorrente);
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getMetodoPagamento(), bancoNovo);
                }
            }
            case agendaPagamento -> {
                if(salario == null && taxaSindical == null && contaCorrente == null){
                    if(! this.folha.agendaExiste(valor))
                        throw new AgendaNaoDisponivel();
                    operacao = new AlteraEmpregado(emp, atributoEmpregado, empregado.getAgenda(), valor);
                }
            }
        }
        if(operacao == null)
            throw new TipoInvalido();
        this.executar(operacao);
    }

    public void executar(Operacao operacao) throws Exception {
        String emp = operacao.getId();
        switch (operacao) {
            case ZeraSistema ignored -> this.folha = new SistemaDeFolha();
            case CriaEmpregado criaEmpregado -> this.folha.addEmpregado(emp, criaEmpregado.getEmpregado());
            case RemoveEmpregado ignored -> this.folha.removeEmpregado(emp);
            case RodaFolha rodaFolha -> {
                BufferedWriter writer = new BufferedWriter(new FileWriter(rodaFolha.getSaida()));
                writer.write(emp);
                writer.close();
            }
            case LancaCartao lancaCartao -> {
                Horista horista = (Horista) this.folha.getEmpregadoById(emp);
                horista.addCartao(lancaCartao.getCartao());
            }
            case LancaVenda lancaVenda -> {
                Comissionado comissionado = (Comissionado) this.folha.getEmpregadoById(emp);
                comissionado.addVenda(lancaVenda.getVenda());
            }
            case LancaServico lancaServico ->{
                Empregado empregado = this.folha.getEmpregadoById(emp);
                empregado.addServico(lancaServico.getServico());
            }
            case AlteraEmpregado alteraEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Atributo atributo = alteraEmpregado.getAtributo();
                switch (atributo){
                    case nome -> empregado.setNome((String) alteraEmpregado.getValorNovo());
                    case endereco -> empregado.setEndereco((String) alteraEmpregado.getValorNovo());
                    case tipo -> this.folha.replace(emp, (Empregado) alteraEmpregado.getValorNovo());
                    case salario -> empregado.setSalario((float) alteraEmpregado.getValorNovo());
                    case comissao -> empregado.setTComissao((float) alteraEmpregado.getValorNovo());
                    case sindicalizado -> this.folha.addMembro(emp, (MembroSindicato) alteraEmpregado.getValorNovo());
                    case metodoPagamento -> empregado.setMetodoPagamento((MetodoPagamento) alteraEmpregado.getValorNovo());
                    case agendaPagamento -> empregado.setAgenda((String) alteraEmpregado.getValorNovo());
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
                contratacao = Sanitation.toDate("1/1/2005");
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
                            throw new IllegalStateException("Unexpected value: " + this.folha.getAgendaByEmpregado(empregado));
                }
                if(seraPago){
                    LocalDate dataInicial = dataFinal.minusDays(between);
                    descontos = 0;
                    if(empregado.ehSindicalizado()){
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
                            if(descontos > salarioBruto)
                                descontos = salarioBruto;
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
                            if(descontos > salarioBruto)
                                descontos = salarioBruto;
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
                            if(descontos > salarioBruto)
                                descontos = salarioBruto;
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
            throw new Exception("Nao pode dar comandos depois de encerrarSistema.");
        if(undoStack.isEmpty())
            throw new Exception("Nao ha comando a desfazer.");
        Operacao operacao = undoStack.pop();
        String emp = operacao.getId();
        switch (operacao) {
            case ZeraSistema zeraSistema -> {
                this.folha.setEmpregados(zeraSistema.getEmpregados());
                this.folha.setListaDeMembros(zeraSistema.getListaDeMembros());
                this.folha.setAgendas(zeraSistema.getAgendas());
            }
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
                Empregado empregado = this.folha.getEmpregadoById(emp);
                empregado.removeServico(lancaServico.getServico());
            }
            case AlteraEmpregado alteraEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Atributo atributo = alteraEmpregado.getAtributo();
                switch (atributo){
                    case nome -> empregado.setNome((String) alteraEmpregado.getValorAntigo());
                    case endereco -> empregado.setEndereco((String) alteraEmpregado.getValorAntigo());
                    case tipo -> this.folha.replace(emp, (Empregado) alteraEmpregado.getValorAntigo());
                    case salario -> empregado.setSalario((float) alteraEmpregado.getValorAntigo());
                    case comissao -> empregado.setTComissao((float) alteraEmpregado.getValorAntigo());
                    case sindicalizado -> this.folha.addMembro(emp, (MembroSindicato) alteraEmpregado.getValorAntigo());
                    case metodoPagamento -> empregado.setMetodoPagamento((MetodoPagamento) alteraEmpregado.getValorAntigo());
                    case agendaPagamento -> empregado.setAgenda((String) alteraEmpregado.getValorAntigo());
                }
            }
            default -> {
            }
        }
        redoStack.push(operacao);
    }

    public void redo() throws Exception {
        if(redoStack == null)
            throw new Exception("Nao pode dar comandos depois de encerrarSistema.");
        if(redoStack.isEmpty())
            throw new Exception("Nao ha comando a desfazer");
        this.executar(redoStack.pop());
    }

    public String getNumeroDeEmpregados(){
        return Integer.toString(this.folha.getEmpregados().size());
    }

    public void criaAgendaPagamentos(String descricao) throws DescricaoAgendaInvalida, AgendaJaExiste {
        this.folha.criaAgenda(descricao);
    }
}
