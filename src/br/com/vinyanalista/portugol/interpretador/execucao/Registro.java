package br.com.vinyanalista.portugol.interpretador.execucao;

import java.util.HashMap;

import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class Registro {
	private final HashMap<Simbolo, PosicaoDeMemoria> campos;

	public Registro(TipoRegistro tipo) {
		TabelaDeSimbolos campos = tipo.getCampos();
		int quantidadeDeCampos = campos.tamanho();
		this.campos = new HashMap<Simbolo, PosicaoDeMemoria>(quantidadeDeCampos);
		for (int c = 0; c < quantidadeDeCampos; c++) {
			TabelaDeAtributos atributosDoCampo = campos.obter(c);
			Simbolo simboloDoCampo = (Simbolo) atributosDoCampo.obter(Atributo.SIMBOLO);
			Tipo tipoDoCampo = (Tipo) atributosDoCampo.obter(Atributo.TIPO);
			PosicaoDeMemoria campo = new PosicaoDeMemoria();
			campo.setValor(tipoDoCampo.getValorPadrao());
			this.campos.put(simboloDoCampo, campo);
		}
	}

	public PosicaoDeMemoria getCampo(Simbolo campo) {
		return campos.get(campo);
	}
}