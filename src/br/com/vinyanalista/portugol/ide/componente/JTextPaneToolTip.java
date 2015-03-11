package br.com.vinyanalista.portugol.ide.componente;

import javax.swing.*;

public class JTextPaneToolTip extends JToolTip {
	private static final long serialVersionUID = 1L;
	
	private final JTextPane textPane;

	public JTextPaneToolTip(JTextPane textPane) {
		this.textPane = textPane;
	}
	
	@Override
	public void setTipText(String text) {
		textPane.setText(text);
	}
}