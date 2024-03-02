package br.ufal.ic.p2.wepayu.models.empregados.pagamentos;

import br.ufal.ic.p2.wepayu.exception.naoeh.NaoEhException;
import br.ufal.ic.p2.wepayu.exception.naoeh.NaoRecebeBancoException;

abstract public class MetodoPagamento {
    private String metodo;

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getBanco() throws NaoEhException {
        throw new NaoRecebeBancoException();
    }

    public String getAgencia() throws NaoEhException {
        throw new NaoRecebeBancoException();
    }

    public String getContaCorrente() throws NaoEhException {
        throw new NaoRecebeBancoException();
    }
}
