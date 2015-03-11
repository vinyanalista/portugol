package br.com.vinyanalista.portugol.ide;

import javax.swing.*;

public class BotaoDaBarraDeFerramentas extends JButton {
	private static final long serialVersionUID = 1L;

	public BotaoDaBarraDeFerramentas(Action acao) {
		super(acao);
		setText(null);
		setToolTipText((String) acao.getValue(Action.NAME));
	}

	@Override
	public void setText(String text) {
		// Não faz nada, pois os botões da barra de ferramentas não devem
		// apresentar texto
	}
}
