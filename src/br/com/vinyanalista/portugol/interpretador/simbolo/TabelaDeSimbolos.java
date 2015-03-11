package br.com.vinyanalista.portugol.interpretador.simbolo;

import java.util.*;

public class TabelaDeSimbolos {
	private final TabelaDeSimbolos pai;
    private final HashMap<Simbolo, TabelaDeAtributos> tabela;

    public TabelaDeSimbolos() {
    	this(null);
    }
    
    public TabelaDeSimbolos(TabelaDeSimbolos pai) {
    	this.pai = pai;
        tabela = new HashMap<Simbolo, TabelaDeAtributos>();
    }
    
    @Override
    public boolean equals(Object obj) {
    	// TODO Implementar TabelaDeSimbolos.equals(Object)
    	return super.equals(obj);
    }

    public boolean existe(Simbolo simbolo) {
    	return existe(simbolo, true);
    }
    
    public boolean existe(Simbolo simbolo, boolean buscarNaTabelaPai) {
    	boolean existe = tabela.containsKey(simbolo);
    	if (!existe && buscarNaTabelaPai && pai != null) {
    		existe = pai.existe(simbolo);
    	}
        return existe;
    }

    public TabelaDeAtributos inserir(Simbolo simbolo, TabelaDeAtributos tabelaDeAtributos) {
        tabela.put(simbolo, tabelaDeAtributos);
        return tabelaDeAtributos;
    }

    public TabelaDeAtributos obter(Simbolo simbolo) {
    	boolean existe = existe(simbolo, false);
    	if (!existe && pai != null) {
    		return pai.obter(simbolo);
    	} else {
    		return tabela.get(simbolo);
    	}
    }
    
    public TabelaDeAtributos obter(int posicao) {
        // http://javarevisited.blogspot.com.br/2011/12/how-to-traverse-or-loop-hashmap-in-java.html
        int contador = -1;
        for (Simbolo simbolo : tabela.keySet()) {
            contador++;
            if (contador == posicao) {
                return tabela.get(simbolo);
            }
        }
        return null;
    }
    
    public List<Simbolo> obterListaOrdenadaDeSimbolos() {
    	List<Simbolo> listaDeSimbolos = new ArrayList<Simbolo>(tamanho());
    	for (Simbolo simbolo : tabela.keySet()) {
    		listaDeSimbolos.add(simbolo);
    	}
    	Collections.sort(listaDeSimbolos);
    	return listaDeSimbolos;
    }
    
    public int tamanho() {
        return tabela.size();
    }
}
