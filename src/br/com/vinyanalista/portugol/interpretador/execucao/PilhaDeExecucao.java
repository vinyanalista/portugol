package br.com.vinyanalista.portugol.interpretador.execucao;

import java.util.Stack;

public class PilhaDeExecucao {

	private final Stack<RegistroDeAtivacao> pilha;

	public PilhaDeExecucao() {
		pilha = new Stack<RegistroDeAtivacao>();
	}

	public void inserir(RegistroDeAtivacao registroDeAtivacao) {
		pilha.push(registroDeAtivacao);
	}

	public RegistroDeAtivacao obterRegistroAtual() {
		return pilha.peek();
	}

	public RegistroDeAtivacao remover() {
		return pilha.pop();
	}

}