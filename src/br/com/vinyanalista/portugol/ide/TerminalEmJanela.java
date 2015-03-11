package br.com.vinyanalista.portugol.ide;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import br.com.vinyanalista.portugol.interpretador.*;

public class TerminalEmJanela extends Terminal {
	// private static final Color COR_VERDE_ESCURO = new Color(16, 130, 16);

	private final JButton botaoEncerrar;
	private final JButton botaoEntrar;
	private Color corDoTextoDaSaida;
	private final JTextField entrada;
	private final JTextPane saida;
	private final JDialog janelaDoConsole;
	private final TelaPrincipalMinima telaPrincipal;

	public TerminalEmJanela(TelaPrincipalMinima telaPrincipal) {
		super();

		botaoEncerrar = new JButton("Encerrar");
		botaoEncerrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				informacao("\nA execução do programa foi interrompida pelo usuário.");
				encerrar();
			}
		});
		
		botaoEntrar = new JButton("Entrar");
		botaoEntrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				entrar();
			}
		});
		botaoEntrar.setEnabled(false);
		
		entrada = new JTextField();
		entrada.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				entrar();
			}
		});
		entrada.setEnabled(false);
		
		janelaDoConsole = new JDialog();
		janelaDoConsole.addWindowListener(new WindowAdapter() {		
			@Override
			public void windowOpened(WindowEvent e) {
				janelaDoConsole.setIconImage(Icone.obterIcone(Icone.PORTUGOL).getImage());
			}
		});
		janelaDoConsole.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		janelaDoConsole.setLayout(new BorderLayout(5, 5));
		janelaDoConsole.setMinimumSize(new Dimension(250, 100));
		janelaDoConsole.setModal(false);
		janelaDoConsole.setSize(400, 300);
		janelaDoConsole.setTitle("Console");
		
		JPanel painelControles = new JPanel();
		painelControles.setLayout(new BoxLayout(painelControles, BoxLayout.X_AXIS));
		
		saida = new JTextPane();
		saida.setEditable(false);
		
		JScrollPane saidaComBarraDeRolagem = new JScrollPane(saida);
		
		this.telaPrincipal = telaPrincipal;
		
		janelaDoConsole.add(saidaComBarraDeRolagem, BorderLayout.CENTER);
		
		painelControles.add(entrada);
		painelControles.add(Box.createHorizontalStrut(5));
		painelControles.add(botaoEntrar);
		painelControles.add(botaoEncerrar);
		
		janelaDoConsole.add(painelControles, BorderLayout.SOUTH);
	}

	private void adicionarASaida(String texto) {
		// http://stackoverflow.com/questions/4059198/jtextpane-appending-a-new-string
		StyledDocument textoFormatado = saida.getStyledDocument();
		SimpleAttributeSet atributos = new SimpleAttributeSet();
		if (corDoTextoDaSaida == null) {
			corDoTextoDaSaida = Color.BLACK;
		}
		StyleConstants.setForeground(atributos, corDoTextoDaSaida);
		try {
			textoFormatado.insertString(textoFormatado.getLength(), texto + "\n", atributos);
		} catch (BadLocationException excecao) {
			excecao.printStackTrace();
		}
		posicionarCursorNoFinalDaSaida();
	}

	@Override
	public synchronized void encerrar() {
		if (isEncerrado()) {
			return;
		}
		super.encerrar();
		botaoEncerrar.setEnabled(false);
		botaoEntrar.setEnabled(false);
		entrada.setEnabled(false);
		posicionarCursorNoFinalDaSaida();
		notify();
	}
	
	private synchronized void entrar() {
		notify();
	}

	@Override
	public synchronized void erro(String mensagemDeErro) {
		if (isEncerrado()) {
			return;
		}
		mostrar();
		mudarCor(Color.RED);
		adicionarASaida(mensagemDeErro);
		mudarCor(null);
	}
	
	public void esconder() {
		janelaDoConsole.setVisible(false);
	}

	@Override
	protected synchronized void escrever(String mensagem) {
		mostrar();
		mudarCor(null);
		adicionarASaida(mensagem);
	}
	
	public synchronized void fechar() {
		encerrar();
		janelaDoConsole.dispose();
	}
	
	public JDialog getJanela() {
		return janelaDoConsole;
	}
	
	@Override
	public void informacao(String mensagemDeInformacao) {
		if (isEncerrado()) {
			return;
		}
		mostrar();
		mudarCor(Color.BLUE);
		adicionarASaida(mensagemDeInformacao);
		mudarCor(null);
	}

	@Override
	protected synchronized String ler() {
		if (isEncerrado()) {
			return null;
		}
		botaoEntrar.setEnabled(true);
		entrada.setEnabled(true);
		mostrar();
		entrada.requestFocus();
		try {
			wait();
		} catch (InterruptedException excecao) {
			return null;
		}
		if (isEncerrado()) {
			return null;
		}
		botaoEntrar.setEnabled(false);
		entrada.setEnabled(false);
		String leitura = entrada.getText();
		entrada.setText("");
		mudarCor(Color.CYAN);
		adicionarASaida(leitura);
		mudarCor(null);
		return leitura;
	}

	@Override
	public synchronized void limpar() {
		if (isEncerrado()) {
			return;
		}
		saida.setText("");
	}

	public void mostrar() {
		if (janelaDoConsole.isVisible()) {
			return;
		}
		TelaPrincipalMinima.centralizarDialogo(janelaDoConsole, telaPrincipal);
		janelaDoConsole.setVisible(true);
	}
	
	@Override
	protected void mudarCor(Color cor) {
		corDoTextoDaSaida = cor;
	}

	private void posicionarCursorNoFinalDaSaida() {
		int tamanhoDoTexto = saida.getDocument().getLength();
		saida.setCaretPosition(tamanhoDoTexto);
	}

}