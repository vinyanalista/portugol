package br.com.vinyanalista.portugol.ide.componente;

import javax.swing.*;

public class ItemDeMenuComCheckbox extends JCheckBoxMenuItem {
	private static final long serialVersionUID = 1L;

	private final JTextPaneToolTip tooltip;

	public ItemDeMenuComCheckbox(Action acao, JTextPaneToolTip tooltip) {
		super(acao);
		this.tooltip = tooltip;
	}

	@Override
	public JToolTip createToolTip() {
		return tooltip;
	}
}