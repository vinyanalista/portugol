package br.com.vinyanalista.portugol.interpretador.execucao;

import java.util.HashMap;

import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class RegistroDeAtivacao {
	private final HashMap<Simbolo, PosicaoDeMemoria> memoria;
	private final RegistroDeAtivacao pai;
	private final Simbolo simboloDaSubrotina;
	private final TabelaDeSimbolos tabelaDeSimbolos;
	private Tipo tipoDoRetorno;
	private Object valorRetornado;

	public RegistroDeAtivacao(Simbolo simboloDaSubrotina,
			TabelaDeSimbolos tabelaDeSimbolos, RegistroDeAtivacao pai) {
		memoria = new HashMap<Simbolo, PosicaoDeMemoria>();
		this.pai = pai;
		this.simboloDaSubrotina = simboloDaSubrotina;
		this.tabelaDeSimbolos = tabelaDeSimbolos;
		tipoDoRetorno = null;
		valorRetornado = null;
		for (int s = 0; s < tabelaDeSimbolos.tamanho(); s++) {
			TabelaDeAtributos atributosDoSimbolo = tabelaDeSimbolos.obter(s);
			Tipo tipo = (Tipo) atributosDoSimbolo.obter(Atributo.TIPO);
			if (tipo instanceof TipoSubrotina || tipo instanceof TipoSubrotinaPredefinida) {
				continue;
			}
			PosicaoDeMemoria posicaoDeMemoria = new PosicaoDeMemoria();
			posicaoDeMemoria.setValor(tipo.getValorPadrao());
			Simbolo simbolo = (Simbolo) atributosDoSimbolo.obter(Atributo.SIMBOLO);
			atribuirPosicaoDeMemoria(simbolo, posicaoDeMemoria);
		}
	}
	
	public void atribuirPosicaoDeMemoria(Simbolo simbolo, PosicaoDeMemoria posicaoDeMemoria) {
		memoria.put(simbolo, posicaoDeMemoria);
	}
	
	public TabelaDeSimbolos getTabelaDeSimbolos() {
		return tabelaDeSimbolos;
	}
	
	public Tipo getTipoDoRetorno() {
		return tipoDoRetorno;
	}
	
	public Object getValorRetornado() {
		return valorRetornado;
	}

	public PosicaoDeMemoria obterPosicaoDeMemoria(Simbolo simbolo) {
		PosicaoDeMemoria posicaoDeMemoria = memoria.get(simbolo);
		if (posicaoDeMemoria == null) {
			return pai.obterPosicaoDeMemoria(simbolo);
		} else {
			return posicaoDeMemoria;
		}
	}
	
	public void setTipoDoRetorno(Tipo tipoDoRetorno) {
		this.tipoDoRetorno = tipoDoRetorno;
	}
	
	public void setValorRetornado(Object valorRetornado) {
		this.valorRetornado = valorRetornado;
	}
	
	@Override
	public String toString() {
		return (simboloDaSubrotina == null ? "ALGORITMO" : simboloDaSubrotina.toString());
	}
}