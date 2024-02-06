package br.ufal.ic.p2.wepayu.utilidade;

import br.ufal.ic.p2.wepayu.Exception.*;
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
    private static final DateTimeFormatter dateFileFormatter = DateTimeFormatter.ofPattern("uuuu-dd-MM").withResolverStyle(ResolverStyle.STRICT);
    public static final String incialDate = "1/1/2005";

    public static Atributo getAtributo(String atributoString) throws AtributoNaoExiste{
        Atributo atributo;
        try{
            atributo = Atributo.valueOf(atributoString);
        }catch (Exception e){
            throw new AtributoNaoExiste();
        }
        return atributo;
    }
    public static Tipo getTipo(String tipoString) throws TipoInvalido{
        Tipo tipo;
        try{
            tipo = Tipo.valueOf(tipoString);
        }catch (Exception e){
            throw new TipoInvalido();
        }
        return tipo;
    }
    public static void notNull(String str, Atributo atributo) throws Exception {
        if(str == null || str.isEmpty())
            switch (atributo) {
                case idEmpregado -> throw new IdentificacaoNula();
                case nome -> throw new NomeNulo();
                case endereco -> throw new EnderecoNulo();
                case tipo -> throw new TipoNulo();
                case salario -> throw new SalarioNulo();
                case comissao -> throw new ComissaoNula();
                case idSindicato -> throw new IdSindicatoNula();
                case taxaSindical -> throw new TaxaSindicalNula();
                case banco -> throw new BancoNulo();
                case agencia -> throw new AgenciaNulo();
                case contaCorrente -> throw new ContaNulo();
                case membro -> throw new MembroNulo();
                case agendaPagamento -> throw new Exception("Agenda de pagamento nao pode ser nula.");
                default -> throw new Exception("Erro Fatal!");
            }
        if(atributo == Atributo.sindicalizado)
            toBool(str);
    }
    public static float toFloat(String num, TipoNumerico tipo) throws Exception {
        float numFloat;
        try {
            numFloat = Float.parseFloat(num.replace(',', '.'));
        }catch (Exception e){
            switch (tipo) {
                case salario -> throw new SalarioNumerico();
                case comissao -> throw new ComissaoNumerica();
                case taxaSindical -> throw new TaxaSindicalNumerica();
                case hora -> throw new HoraNumerica();
                case valor -> throw new ValorNumerico();
                default -> throw new Exception("Erro inesperado, nao devia estar aqui!!!");
            }
        }
        if(numFloat < 0)
            switch (tipo) {
                case salario -> throw new SalarioNaoNegativo();
                case comissao -> throw new ComissaoNaoNegativa();
                case taxaSindical -> throw new TaxaSindicalNegativa();
                case hora -> throw new HoraPositiva();
                case valor -> throw new ValorPositivo();
            }
        if(numFloat == 0)
            switch (tipo) {
                case salario -> throw new SalarioNulo();
                case comissao -> throw new ComissaoNula();
                case hora -> throw new HoraPositiva();
                case valor -> throw new ValorPositivo();
            }
        return numFloat;
    }
    static public boolean toBool(String trueFalse) throws Exception {
        if(trueFalse.equals("true"))
            return true;
        else if(trueFalse.equals("false"))
            return false;
        throw new ValorTrueFalse();
    }
    public static LocalDate isValid(String dataString, TipoDate tipo) throws DataInvalida {
        LocalDate data;
        try {
            data = LocalDate.parse(dataString, dateFormatter);
        }catch (DateTimeParseException e){
            switch (tipo){
                case DataInicial -> throw new DataInvalida(" inicial");
                case DataFinal -> throw new DataInvalida(" final");
                default -> throw new DataInvalida("");
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

    public static void ordemCronologica(LocalDate dataIncial, LocalDate dataFinal) throws Cronologica {
        if(dataFinal.isBefore(dataIncial))
            throw new Cronologica();
    }

    public static boolean between(LocalDate data, LocalDate dataInicial, LocalDate dataFinal){
        return (dataInicial.equals(data) || dataInicial.isBefore(data)) && dataFinal.isAfter(data);
    }
}
