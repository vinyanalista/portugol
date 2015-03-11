package br.com.vinyanalista.portugol.ide;

import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;

import br.com.vinyanalista.portugol.auxiliar.Log;

public class Aplicativo extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final TelaPrincipalCompleta telaPrincipalCompleta;

	public Aplicativo() {
		super();
		if (TelaPrincipalCompletaJavaWebStart.executandoViaJavaWebStart()) {
			Log.informacao("Executando via Java Web Start");
			telaPrincipalCompleta = new TelaPrincipalCompletaJavaWebStart(this);
		} else {
			telaPrincipalCompleta = new TelaPrincipalCompletaDesktop(this);
		}
		
		setRootPane(telaPrincipalCompleta);
		
		addWindowListener(new WindowAdapter() {
			// Confirma antes de sair
			// http://iitdu.forumsmotion.com/t593-java-swing-adding-confirmation-dialogue-for-closing-window-in-jframe
			public void windowClosing(WindowEvent e) {
				telaPrincipalCompleta.sair();
			}

			// Ícone da janela
			// http://stackoverflow.com/questions/15657569/how-to-set-icon-to-jframe
			public void windowOpened(WindowEvent evt) {
				setIconImage(Icone.obterIcone(Icone.PORTUGOL).getImage());
				telaPrincipalCompleta.editor.requestFocusInWindow();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setMinimumSize(telaPrincipalCompleta.getMinimumSize());
		setSize(telaPrincipalCompleta.getSize());
		setLocationRelativeTo(null); // Centraliza a janela
	}

	public static void main(String[] args) {
		final Aplicativo aplicativo = new Aplicativo();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace(); // Não deve acontecer
				}
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				aplicativo.setVisible(true);
			}
		});
	}
}