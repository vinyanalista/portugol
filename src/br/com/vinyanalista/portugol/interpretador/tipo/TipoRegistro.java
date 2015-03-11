package br.com.vinyanalista.portugol.interpretador.tipo;

import br.com.vinyanalista.portugol.interpretador.execucao.Registro;
import br.com.vinyanalista.portugol.interpretador.simbolo.TabelaDeSimbolos;

public class TipoRegistro extends Tipo {
	private final TabelaDeSimbolos tabelaDeSimbolos;

	public TipoRegistro() {
		super(null);
		tabelaDeSimbolos = new TabelaDeSimbolos();
	}

	public TabelaDeSimbolos getCampos() {
		return tabelaDeSimbolos;
	}

	@Override
	public boolean ehLogico() {
		return false;
	}

	@Override
	public boolean ehNumerico() {
		return false;
	}

	@Override
	public boolean equals(Object objeto) {
		if (!(objeto instanceof TipoRegistro)) {
			return false;
		}
		return tabelaDeSimbolos
				.equals(((TipoRegistro) objeto).tabelaDeSimbolos);
	}
	
	@Override
	public Object getValorPadrao() {
		return new Registro(this);
	}

	@Override
	public boolean podeReceberEntradaDoUsuario() {
		return false;
	}

	@Override
	public String toString() {
		// TODO Implementar TipoRegistro.toString()
		return "REGISTRO([...])";
	}

}