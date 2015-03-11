package br.com.vinyanalista.portugol.interpretador.tipo;

import br.com.vinyanalista.portugol.base.node.PVariavel;

public class Campo {
	public static String identificadorGlobal(String identificadorDoRegistro,
			String identificadorDoCampo) {
		return identificadorDoRegistro + "." + identificadorDoCampo;
	}
	
	public static String string(String stringDoRegistro, String stringDoCampo) {
		return stringDoRegistro + "." + stringDoCampo;
	}

	private final String identificador;
	private final PVariavel variavel;

	public Campo(String identificador, PVariavel variavel) {
		this.identificador = identificador;
		this.variavel = variavel;
	}

	@Override
	public boolean equals(Object objeto) {
		if (!(objeto instanceof Campo)) {
			return false;
		}
		Campo outroCampo = (Campo) objeto;

		return this.identificador.equals(outroCampo.identificador)
				&& this.variavel.equals(outroCampo.variavel);
	}

	public String getIdentificador() {
		return identificador;
	}

	public PVariavel getVariavel() {
		return variavel;
	}
}