package br.com.vinyanalista.portugol.interpretador;

public class TerminalEncerrado extends Exception {
	private static final long serialVersionUID = 1L;

	public TerminalEncerrado() {
		super("Terminal encerrado");
	}

}