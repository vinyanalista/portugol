package br.com.vinyanalista.portugol.interpretador;

import java.awt.Color;
import java.util.Scanner;

public class TerminalDeTexto extends Terminal {
	private static final long serialVersionUID = 1L;

	private Scanner scanner;

	public TerminalDeTexto() {
		super();
		scanner = new Scanner(System.in);
	}

	@Override
	public synchronized void encerrar() {
		if (isEncerrado()) {
			return;
		}
		super.encerrar();
		scanner.close();
	}

	@Override
	public synchronized void erro(String mensagemDeErro) {
		if (isEncerrado()) {
			return;
		}
		mudarCor(Color.RED);
		System.out.println(mensagemDeErro);
		mudarCor(null);
	}

	@Override
	protected synchronized void escrever(String mensagem) {
		mudarCor(null);
		System.out.println(mensagem);
	}
	
	@Override
	public void informacao(String mensagemDeInformacao) {
		if (isEncerrado()) {
			return;
		}
		mudarCor(Color.GREEN);
		System.out.println(mensagemDeInformacao);
		mudarCor(null);
	}

	@Override
	protected synchronized String ler() {
		mudarCor(Color.CYAN);
		String leitura = scanner.nextLine();
		mudarCor(null);
		return leitura;
	}

	@Override
	public synchronized void limpar() {
		if (isEncerrado()) {
			return;
		}
		// TODO Implementar ConsoleTextual.limpar()
	}
	
	@Override
	protected void mudarCor(Color cor) {
		// http://stackoverflow.com/questions/1448858/how-to-color-system-out-println-output
		// https://github.com/fusesource/jansi/blob/master/jansi/src/main/java/org/fusesource/jansi/Ansi.java
		StringBuilder sequenciaDeEscapeANSI = new StringBuilder();
		sequenciaDeEscapeANSI.append((char) 27);
		sequenciaDeEscapeANSI.append('[');
		int corComoInteiro = 39; // Cor padrão
		if (cor != null) {
			if (cor == Color.BLACK) {
				corComoInteiro = 30;
			} else if (cor == Color.BLUE) {
				corComoInteiro = 34;
			} else if (cor == Color.CYAN) {
				corComoInteiro = 36;
			} else if (cor == Color.GREEN) {
				corComoInteiro = 32;
			} else if (cor == Color.MAGENTA) {
				corComoInteiro = 35;
			} else if (cor == Color.RED) {
				corComoInteiro = 31;
			} else if (cor == Color.WHITE) {
				corComoInteiro = 37;
			} else if (cor == Color.YELLOW) {
				corComoInteiro = 33;
			}
		}
		sequenciaDeEscapeANSI.append(corComoInteiro);
		sequenciaDeEscapeANSI.append('m');
		System.out.print(sequenciaDeEscapeANSI);
	}

}