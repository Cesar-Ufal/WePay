package br.ufal.ic.p2.wepayu.utilidade.Operacoes;

import br.ufal.ic.p2.wepayu.models.Empregados.Registros.CartaoDePonto;

public class LancaCartao extends Operacao {
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
