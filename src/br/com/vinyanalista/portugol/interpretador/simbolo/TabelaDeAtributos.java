package br.com.vinyanalista.portugol.interpretador.simbolo;

import java.util.*;

public class TabelaDeAtributos {
	private final HashMap<Atributo, Object> tabela;

    public TabelaDeAtributos() {
        tabela = new HashMap<Atributo, Object>();
    }
    
    public Set<Atributo> atributos() {
        return tabela.keySet();
    }
    
    public boolean possui(Atributo atributo) {
        return tabela.containsKey(atributo);
    }

    public void inserir(Atributo atributo, Object valor) {
        tabela.put(atributo, valor);
    }

    public Object obter(Atributo atributo) {
        return tabela.get(atributo);
    }
    
    public int tamanho() {
        return tabela.size();
    }
}
