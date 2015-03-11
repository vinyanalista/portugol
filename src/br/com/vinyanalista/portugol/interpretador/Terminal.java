package br.com.vinyanalista.portugol.interpretador;

import java.awt.Color;

import br.com.vinyanalista.portugol.interpretador.execucao.ErroEmTempoDeExecucao;

public abstract class Terminal {
	private static final String FALSO = "FALSO";
	private static final String VERDADEIRO = "VERDADEIRO";
	
	private boolean encerrado;

	public Terminal() {
		encerrado = false;
	}

	public synchronized void encerrar() {
		encerrado = true;
	}
	
	public abstract void erro(String mensagemDeErro);

	protected abstract void escrever(String mensagem);

	public synchronized void escrever(Object... expressoes) {
		if (encerrado) {
			return;
		}
		StringBuilder mensagem = new StringBuilder();
		for (Object expressao : expressoes) {
			mensagem.append(expressao);
		}
		escrever(mensagem.toString());
	}
	
	public abstract void informacao(String mensagemDeInformacao);
	
	public synchronized boolean isEncerrado() {
		return encerrado;
	}

	protected abstract String ler();
	
	public synchronized String lerLiteral() throws ErroEmTempoDeExecucao, TerminalEncerrado {
		if (encerrado) {
			throw new TerminalEncerrado();
		}
		String leitura = ler();
		// O terminal pode ter sido encerrado durante a leitura
		if (encerrado) {
			throw new TerminalEncerrado();
		}
		return leitura;
	}
	
	public synchronized Boolean lerLogico() throws ErroEmTempoDeExecucao, TerminalEncerrado {
		if (encerrado) {
			throw new TerminalEncerrado();
		}
		String leitura = ler().toUpperCase();
		// O terminal pode ter sido encerrado durante a leitura
		if (encerrado) {
			throw new TerminalEncerrado();
		}
		if (leitura.equals(VERDADEIRO)) {
			return Boolean.TRUE;
		} else if (leitura.equals(FALSO)) {
			return Boolean.FALSE;
		} else {
			throw new ErroEmTempoDeExecucao("O programa esperava a entrada de um valor do tipo LOGICO");
		}
	}
	
	public synchronized Number lerNumerico() throws ErroEmTempoDeExecucao, TerminalEncerrado {
		if (encerrado) {
			throw new TerminalEncerrado();
		}
		String valorLido = ler();
		// O terminal pode ter sido encerrado durante a leitura
		if (encerrado) {
			throw new TerminalEncerrado();
		}
		Integer valorInteiro = null;
		Double valorReal = null;
		try {
			valorInteiro = Integer.parseInt(valorLido);
		} catch (Exception excecao) {
		}
		try {
			valorReal = Double.parseDouble(valorLido);
		} catch (Exception excecao) {
		}
		if (valorInteiro != null) {
			return valorInteiro;
		} else if (valorReal != null) {
			return valorReal;
		} else {
			throw new ErroEmTempoDeExecucao("O programa esperava a entrada de um valor do tipo NUMERICO");
		} 
	}
	
	public abstract void limpar();
	
	protected abstract void mudarCor(Color cor);
	
}