package br.com.vinyanalista.portugol.ide;

import javax.swing.*;

public abstract class Acao extends AbstractAction {
	protected static final long serialVersionUID = 1L;

	public Acao(String nome, Icon icone, String tooltip, KeyStroke atalho) {
		super(nome, icone);
		putValue(ACCELERATOR_KEY, atalho);
		putValue(SHORT_DESCRIPTION, tooltip);
	}

}
