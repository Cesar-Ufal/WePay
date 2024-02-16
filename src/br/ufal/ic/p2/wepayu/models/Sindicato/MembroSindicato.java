package br.ufal.ic.p2.wepayu.models.Sindicato;

import java.io.Serializable;
import java.util.ArrayList;

public class MembroSindicato implements Serializable {
    private String idMembro;
    private float taxaSindical;
    private ArrayList<TaxaServico> taxas;

    public MembroSindicato(String idMembro, float taxaSindical){
        this();
        setIdMembro(idMembro);
        setTaxaSindical(taxaSindical);
        this.setTaxas(new ArrayList<>());
    }
    public MembroSindicato(){
        this.taxas = new ArrayList<>();
    }
    public String getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(String idMembro) {
        this.idMembro = idMembro;
    }

    public float getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(float taxaSindical) {
        this.taxaSindical = taxaSindical;
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
