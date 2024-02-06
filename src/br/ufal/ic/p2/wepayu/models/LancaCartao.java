package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utilidade.Atributo;

import java.io.Serializable;

public class LancaCartao extends Operacao implements Serializable {
    private CartaoDePonto cartao;
    public LancaCartao(String emp, CartaoDePonto cartao) {
        this.setId(emp);
        this.setCartao(cartao);
    }

    public CartaoDePonto getCartao() {
        return this.cartao;
    }

    public void setCartao(CartaoDePonto cartao) {
        this.cartao = cartao;
    }

}
