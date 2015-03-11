package br.com.vinyanalista.portugol.ide.componente;

import javax.swing.*;

public class ItemDeMenu extends JMenuItem {
	private static final long serialVersionUID = 1L;

	private final JTextPaneToolTip tooltip;

	public ItemDeMenu(Action acao, JTextPaneToolTip tooltip) {
		super(acao);
		this.tooltip = tooltip;
	}

	@Override
	public JToolTip createToolTip() {
		return tooltip;
	}
}