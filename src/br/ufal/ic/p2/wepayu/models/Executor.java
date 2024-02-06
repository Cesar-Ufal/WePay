package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.utilidade.*;

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

    public String getAtributoEmpregado(String emp, Atributo atributo) throws Exception {
        Empregado empregado = this.folha.getEmpregadoById(emp);
        return switch (atributo) {
            case nome -> empregado.getNome();
            case endereco -> empregado.getEndereco();
            case sindicalizado -> empregado.ehSindicalizado() ? "true" : "false";
            case tipo -> empregado.getTipo();
            case salario -> Sanitation.toString(empregado.getSalario(), TipoNumerico.salario);
            case comissao -> Sanitation.toString(empregado.getComissao(), TipoNumerico.comissao);
            case metodoPagamento -> empregado.getMetodoPagamento().getMetodo();
            case idSindicato -> folha.getMembroById(empregado.getIdMembro()).getIdMembro();
            case taxaSindical ->
                    Sanitation.toString(folha.getMembroById(empregado.getIdMembro()).getTaxaSindical(), TipoNumerico.taxaSindical);
            case banco -> empregado.getBanco();
            case agencia -> empregado.getAgencia();
            case contaCorrente -> empregado.getContaCorrente();
            case agendaPagamento -> empregado.getAgenda();
            default -> "";
        };
    }

    public String getEmpregadoPorNome(String nome, int indice) throws NaoEmpregadoNome {
        return this.folha.getEmpregadoByName(nome, indice);
    }
    public void executar(Operacao operacao) throws Exception {
        String emp = operacao.getId();
        switch (operacao) {
            case ZeraSistema zeraSistema -> {
                zeraSistema.setEmpregados(this.folha.getEmpregados());
                zeraSistema.setListaDeMembros(this.folha.getListaDeMembros());
                this.folha = new SistemaDeFolha();
            }
            case CriaEmpregado criaEmpregado -> this.folha.add(criaEmpregado.getEmpregado());
            case RemoveEmpregado removeEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                removeEmpregado.setEmpregado(empregado);
                this.folha.remove(emp);
            }
            case LancaCartao lancaCartao -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                CartaoDePonto cartao = lancaCartao.getCartao();
                if (!(empregado instanceof Horista))
                    throw new NaoHorista();
                ((Horista) empregado).addCartao(cartao);
            }
            case LancaVenda lancaVenda -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                ResultadoDeVenda venda = lancaVenda.getVenda();
                if (!(empregado instanceof Comissionado))
                    throw new NaoComissionado();
                ((Comissionado) empregado).addVenda(venda);
            }
            case LancaServico lancaServico ->{
                MembroSindicato membro = this.folha.getMembroById(emp);
                membro.addTaxa(lancaServico.getServico());
            }
            case AlteraEmpregado alteraEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Atributo atributo = alteraEmpregado.getAtributo();
                switch (atributo){
                    case nome -> {
                        alteraEmpregado.setValorAntigo(empregado.getNome());
                        String novoNome = (String) alteraEmpregado.getValorNovo();
                        empregado.setNome(novoNome);
                    }
                    case endereco -> {
                        alteraEmpregado.setValorAntigo(empregado.getEndereco());
                        String novoEndereco = (String) alteraEmpregado.getValorNovo();
                        empregado.setEndereco(novoEndereco);
                    }
                    case tipo -> {
                        Tipo tipo = Sanitation.getTipo((String) alteraEmpregado.getValorNovo());
                        switch (tipo){
                            case horista -> {
                                Horista horista = new Horista(empregado);
                                if(alteraEmpregado.getValorAntigo() != null){
                                    float salario = Sanitation.toFloat((String) alteraEmpregado.getValorAntigo(), TipoNumerico.salario);
                                    horista.setSalarioPorHora(salario);
                                }
                                alteraEmpregado.setValorNovo(horista);
                                this.folha.replace(empregado, horista);
                            }
                            case comissionado -> {
                                float comissao = Sanitation.toFloat((String) alteraEmpregado.getValorAntigo(), TipoNumerico.comissao);
                                Comissionado comissionado = new Comissionado(empregado, comissao);
                                this.folha.replace(empregado, comissionado);
                            }
                            case assalariado -> {
                                Assalariado assalariado = new Assalariado(empregado);
                                if(alteraEmpregado.getValorAntigo() != null){
                                    float salario = Sanitation.toFloat((String) alteraEmpregado.getValorAntigo(), TipoNumerico.salario);
                                    assalariado.setSalarioMensal(salario);
                                }
                                alteraEmpregado.setValorNovo(assalariado);
                                this.folha.replace(empregado, assalariado);
                            }
                        }
                        alteraEmpregado.setValorAntigo(empregado);
                    }
                    case salario -> {
                        alteraEmpregado.setValorAntigo(empregado.getSalario());
                        float novoSalario = Sanitation.toFloat((String) alteraEmpregado.getValorNovo(), TipoNumerico.salario);
                        empregado.setSalario(novoSalario);
                    }
                    case comissao -> {
                        if(!(empregado instanceof Comissionado))
                            throw new NaoComissionado();
                        alteraEmpregado.setValorAntigo(((Comissionado) empregado).getTaxaDeComissao());
                        float novaComissao = Sanitation.toFloat((String) alteraEmpregado.getValorNovo(), TipoNumerico.comissao);
                        ((Comissionado) empregado).setTaxaDeComissao(novaComissao);
                    }
                    case sindicalizado -> {
                        String idMembroAntigo = empregado.getIdMembro();
                        MembroSindicato membroAntigo = null;
                        if(idMembroAntigo != null && this.folha.membroExiste(idMembroAntigo))
                            membroAntigo = this.folha.getMembroById(idMembroAntigo);
                        alteraEmpregado.setValorAntigo(membroAntigo);
                        if(alteraEmpregado.getValorNovo() instanceof MembroSindicato membroNovo) {
                            if (this.folha.membroExiste(membroNovo.getIdMembro()))
                                throw new MesmaIdentificacaoSindical();
                            this.folha.addMembro(membroNovo);
                            empregado.setIdMembro(membroNovo.getIdMembro());
                        }else {
                            empregado.setIdMembro(null);
                        }
                        if(idMembroAntigo != null)
                            this.folha.removeMembro(idMembroAntigo);
                    }
                    case taxaSindical -> {
                    }
                    case metodoPagamento -> {
                        alteraEmpregado.setValorAntigo(empregado.getMetodoPagamento());
                        if(alteraEmpregado.getValorNovo() instanceof MetodoPagamento metodoNovo) {
                            empregado.setMetodoPagamento(metodoNovo);
                        }else{
                            switch ((String) alteraEmpregado.getValorNovo()){
                                case "correios" -> empregado.setMetodoPagamento(new Correios());
                                case "emMaos" -> empregado.setMetodoPagamento(new EmMaos());
                                case "bancos" -> throw new TipoNaoAplicavel();
                                default -> throw new MetodoPagamentoInvalido();
                            }
                            alteraEmpregado.setValorNovo(empregado.getMetodoPagamento());
                        }
                    }
                    case agendaPagamento -> {
                        String agendaNova = (String) alteraEmpregado.getValorNovo();
                        if(! this.folha.agendaExiste(agendaNova))
                            throw new AgendaNaoDisponivel();
                        alteraEmpregado.setValorAntigo(empregado.getAgenda());
                        empregado.setAgenda(agendaNova);
                    }
                }
            }
            default -> {

            }
        }
        undoStack.push(operacao);
    }

    public String getHoras(String emp, LocalDate dataInicial, LocalDate dataFinal, boolean normal) throws Exception{
        Empregado empregado = this.folha.getEmpregadoById(emp);
        if(!(empregado instanceof Horista))
            throw new NaoHorista();
        float horas = 0;
        for(CartaoDePonto cartao : ((Horista) empregado).getCartoes()) {
            LocalDate dataCartao = Sanitation.toDate(cartao.getData());
            if (Sanitation.between(dataCartao, dataInicial, dataFinal)) {
                if (normal)
                    horas += cartao.getHoras() > 8 ? 8 : cartao.getHoras();
                else
                    horas += cartao.getHoras() > 8 ? cartao.getHoras() - 8 : 0;
            }
        }
        return Sanitation.toString(horas, TipoNumerico.hora);
    }

    public String getVendas(String emp, LocalDate dataInicial, LocalDate dataFinal) throws Exception{
        Empregado empregado = this.folha.getEmpregadoById(emp);
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

    public String getServico(String emp, LocalDate dataInicial, LocalDate dataFinal) throws Exception {
        Empregado empregado = this.folha.getEmpregadoById(emp);
        MembroSindicato membro = this.folha.getMembroById(empregado.getIdMembro());
        float valor = 0;
        for(TaxaServico taxa : membro.getTaxas()){
            LocalDate dataTaxa = Sanitation.toDate(taxa.getData());
            if(Sanitation.between(dataTaxa, dataInicial, dataFinal))
                valor += taxa.getValor();
        }
        return Sanitation.toString(valor, TipoNumerico.valor);
    }

    public String rodaFolha(String data) throws Exception {
        StringBuilder texto = new StringBuilder();
        LocalDate diaAtual = Sanitation.isValid(data, TipoDate.Data);
        LocalDate dataFinal = diaAtual.plusDays(1);
        LocalDate ultimoDiaUtil = diaAtual.with(TemporalAdjusters.lastDayOfMonth());
        int diasNoMes = ultimoDiaUtil.getDayOfMonth();
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
        SortedSet<String> horistas = new TreeSet<>();
        SortedSet<String> assalariados = new TreeSet<>();
        SortedSet<String> comissionados = new TreeSet<>();
        if(ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY)
            ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
        else if(ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY)
            ultimoDiaUtil = ultimoDiaUtil.minusDays(2);
        for(Empregado empregado : folha.getEmpregados().values()){
            seraPago = false;
            LocalDate contratacao;
            if(empregado.getDataContratacao() == null)
                contratacao = Sanitation.toDate(Sanitation.incialDate);
            else
                contratacao = Sanitation.toDate(empregado.getDataContratacao());
            if(! contratacao.isAfter(diaAtual)){
                AgendaDePagamento agendaDePagamento = this.folha.getAgendaByEmpregado(empregado);
                long periodicidade = 0;
                switch (agendaDePagamento) {
                    case Mensal mensal -> {
                        int diaPagamento = mensal.getDia() == 0 ? diasNoMes : mensal.getDia();
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
                        MembroSindicato membro = folha.getMembroById(empregado.getIdMembro());
                        for(TaxaServico taxa : membro.getTaxas()) {
                            LocalDate dataTaxa = Sanitation.toDate(taxa.getData());
                            if (Sanitation.between(dataTaxa, dataInicial, dataFinal))
                                descontos += taxa.getValor();
                        }
                        descontos += membro.getTaxaSindical() * between;
                    }
                    String metodoPagamento = switch (empregado.getMetodoPagamento()){
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
                                fixo = (float) Math.floor(empregado.getSalario() * periodicidade * 1200 / 52) / 100.0f;
                            else
                                fixo = empregado.getSalario();
                            comissionadoFixoTotais += fixo;
                            vendas = 0;
                            for(ResultadoDeVenda venda : ((Comissionado) empregado).getVendas()){
                                LocalDate dataVendas = Sanitation.toDate(venda.getData());
                                if(Sanitation.between(dataVendas, dataInicial, dataFinal)){
                                    vendas += (float) Math.floor(venda.getValor() * 100) / 100;
                                }
                            }
                            comissionadoVendasTotais += vendas;
                            comissao = (float) Math.floor(vendas * ((Comissionado) empregado).getTaxaDeComissao() * 100) / 100;
                            comissionadoComissaoTotais += comissao;
                            salarioBruto = fixo + comissao;
                            comissionadoBrutoTotais += salarioBruto;
                            if(descontos > salarioBruto)
                                descontos = salarioBruto;
                            comissionadoDescontosTotais += descontos;
                            salarioLiquido = salarioBruto - descontos;
                            comissionadoLiquidoTotais += salarioLiquido;
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
        texto.append("FOLHA DE PAGAMENTO DO DIA ").append(Sanitation.toString(diaAtual, TipoNumerico.dataFile));
        texto.append("\n====================================\n\n");
        texto.append("===============================================================================================================================\n");
        texto.append("===================== HORISTAS ================================================================================================\n");
        texto.append("===============================================================================================================================\n");
        texto.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        texto.append("==================================== ===== ===== ============= ========= =============== ======================================");
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
        return texto.toString();
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
            }
            case CriaEmpregado ignored -> this.folha.remove(emp);
            case RemoveEmpregado removeEmpregado -> this.folha.add(removeEmpregado.getEmpregado());
            case LancaCartao lancaCartao -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                CartaoDePonto cartao = lancaCartao.getCartao();
                ((Horista) empregado).removeCartao(cartao);
            }
            case LancaVenda lancaVenda -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                ResultadoDeVenda venda = lancaVenda.getVenda();
                ((Comissionado) empregado).removeVenda(venda);
            }
            case LancaServico lancaServico ->{
                MembroSindicato membro = this.folha.getMembroById(emp);
                TaxaServico servico = lancaServico.getServico();
                membro.removeTaxa(servico);
            }
            case AlteraEmpregado alteraEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Atributo atributo = alteraEmpregado.getAtributo();
                switch (atributo){
                    case nome -> empregado.setNome((String) alteraEmpregado.getValorAntigo());
                    case endereco -> empregado.setEndereco((String) alteraEmpregado.getValorAntigo());
                    case tipo -> this.folha.replace((Empregado) alteraEmpregado.getValorNovo(), (Empregado) alteraEmpregado.getValorAntigo());
                    case salario -> empregado.setSalario((float) alteraEmpregado.getValorAntigo());
                    case comissao -> {
                        if(!(empregado instanceof Comissionado))
                            throw new NaoComissionado();
                        ((Comissionado) empregado).setTaxaDeComissao((float) alteraEmpregado.getValorAntigo());
                    }
                    case sindicalizado -> {
                        if(empregado.ehSindicalizado())
                            this.folha.removeMembro((String) alteraEmpregado.getValorAntigo());
                        if(alteraEmpregado.getValorAntigo() == null) {
                            empregado.setIdMembro(null);
                        }else {
                            MembroSindicato membro = (MembroSindicato) alteraEmpregado.getValorAntigo();
                            this.folha.addMembro(membro);
                            empregado.setIdMembro(membro.getIdMembro());
                        }
                    }
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
        Operacao operacao = redoStack.pop();
        String emp = operacao.getId();
        switch (operacao) {
            case ZeraSistema zeraSistema -> {
                this.folha = new SistemaDeFolha();
            }
            case CriaEmpregado criaEmpregado -> this.folha.add(criaEmpregado.getEmpregado());
            case RemoveEmpregado removeEmpregado -> this.folha.remove(emp);
            case LancaCartao lancaCartao -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                CartaoDePonto cartao = lancaCartao.getCartao();
                ((Horista) empregado).addCartao(cartao);
            }
            case LancaVenda lancaVenda -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                ResultadoDeVenda venda = lancaVenda.getVenda();
                ((Comissionado) empregado).addVenda(venda);
            }
            case LancaServico lancaServico ->{
                MembroSindicato membro = this.folha.getMembroById(emp);
                TaxaServico servico = lancaServico.getServico();
                membro.addTaxa(servico);
            }
            case AlteraEmpregado alteraEmpregado -> {
                Empregado empregado = this.folha.getEmpregadoById(emp);
                Atributo atributo = alteraEmpregado.getAtributo();
                switch (atributo){
                    case nome -> empregado.setNome((String) alteraEmpregado.getValorNovo());
                    case endereco -> empregado.setEndereco((String) alteraEmpregado.getValorNovo());
                    case tipo -> this.folha.replace((Empregado) alteraEmpregado.getValorAntigo(), (Empregado) alteraEmpregado.getValorNovo());
                    case salario -> empregado.setSalario((float) alteraEmpregado.getValorNovo());
                    case comissao -> {
                        if(!(empregado instanceof Comissionado))
                            throw new NaoComissionado();
                        ((Comissionado) empregado).setTaxaDeComissao((float) alteraEmpregado.getValorNovo());
                    }
                    case sindicalizado -> {
                        if(empregado.ehSindicalizado())
                            this.folha.removeMembro((String) alteraEmpregado.getValorAntigo());
                        if(alteraEmpregado.getValorNovo() == null) {
                            empregado.setIdMembro(null);
                        }else {
                            MembroSindicato membro = (MembroSindicato) alteraEmpregado.getValorNovo();
                            this.folha.addMembro(membro);
                            empregado.setIdMembro(membro.getIdMembro());
                        }
                    }
                    case metodoPagamento -> empregado.setMetodoPagamento((MetodoPagamento) alteraEmpregado.getValorNovo());
                    case agendaPagamento -> empregado.setAgenda((String) alteraEmpregado.getValorNovo());
                }
            }
            default -> {
            }
        }
        undoStack.push(operacao);
    }

    public String getNumeroDeEmpregados(){
        return Integer.toString(this.folha.getEmpregados().size());
    }

    public void criaAgendaPagamentos(String descricao) throws DescricaoAgendaInvalida, AgendaJaExiste {
        if(this.folha.agendaExiste(descricao))
            throw new AgendaJaExiste();
        String[] parametros = descricao.split(" ");
        try {
            if (parametros[0].equals("mensal")) {
                if(parametros.length == 2){
                    if(parametros[1].equals("$")){
                        this.folha.addAgenda(descricao, new Mensal(0));
                        return;
                    }
                    int dia = Integer.parseInt(parametros[1]);
                    if(dia > 0 && dia < 29){
                        this.folha.addAgenda(descricao, new Mensal(dia));
                        return;
                    }
                }
            } else if (parametros[0].equals("semanal")) {
                if(parametros.length == 2){
                    int semana = Integer.parseInt(parametros[1]);
                    if(semana > 0 && semana < 8) {
                        this.folha.addAgenda(descricao, new Semanal(1, semana));
                        return;
                    }
                }else if(parametros.length == 3){
                    int periodicidade = Integer.parseInt(parametros[1]);
                    int semana = Integer.parseInt(parametros[2]);
                    if(periodicidade > 0 && periodicidade < 53 && semana > 0 && semana < 8) {
                        this.folha.addAgenda(descricao, new Semanal(periodicidade, semana));
                        return;
                    }
                }
            }
        }catch (Exception e) {
            throw new DescricaoAgendaInvalida();
        }
        throw new DescricaoAgendaInvalida();
    }
}
