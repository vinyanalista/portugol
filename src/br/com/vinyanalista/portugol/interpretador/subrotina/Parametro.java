package br.com.vinyanalista.portugol.interpretador.subrotina;

import br.com.vinyanalista.portugol.interpretador.simbolo.Simbolo;
import br.com.vinyanalista.portugol.interpretador.tipo.Tipo;

public class Parametro {
	private final String descricao;
	private final String identificador;
	private final Tipo tipo;

	public Parametro(String identificador, String descricao, Tipo tipo) {
		this.identificador = identificador;
		this.descricao = descricao;
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getIdentificador() {
		return identificador;
	}

	public Tipo getTipo() {
		return tipo;
	}
	
	public Simbolo getSimbolo() {
		return Simbolo.obter(identificador);
	}

}