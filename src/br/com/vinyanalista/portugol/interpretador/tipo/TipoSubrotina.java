package br.com.vinyanalista.portugol.interpretador.tipo;

import java.util.*;

import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;

public class TipoSubrotina extends Tipo {
	public static String parametrosParaString(String[] parametros) {
		StringBuilder string = new StringBuilder("(");
		for (int p = 0; p < parametros.length; p++) {
			string.append(parametros[p].trim().toUpperCase());
			if (p < parametros.length - 1) {
				string.append(", ");
			}
		}
		string.append(")");
		return string.toString();
	}
	
	/* public static String argumentosParaString(AChamadaASubRotina chamada) {
		List<PExpressao> argumentos = chamada.getExpressao();
		int quantidadeDeArgumentos = argumentos.size();
		String[] argumentosComoString = new String[quantidadeDeArgumentos];
		for (int a = 0; a < quantidadeDeArgumentos; a++) {
			argumentosComoString[a] = argumentos.get(a).toString();
		}
		return parametrosParaString(argumentosComoString);
	} */
	
	private final PSubRotina implementacao;
	private final List<Simbolo> parametros;
	private final TabelaDeSimbolos tabelaDeSimbolos;

	public TipoSubrotina(PSubRotina implementacao, TabelaDeSimbolos tabelaDeSimbolosPai) {
		super(null);
		this.implementacao = implementacao;
		parametros = new ArrayList<Simbolo>();
		tabelaDeSimbolos = new TabelaDeSimbolos(tabelaDeSimbolosPai);
	}
	
	public PSubRotina getImplementacao() {
		return implementacao;
	}
	
	public List<Simbolo> getParametros() {
		return parametros;
	}

	public TabelaDeSimbolos getTabelaDeSimbolos() {
		return tabelaDeSimbolos;
	}
	
	@Override
	public Object getValorPadrao() {
		return null;
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
		if (!(objeto instanceof TipoSubrotina)) {
			return false;
		}
		return toString().equals(((TipoSubrotina) objeto).toString());
	}

	@Override
	public boolean podeReceberEntradaDoUsuario() {
		return false;
	}

	@Override
	public String toString() {
		int quantidadeDeParametros = parametros.size();
		String[] parametrosComoString = new String[quantidadeDeParametros];
		for (int p = 0; p < quantidadeDeParametros; p++) {
			Simbolo simboloDoParametro = parametros.get(p);
			TabelaDeAtributos atributosDoParametro = tabelaDeSimbolos.obter(simboloDoParametro);
			Tipo tipoDoParametro = (Tipo) atributosDoParametro.obter(Atributo.TIPO);
			parametrosComoString[p] = tipoDoParametro.toString();
		}
		return "SUB-ROTINA" + parametrosParaString(parametrosComoString);
	}

}