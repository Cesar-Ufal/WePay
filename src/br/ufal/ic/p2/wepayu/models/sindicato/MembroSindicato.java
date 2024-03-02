package br.ufal.ic.p2.wepayu.models.sindicato;

import br.ufal.ic.p2.wepayu.exception.nulo.NuloException;
import br.ufal.ic.p2.wepayu.exception.numerico.NumericoException;
import br.ufal.ic.p2.wepayu.utilidade.Atributo;
import br.ufal.ic.p2.wepayu.utilidade.Sanitation;
import br.ufal.ic.p2.wepayu.utilidade.TipoNumerico;

import java.io.Serializable;
import java.util.ArrayList;

public class MembroSindicato implements Serializable {
    private String idMembro;
    private float taxaSindical;
    private ArrayList<TaxaServico> taxas;

    public MembroSindicato(String idMembro, float taxaSindical) throws NumericoException, NuloException {
        this.setIdMembro(idMembro);
        this.setTaxaSindical(taxaSindical);
        this.setTaxas(new ArrayList<>());
    }

    public MembroSindicato(String idMembro, String taxaSindical) throws NumericoException, NuloException {
        this.setIdMembro(idMembro);
        this.setTaxaSindical(taxaSindical);
        this.setTaxas(new ArrayList<>());
    }

    public MembroSindicato(){
        this.setTaxas(new ArrayList<>());
    }
    public String getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(String idMembro) throws NuloException {
        Sanitation.notNull(idMembro, Atributo.membro);
        this.idMembro = idMembro;
    }

    public float getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(float taxaSindical) throws NumericoException, NuloException {
        Sanitation.numberValid(taxaSindical, TipoNumerico.taxaSindical);
        this.taxaSindical = taxaSindical;
    }

    public void setTaxaSindical(String taxaSindicalString) throws NumericoException, NuloException {
        Sanitation.notNull(taxaSindicalString, Atributo.taxaSindical);
        float taxaSindical = Sanitation.toFloat(taxaSindicalString, TipoNumerico.taxaSindical);
        this.setTaxaSindical(taxaSindical);
    }

    public ArrayList<TaxaServico> getTaxas() {
        return taxas;
    }

    public void setTaxas(ArrayList<TaxaServico> taxas) {
        this.taxas = taxas;
    }

    public void addTaxa(TaxaServico servico){
        this.taxas.add(servico);
    }

    public void removeTaxa(TaxaServico servico){
        this.taxas.remove(servico);
    }

}
