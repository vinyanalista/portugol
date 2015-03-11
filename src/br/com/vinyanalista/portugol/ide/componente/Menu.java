package br.com.vinyanalista.portugol.ide.componente;

import javax.swing.*;

public class Menu extends JMenu {
	private static final long serialVersionUID = 1L;

	private final JTextPaneToolTip tooltip;

	public Menu(String texto, String ajuda, JTextPaneToolTip tooltip) {
		super(texto);
		setToolTipText(ajuda);
		this.tooltip = tooltip;
	}
	
	public Menu(String texto, char mnemonico) {
		super(texto);
		setMnemonic(mnemonico);
		tooltip = null;
	}

	@Override
	public JToolTip createToolTip() {
		if (tooltip == null) {
			return super.createToolTip();
		} else {
			return tooltip;
		}
	}
}