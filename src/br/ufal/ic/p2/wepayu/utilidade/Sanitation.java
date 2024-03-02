package br.ufal.ic.p2.wepayu.utilidade;

import br.ufal.ic.p2.wepayu.exception.CronologicaException;
import br.ufal.ic.p2.wepayu.exception.existe.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.exception.invalido.*;
import br.ufal.ic.p2.wepayu.exception.nulo.*;
import br.ufal.ic.p2.wepayu.exception.numerico.*;
import br.ufal.ic.p2.wepayu.exception.numerico.negativo.*;
import br.ufal.ic.p2.wepayu.exception.numerico.positivo.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class Sanitation {
    private static final DecimalFormat formatter = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ITALIAN));
    private static final DecimalFormat formatterHora = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ITALIAN));
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter dateFileFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    public static Atributo getAtributo(String atributoString) throws AtributoNaoExisteException {
        try{
            return Atributo.valueOf(atributoString);
        }catch (Exception e){
            throw new AtributoNaoExisteException();
        }
    }

    public static void notNull(String str, Atributo atributo) throws NuloException {
        if(str == null || str.isEmpty())
            switch (atributo) {
                case idEmpregado -> throw new IdentificacaoNulaException();
                case nome -> throw new NomeNuloException();
                case endereco -> throw new EnderecoNuloException();
                case tipo -> throw new TipoNuloException();
                case salario -> throw new SalarioNuloException();
                case comissao -> throw new ComissaoNulaException();
                case idSindicato -> throw new IdSindicatoNuloException();
                case taxaSindical -> throw new TaxaSindicalNulaException();
                case banco -> throw new BancoNuloException();
                case agencia -> throw new AgenciaNulaException();
                case contaCorrente -> throw new ContaNulaException();
                case membro -> throw new MembroNuloException();
                case agendaPagamento -> throw new AgendaNulaException();
                default -> throw new IllegalStateException("Unexpected value: " + atributo);
            }
        if(atributo == Atributo.sindicalizado && ! str.equals("true") && ! str.equals("false"))
            throw new ValorTrueFalseException();
    }

    public static float toFloat(String num, TipoNumerico tipo) throws NuloException, NumericoException {
        float numFloat;
        try {
            numFloat = Float.parseFloat(num.replace(',', '.'));
        }catch (Exception e){
            switch (tipo) {
                case salario -> throw new SalarioNumericoException();
                case comissao -> throw new ComissaoNumericaException();
                case taxaSindical -> throw new TaxaSindicalNumericaException();
                case hora -> throw new HoraNumericaException();
                case valor -> throw new ValorNumericoException();
                default -> throw new IllegalStateException("Unexpected value: " + tipo);
            }
        }
        return numFloat;
    }

    public static void numberValid(float num, TipoNumerico tipo) throws NumericoException, NuloException {
        if(num < 0)
            switch (tipo) {
                case salario -> throw new SalarioNegativoException();
                case comissao -> throw new ComissaoNegativaException();
                case taxaSindical -> throw new TaxaSindicalNegativaException();
                case hora -> throw new HoraPositivaException();
                case valor -> throw new ValorPositivoException();
            }
        if(num == 0)
            switch (tipo) {
                case salario -> throw new SalarioNuloException();
                case comissao -> throw new ComissaoNulaException();
                case hora -> throw new HoraPositivaException();
                case valor -> throw new ValorPositivoException();
            }
    }

    public static LocalDate isValid(String dataString, TipoDate tipo) throws InvalidoException {
        LocalDate data;
        try {
            data = LocalDate.parse(dataString, dateFormatter);
        }catch (DateTimeParseException e){
            switch (tipo){
                case DataInicial -> throw new DataInvalidaException(" inicial");
                case DataFinal -> throw new DataInvalidaException(" final");
                default -> throw new DataInvalidaException("");
            }
        }
        return data;
    }

    public static LocalDate toDate(String dataString) {
        return LocalDate.parse(dataString, dateFormatter);
    }

    public static String toString(Object num, TipoNumerico atributo){
        if(atributo == TipoNumerico.hora)
            return formatterHora.format((float) num);
        if(atributo == TipoNumerico.data)
            return dateFormatter.format((LocalDate) num);
        if(atributo == TipoNumerico.dataFile)
           return dateFileFormatter.format((LocalDate) num);
        return formatter.format((float) num);
    }

    public static void ordemCronologica(LocalDate dataIncial, LocalDate dataFinal) throws CronologicaException {
        if(dataFinal.isBefore(dataIncial))
            throw new CronologicaException();
    }

    public static boolean between(LocalDate data, LocalDate dataInicial, LocalDate dataFinal){
        return (dataInicial.equals(data) || dataInicial.isBefore(data)) && dataFinal.isAfter(data);
    }
}
