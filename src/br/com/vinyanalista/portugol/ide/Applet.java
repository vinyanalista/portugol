package br.com.vinyanalista.portugol.ide;

import java.util.Locale;

import javax.swing.*;

import br.com.vinyanalista.portugol.auxiliar.Log;

public class Applet extends JApplet {
	private static final long serialVersionUID = 1L;
	
	private final TelaPrincipalMinima telaPrincipalMinima;

	public Applet() {
		Locale.setDefault(new Locale("pt", "BR"));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace(); // Não deve acontecer
		}
		Log.informacao("Executando via applet");
		telaPrincipalMinima = new TelaPrincipalMinima();
		setRootPane(telaPrincipalMinima);
	}
	
	@Override
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				telaPrincipalMinima.editor.requestFocusInWindow();
			}
		});
	}
	
}