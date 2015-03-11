package br.com.vinyanalista.portugol.ide;
//http://www.planet-source-code.com/vb/scripts/showcode.asp?lngWId=2&txtCodeId=3153
//http://www.javaworld.com/jw-06-1998/jw-06-undoredo.html

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

import org.fife.print.RPrintUtilities;
import org.fife.ui.autocomplete.*;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.folding.*;
import org.fife.ui.rtextarea.*;

import br.com.vinyanalista.portugol.auxiliar.Log;
import br.com.vinyanalista.portugol.base.lexer.LexerException;
import br.com.vinyanalista.portugol.base.parser.ParserException;
import br.com.vinyanalista.portugol.ide.componente.*;
import br.com.vinyanalista.portugol.ide.componente.Menu;
import br.com.vinyanalista.portugol.interpretador.*;
import br.com.vinyanalista.portugol.interpretador.analise.*;
import br.com.vinyanalista.portugol.interpretador.execucao.*;
import br.com.vinyanalista.portugol.interpretador.subrotina.*;

public class TelaPrincipalMinima extends JRootPane implements EscutaDeExecutor, Printable {
	private static final long serialVersionUID = 1L;
	
	protected static final String ANO = "2015";
	protected static final String AUTORES = "Antônio Vinícius";
	protected static final String NOME_DO_PROGRAMA = "Portugol Online";
	protected static final String PROPRIEDADE_FIM_DE_LINHA = "line.separator";
	protected static final String SYNTAX_STYLE_PORTUGOL = "portugol";
	protected static final float ZOOM_AJUSTE = 0.25f;
	protected static final float ZOOM_INICIAL = 1.0f;
	protected static final float ZOOM_MAXIMO = 5.0f;
	protected static final float ZOOM_MINIMO = 0.1f;
	
	protected boolean arquivoModificado = false;
    protected boolean acompanharMudancas = false;
    protected final JToolBar barraDeFerramentas;
	protected final JMenuBar barraDeMenus;
	protected final JTextPane barraDeStatus;
	protected final RSyntaxTextArea editor;
	protected Font fonteAtualDoEditor;
	protected Font fonteAtualDaNumeracao;
	protected final Font FONTE_PADRAO_DO_EDITOR;
	protected final Font FONTE_PADRAO_DA_NUMERACAO;
	protected Interpretador interpretador;
	protected final LocalizarSubstituir localizarSubstituir;
	protected RelatorioDaAnalise relatorioDaAnalise;
	protected final RTextScrollPane scrollPane;
	protected Tema temaAtual;
	protected TerminalEmJanela terminal;
	protected final JTextPaneToolTip tooltip;
	protected float zoom;

	/* Ações */
	
	protected Acao acaoNovo, acaoImprimir, acaoExecutar;

	protected Action acaoDesfazer, acaoRefazer, acaoRecortar, acaoCopiar,
			acaoColar, acaoDeletar, acaoSelecionarTudo;
	protected Acao acaoDataHora;

	protected Acao acaoLocalizar, acaoLocalizarProxima, acaoSubstituir;
	
	protected Acao acaoAumentarFonte, acaoRestaurarTamanhoPadraoDeFonte,
			acaoDiminuirFonte, acaoRelatorioDaAnalise, acaoExibirConsole;

	protected Acao acaoSobre;
	
	/* Menus */

	protected JMenu menuArquivo;
	protected JMenuItem itemDeMenuNovo;
	protected JMenu submenuExemplos;
	protected JMenuItem itemDeMenuImprimir, itemDeMenuExecutar;

	protected JMenu menuEditar;
	protected JMenuItem itemDeMenuDesfazer, itemDeMenuRefazer,
			itemDeMenuRecortar, itemDeMenuCopiar, itemDeMenuColar,
			itemDeMenuDeletar, itemDeMenuSelecionarTudo, itemDeMenuDataHora;

	protected JMenu menuLocalizar;
	protected JMenuItem itemDeMenuLocalizar, itemDeMenuLocalizarProxima,
			itemDeMenuSubstituir;

	protected JMenu menuExibir;
	protected JMenu submenuTemas;
	protected JMenuItem itemDeMenuAumentarFonte,
			itemDeMenuRestaurarTamanhoPadraoDeFonte, itemDeMenuDiminuirFonte,
			itemDeMenuRelatorioDaAnalise;
	protected JCheckBoxMenuItem itemDeMenuExibirConsole = new JCheckBoxMenuItem(acaoExibirConsole);

	protected JMenu menuAjuda;
	protected JMenuItem itemDeMenuSobre;
	
	/* Barra de ferramentas */

	protected BotaoDaBarraDeFerramentas botaoNovo, botaoImprimir,
			botaoExecutar;
	protected JToggleButton botaoExibirConsole;
	
	protected BotaoDaBarraDeFerramentas botaoDesfazer, botaoRefazer,
			botaoRecortar, botaoCopiar, botaoColar, botaoDeletar;

	protected BotaoDaBarraDeFerramentas botaoLocalizar, botaoSubstituir;

	protected BotaoDaBarraDeFerramentas botaoAumentarFonte,
			botaoRestaurarTamanhoPadraoDeFonte, botaoDiminuirFonte;

	protected BotaoDaBarraDeFerramentas botaoSobre;
	
	public TelaPrincipalMinima() {
		barraDeStatus = new JTextPane();
		tooltip = new JTextPaneToolTip(barraDeStatus);
		
		// RSyntaxTextArea
		// http://fifesoft.com/rsyntaxtextarea/
		editor = new RSyntaxTextArea(20, 60);
		
		localizarSubstituir = new LocalizarSubstituir(this);
		
		Log.informacao("Editor e localizar/substituir iniciados");
		
		/* Ações */
		
		criarAcoes();
		Log.informacao("Ações iniciadas");

		/* Barra de menus */
		
		barraDeMenus = new JMenuBar();
		criarMenus();
		montarBarraDeMenus();
		setJMenuBar(barraDeMenus);
		Log.informacao("Menus iniciados");

		/* Barra de ferramentas */
		
		barraDeFerramentas = new JToolBar("Barra de ferramentas");
		barraDeFerramentas.setMargin(new Insets(0, 0, 0, 0));
		barraDeFerramentas.setRollover(true);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(barraDeFerramentas, BorderLayout.PAGE_START);
		
		criarBotoes();
		montarBarraDeFerramentas();
		Log.informacao("Botões iniciados");

		/* Editor */

		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping(SYNTAX_STYLE_PORTUGOL, PortugolTokenMaker.class.getCanonicalName());
		editor.setSyntaxEditingStyle(SYNTAX_STYLE_PORTUGOL);
		FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_PORTUGOL, new CurlyFoldParser());
		
		editor.setCodeFoldingEnabled(true);
		editor.setAntiAliasingEnabled(true);
		scrollPane = new RTextScrollPane(editor);
		scrollPane.setFoldIndicatorEnabled(true);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		editor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				aoModificarArquivo();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				aoModificarArquivo();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				aoModificarArquivo();
			}
		});

		editor.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				atualizarPosicaoDoCursor();
				restaurarCorNormalDoRealceDaLinha();
			}
		});
		
		IconGroup iconesDoMenuDeContexto = new IconGroup("Oxygen Icons", "br/com/vinyanalista/portugol/ide/icones/", null, "png");
		RTextArea.setIconGroup(iconesDoMenuDeContexto);
		
		configurarComplecaoDeCodigo();
		
		fonteAtualDoEditor = FONTE_PADRAO_DO_EDITOR = editor.getFont();
		fonteAtualDaNumeracao = FONTE_PADRAO_DA_NUMERACAO = scrollPane.getGutter().getLineNumberFont();
		zoom = ZOOM_INICIAL;
		
		editor.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {	
			}
			
			@Override
			public void keyPressed(KeyEvent evento) {
				// Simula o Ctrl + + (que, na verdade, é Ctrl + =)
				// http://stackoverflow.com/a/25572006/1657502
				if (evento.isControlDown() && (evento.getKeyCode() == KeyEvent.VK_EQUALS)) {
					aumentarFonte();
				}
			}
		});

		Log.informacao("Editor configurado");
		
		/* Barra de status */
		
		barraDeStatus.setEditable(false);
		getContentPane().add(barraDeStatus, BorderLayout.SOUTH);
		
		ToolTipManager.sharedInstance().setInitialDelay(0);
		
		/* Tela principal */

		setMinimumSize(new Dimension(670, 200));
		setSize(670, 440);

		novo();
		
		Log.informacao("Ambiente de desenvolvimento integrado pronto");
	}

	protected boolean abrirExemplo(Exemplo exemplo) {
		if (arquivoModificado && !confirmarAntesDeDescartarAlteracoes()) {
			return false;
		}
        acompanharMudancas = false;
        arquivoModificado = false;
        editor.setText(exemplo.getProgramaFonte());
        editor.discardAllEdits(); // Limpa histórico do desfazer/refazer
        acompanharMudancas = true;
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				posicionarCursor(1, 1);
			}
		});
        return true;
	}
	
	protected void ajustarFonte(float zoom) {
		this.zoom = zoom;
		int novoTamanho = Math.round(FONTE_PADRAO_DO_EDITOR.getSize() * zoom);
		fonteAtualDoEditor = new Font(FONTE_PADRAO_DO_EDITOR.getName(), FONTE_PADRAO_DO_EDITOR.getStyle(), novoTamanho);
		editor.setFont(fonteAtualDoEditor);
		novoTamanho = Math.round(FONTE_PADRAO_DA_NUMERACAO.getSize() * zoom);
		fonteAtualDaNumeracao = new Font(FONTE_PADRAO_DA_NUMERACAO.getName(), FONTE_PADRAO_DA_NUMERACAO.getStyle(), novoTamanho);
		scrollPane.getGutter().setLineNumberFont(fonteAtualDaNumeracao);
	}
	
	protected void analisar() throws IOException, LexerException,
			ParserException, ErroSemantico {
		restaurarCorNormalDoRealceDaLinha();
		atualizarPosicaoDoCursor();
		interpretador = new Interpretador(null);
		String codigoFonte = editor.getText();
		interpretador.analisar(codigoFonte);
	}
	
	@Override
	public void aoEncerrarExecucao(ErroEmTempoDeExecucao erroEmTempoDeExecucao) {
		if (erroEmTempoDeExecucao != null) {
			tratarErro(erroEmTempoDeExecucao);
		}
	}
	
	protected void aoModificarArquivo() {
		if (!acompanharMudancas) {
            return;
        }
        arquivoModificado = editor.canUndo();
	}
	
	protected void aplicarTema(Tema tema) {
		restaurarCorNormalDoRealceDaLinha();
		tema.aplicar(editor);
		temaAtual = tema;
	}
	
	protected void atualizarBarraDeStatus(String texto) {
		barraDeStatus.setText(texto);
	}
	
	// Posição do cursor na barra de status
	protected void atualizarPosicaoDoCursor() {
        int linha = 1;
        int coluna = 1;
        int posicaoDoCursor = editor.getCaretPosition();
        try {
			linha = editor.getLineOfOffset(posicaoDoCursor);
			coluna = posicaoDoCursor - editor.getLineStartOffset(linha);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        linha++;
        coluna++;
        atualizarBarraDeStatus("Linha: " + linha + " Coluna: " + coluna);
	}
	
	protected void aumentarFonte() {
		float novoZoom = zoom + ZOOM_AJUSTE;
		if (novoZoom > ZOOM_MAXIMO) {
			return;
		}
		ajustarFonte(novoZoom);
		// Verifica se um novo aumento ultrapassaria o zoom máximo
		if ((novoZoom + ZOOM_AJUSTE) > ZOOM_MAXIMO) {
			// Se sim, desabilita a ação de aumentar fonte
			acaoAumentarFonte.setEnabled(false);
		}
		acaoDiminuirFonte.setEnabled(true);
	}
	
	public static void centralizarDialogo(JDialog dialogo, Component referencia) {
		// Obtém janela (do aplicativo ou do navegador)
		// https://community.oracle.com/thread/1293264
		if (dialogo == null || referencia == null) {
			return;
		}
		Frame janela;
		while (!(referencia instanceof Frame))
			referencia = referencia.getParent();
		janela = (Frame) referencia;
		// Obtém dimensões e posição da janela
		Dimension tamanhoDaJanela = janela.getSize();
		int larguraDaJanela = tamanhoDaJanela.width;
		int alturaDaJanela = tamanhoDaJanela.height;
		int xDaJanela = janela.getX();
		int yDaJanela = janela.getY();
		// Obtém dimensões do diálogo
		Dimension tamanhoDoDialogo = dialogo.getSize();
		int larguraDoDialogo = tamanhoDoDialogo.width;
		int alturaDoDialogo = tamanhoDoDialogo.height;
		// Posiciona o diálogo centralizado em relação à janela
		boolean podeRedimensionar = dialogo.isResizable();
		dialogo.setResizable(true);
		int xDoDialogo = xDaJanela + (larguraDaJanela - larguraDoDialogo) / 2;
		if (xDaJanela > 0 && xDoDialogo < 0) {
			xDoDialogo = xDaJanela - xDoDialogo;
		}
		if (xDoDialogo < 0) {
			xDoDialogo = 0;
		}
		int yDoDialogo = yDaJanela + (alturaDaJanela - alturaDoDialogo) / 2;
		if (yDaJanela > 0 && yDoDialogo < 0) {
			yDoDialogo = yDaJanela - yDoDialogo;
		}
		if (yDoDialogo < 0) {
			yDoDialogo = 0;
		}
		dialogo.setBounds(xDoDialogo, yDoDialogo, larguraDoDialogo, alturaDoDialogo);
		dialogo.setResizable(podeRedimensionar);
	}
	
	protected void configurarComplecaoDeCodigo() {
		// http://fifesoft.com/rsyntaxtextarea/examples/example5.php
		// http://fifesoft.com/forum/viewtopic.php?f=10&t=711
		// http://fifesoft.com/forum/viewtopic.php?f=10&t=811
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		provider.setAutoActivationRules(true, "");
		provider.setParameterizedCompletionParams('(', ", ", ')');
		
		HashMap<String, Completion> complecoes = new HashMap<String, Completion>();
		
		for (SubrotinaPreDefinida subrotina : SubrotinaPreDefinida.values()) {
			String identificador = subrotina.getIdentificador().toLowerCase();
			String tipoDoRetorno = (subrotina.getTipoDoRetorno() == null) ? null : subrotina.getTipoDoRetorno().toString();
			FunctionCompletion complecao = new FunctionCompletion(provider, identificador, tipoDoRetorno);
			complecao.setReturnValueDescription(subrotina.getDescricaoDoRetorno());
			List<Parameter> parameters = new ArrayList<Parameter>();
			for (Parametro parametro : subrotina.getParametros()) {
				Parameter param = new Parameter(parametro.getTipo().toString(), parametro.getIdentificador().toLowerCase());
				param.setDescription(parametro.getDescricao());
				parameters.add(param);
			}
			complecao.setParams(parameters);
			provider.addCompletion(complecao);
			complecoes.put(identificador, complecao);
		}
		
		Completion complecao;
		
		// 3.1 Estrutura sequencial em algoritmos
		complecao = new TemplateCompletion(provider, "algoritmo", "algoritmo", "algoritmo\n${cursor}\nfim_algoritmo", null, "Algoritmo");
		provider.addCompletion(complecao);
		complecoes.put("algoritmo", complecao);
		
		// 3.1.1 Declaração de variáveis em algoritmos
		complecao = new TemplateCompletion(provider, "declare", "declare", "declare ${variavel} ${tipo}", null, "Declaração de variáveis");
		provider.addCompletion(complecao);
		complecoes.put("declare", complecao);
		
		// 3.1.3 Comando de entrada em algoritmos
		complecao = new TemplateCompletion(provider, "leia", "leia", "leia ${variavel}", null, "Comando de entrada");
		provider.addCompletion(complecao);
		complecoes.put("leia", complecao);
		
		// 3.1.4 Comando de saída em algoritmos
		complecao = new TemplateCompletion(provider, "escreva", "escreva", "escreva ${expressao}", null, "Comando de saída");
		provider.addCompletion(complecao);
		complecoes.put("escreva", complecao);
		
		// 4.1.1 Estrutura condicional simples
		complecao = new TemplateCompletion(provider, "se", "se", "se ${condicao}\nentao ${comando1}\nsenao ${comando2}", null, "Estrutura condicional");
		provider.addCompletion(complecao);
		complecoes.put("se", complecao);
		
		complecao = new TemplateCompletion(provider, "inicio", "inicio", "inicio\n\t${cursor}\nfim", null, "Bloco de comandos");
		provider.addCompletion(complecao);
		complecoes.put("inicio", complecao);
		
		// 5.1.1 Estrutura de repetição para número definido de repetições (estrutura PARA)
		complecao = new TemplateCompletion(provider, "para", "para", "para ${indice} <- ${valor_inicial} ate ${valor_final} faca passo ${1}\n\t${comando}", null, "Estrutura de repetição para número definido de repetições (estrutura PARA)");
		provider.addCompletion(complecao);
		complecoes.put("para", complecao);
		
		// 5.1.2 Estrutura de repetição para número indefinido de repetições e teste no início (estrutura ENQUANTO)
		complecao = new TemplateCompletion(provider, "enquanto", "enquanto", "enquanto ${condicao} faca\n\t${comando}", null, "Estrutura de repetição para número indefinido de repetições e teste no início (estrutura ENQUANTO)");
		provider.addCompletion(complecao);
		complecoes.put("enquanto", complecao);
		
		// 5.1.3 Estrutura de repetição para número indefinido de repetições e teste no final (estrutura REPITA)
		complecao = new TemplateCompletion(provider, "repita", "repita", "repita\n\t${comando}\nate ${condicao}", null, "Estrutura de repetição para número indefinido de repetições e teste no final (estrutura REPITA)");
		provider.addCompletion(complecao);
		complecoes.put("repita", complecao);
		
		// 8.1 Sub-rotinas (programação modularizada)
		complecao = new TemplateCompletion(provider, "sub-rotina", "sub-rotina", "sub-rotina ${identificador} (${parametro} ${tipo})\n\t${cursor}\nfim_sub_rotina ${identificador}", null, "Sub-rotina");
		provider.addCompletion(complecao);
		complecoes.put("sub-rotina", complecao);
		
		complecao = new TemplateCompletion(provider, "retorne", "retorne", "retorne ${expressao}", null, "Comando de retorno");
		provider.addCompletion(complecao);
		complecoes.put("retorne", complecao);
		
		// 10.2 Declaração de registros em algoritmos
		complecao = new TemplateCompletion(provider, "registro", "registro", "registro (${campo} ${tipo})", null, "Registro");
		provider.addCompletion(complecao);
		complecoes.put("registro", complecao);
		
		for (String palavraReservada : PortugolTokenMaker.getPalavrasReservadas()) {
			if (complecoes.get(palavraReservada) != null) {
				continue;
			}
			complecao = new BasicCompletion(provider, palavraReservada, null,
					"Palavra reservada " + palavraReservada.toUpperCase());
			provider.addCompletion(complecao);
			complecoes.put(palavraReservada, complecao);
		}
		
		AutoCompletion autoComplecao = new AutoCompletion(provider);
		autoComplecao.setAutoActivationDelay(300);
		autoComplecao.setAutoActivationEnabled(true);
		autoComplecao.setAutoCompleteSingleChoices(false);
		autoComplecao.setParameterAssistanceEnabled(true);
		autoComplecao.setShowDescWindow(true);
		autoComplecao.install(editor);
	}
	
	protected boolean confirmarAntesDeDescartarAlteracoes() {
		String[] opcoes = {"Sim", "Não"};
		int resposta = JOptionPane.showOptionDialog(this,
				"Todo o código será descartado. Deseja prosseguir?", "Novo - "
						+ NOME_DO_PROGRAMA, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[1]);
		return (resposta == JOptionPane.YES_OPTION);
	}
	
	@SuppressWarnings("serial")
	protected void criarAcoes() {
		acaoNovo = new Acao("Novo", Icone.obterIcone(Icone.NOVO), "Inicia um novo código-fonte vazio.", KeyStroke
				.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				novo();
			}
		};
		
		acaoImprimir = new Acao("Imprimir...", Icone.obterIcone(Icone.IMPRIMIR), "Imprime o código-fonte.", KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK)) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				imprimir();
			}
		};
		
		acaoExecutar = new Acao("Executar", Icone.obterIcone(Icone.EXECUTAR),
				"Analisa e executa o código-fonte.", KeyStroke.getKeyStroke("F6")) {
			
			@Override
			public void actionPerformed(ActionEvent evento) {
				executar();				
			}
		};

		acaoDesfazer = RSyntaxTextArea.getAction(RSyntaxTextArea.UNDO_ACTION);

		acaoRefazer = RSyntaxTextArea.getAction(RSyntaxTextArea.REDO_ACTION);

		acaoRecortar = RSyntaxTextArea.getAction(RSyntaxTextArea.CUT_ACTION);

		acaoCopiar = RSyntaxTextArea.getAction(RSyntaxTextArea.COPY_ACTION);

		acaoColar = RSyntaxTextArea.getAction(RSyntaxTextArea.PASTE_ACTION);

		acaoDeletar = RSyntaxTextArea.getAction(RSyntaxTextArea.DELETE_ACTION);

		acaoSelecionarTudo = RSyntaxTextArea.getAction(RSyntaxTextArea.SELECT_ALL_ACTION);

		acaoDataHora = new Acao("Data/hora", Icone.obterIcone(Icone.DATA_HORA),
				"Insere a data e hora atuais na posição atual do código-fonte.", KeyStroke.getKeyStroke("F5")) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				inserirDataHora();
			}
		};

		acaoLocalizar = localizarSubstituir.getAcaoLocalizar();

		acaoLocalizarProxima = localizarSubstituir.getAcaoLocalizarProxima();

		acaoSubstituir = localizarSubstituir.getAcaoSubstituir();

		acaoAumentarFonte = new Acao("Aumentar tamanho da fonte", Icone
				.obterIcone(Icone.AUMENTAR_FONTE), "Aumenta o tamanho da fonte do editor de código-fonte.", KeyStroke.getKeyStroke(
				KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK)) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				aumentarFonte();
			}
		};

		acaoRestaurarTamanhoPadraoDeFonte = new Acao(
				"Restaurar tamanho padrão de fonte", Icone
						.obterIcone(Icone.RESTAURAR_TAMANHO_PADRAO_DE_FONTE),
						"Restaura o tamanho padrão da fonte do editor de código-fonte.", KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK)) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				restaurarTamanhoPadraoDeFonte();
			}
		};

		acaoDiminuirFonte = new Acao("Diminuir tamanho da fonte", Icone
				.obterIcone(Icone.DIMINUIR_FONTE), "Diminui o tamanho da fonte do editor de código-fonte.", KeyStroke.getKeyStroke(
				KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK)) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				diminuirFonte();
			}
		};
		
		acaoRelatorioDaAnalise = new Acao("Relatório da análise", null, "Analisa o código-fonte e exibe o resultado da análise.", null) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				exibirRelatorioDaAnalise();
			}
		};
		
		acaoExibirConsole = new Acao("Console",
				Icone.obterIcone(Icone.CONSOLE), "Exibe ou oculta o console.", KeyStroke.getKeyStroke("F12")) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				if (evento.getSource().equals(itemDeMenuExibirConsole)) {
					botaoExibirConsole.setSelected(itemDeMenuExibirConsole.isSelected());
				} else {
					itemDeMenuExibirConsole.setSelected(botaoExibirConsole.isSelected());
				}
				exibirConsole();
			}
		};
		acaoExibirConsole.setEnabled(false);

		acaoSobre = new Acao("Sobre o " + NOME_DO_PROGRAMA, Icone
				.obterIcone(Icone.SOBRE), "Exibe informações sobre o programa e seu autor.", KeyStroke.getKeyStroke("F1")) {
			@Override
			public void actionPerformed(ActionEvent evento) {
				sobre();
			}
		};
	}
	
	protected void criarBotoes() {
		botaoNovo = new BotaoDaBarraDeFerramentas(acaoNovo);
		
		botaoImprimir = new BotaoDaBarraDeFerramentas(acaoImprimir);
		
		botaoExecutar = new BotaoDaBarraDeFerramentas(acaoExecutar);
		
		botaoExibirConsole = new JToggleButton(acaoExibirConsole);
		botaoExibirConsole.setText(null);
		botaoExibirConsole.setToolTipText((String) acaoExibirConsole.getValue(Action.NAME));

		botaoDesfazer = new BotaoDaBarraDeFerramentas(acaoDesfazer);
		
		botaoRefazer = new BotaoDaBarraDeFerramentas(acaoRefazer);

		botaoRecortar = new BotaoDaBarraDeFerramentas(acaoRecortar);
		
		botaoCopiar = new BotaoDaBarraDeFerramentas(acaoCopiar);
		
		botaoColar = new BotaoDaBarraDeFerramentas(acaoColar);
		
		botaoDeletar = new BotaoDaBarraDeFerramentas(acaoDeletar);

		botaoLocalizar = new BotaoDaBarraDeFerramentas(acaoLocalizar);
		
		botaoSubstituir = new BotaoDaBarraDeFerramentas(acaoSubstituir);

		botaoAumentarFonte = new BotaoDaBarraDeFerramentas(acaoAumentarFonte);
		
		botaoRestaurarTamanhoPadraoDeFonte = new BotaoDaBarraDeFerramentas(acaoRestaurarTamanhoPadraoDeFonte);

		botaoDiminuirFonte = new BotaoDaBarraDeFerramentas(acaoDiminuirFonte);

		botaoSobre = new BotaoDaBarraDeFerramentas(acaoSobre);
	}
	
	@SuppressWarnings("serial")
	protected void criarMenus() {
		menuArquivo = new JMenu("Arquivo");
		menuArquivo.setMnemonic('a');
		
		itemDeMenuNovo = new ItemDeMenu(acaoNovo, tooltip);
		itemDeMenuNovo.setMnemonic('n');

		submenuExemplos = new Menu("Abrir exemplo", "Cria um novo código-fonte baseado em um dos exemplos.", tooltip);
		for (int e = 0; e < Exemplo.values().length; e++) {
			final Exemplo exemplo = Exemplo.values()[e];
			String nome = (e + 1) + " - " + exemplo.getNome();
			Acao acaoAbrirExemplo = new Acao(nome, null, "Cria um novo código-fonte baseado no exemplo \"" + exemplo.getNome() + "\".", null) {
				@Override
				public void actionPerformed(ActionEvent evento) {
					abrirExemplo(exemplo);
				}
			};
			JMenuItem itemDeMenuAbrirExemplo = new ItemDeMenu(acaoAbrirExemplo, tooltip);
			submenuExemplos.add(itemDeMenuAbrirExemplo);
		}

		itemDeMenuImprimir = new ItemDeMenu(acaoImprimir, tooltip);
		itemDeMenuImprimir.setMnemonic('i');
		
		itemDeMenuExecutar = new ItemDeMenu(acaoExecutar, tooltip);
		itemDeMenuExecutar.setMnemonic('e');

		menuEditar = new JMenu("Editar");
		menuEditar.setMnemonic('e');
		
		itemDeMenuDesfazer = new ItemDeMenu(acaoDesfazer, tooltip);
		itemDeMenuDesfazer.setMnemonic('z');

		itemDeMenuRefazer = new ItemDeMenu(acaoRefazer, tooltip);
		itemDeMenuRefazer.setMnemonic('f');

		itemDeMenuRecortar = new ItemDeMenu(acaoRecortar, tooltip);
		itemDeMenuRecortar.setMnemonic('r');

		itemDeMenuCopiar = new ItemDeMenu(acaoCopiar, tooltip);
		itemDeMenuCopiar.setMnemonic('c');

		itemDeMenuColar = new ItemDeMenu(acaoColar, tooltip);
		itemDeMenuColar.setMnemonic('l');

		itemDeMenuDeletar = new ItemDeMenu(acaoDeletar, tooltip);
		itemDeMenuDeletar.setMnemonic('x');

		itemDeMenuSelecionarTudo = new ItemDeMenu(acaoSelecionarTudo, tooltip);
		itemDeMenuSelecionarTudo.setMnemonic('t');
		
		itemDeMenuDataHora = new ItemDeMenu(acaoDataHora, tooltip);
		itemDeMenuDataHora.setMnemonic('d');

		menuLocalizar = new JMenu("Localizar");
		menuLocalizar.setMnemonic('l');

		itemDeMenuLocalizar = new ItemDeMenu(acaoLocalizar, tooltip);
		itemDeMenuLocalizar.setMnemonic('o');

		itemDeMenuLocalizarProxima = new ItemDeMenu(acaoLocalizarProxima, tooltip);
		itemDeMenuLocalizarProxima.setMnemonic('p');

		itemDeMenuSubstituir = new ItemDeMenu(acaoSubstituir, tooltip);
		itemDeMenuSubstituir.setMnemonic('s');
		
		menuExibir = new JMenu("Exibir");
		menuExibir.setMnemonic('x');
		
		submenuTemas = new Menu("Tema", "Muda as cores do editor.", tooltip);
		Tema temaPadrao = Tema.DEFAULT;
		JCheckBoxMenuItem itemDeMenuAplicarTemaPadrao = null;
		for (int t = 0; t < Tema.values().length; t++) {
			final Tema tema = Tema.values()[t];
			Acao acaoAplicarTema = new Acao(tema.getNome(), null, "Aplica as cores do tema \"" + tema.getNome() + "\" ao editor." , null) {
				@Override
				public void actionPerformed(ActionEvent evento) {
					for (int i = 0; i < submenuTemas.getItemCount(); i++) {
						JCheckBoxMenuItem itemDeMenuAplicarTema = (JCheckBoxMenuItem) submenuTemas.getItem(i);
						boolean temaSelecionado = (itemDeMenuAplicarTema.getText().equals(tema.getNome()));
						itemDeMenuAplicarTema.setSelected(temaSelecionado);
					}
					aplicarTema(tema);
				}
			};
			JCheckBoxMenuItem itemDeMenuAplicarTema = new ItemDeMenuComCheckbox(acaoAplicarTema, tooltip);
			submenuTemas.add(itemDeMenuAplicarTema);
			if (tema.equals(temaPadrao)) {
				itemDeMenuAplicarTemaPadrao = itemDeMenuAplicarTema;
			}
		}
		
		itemDeMenuAplicarTemaPadrao.setSelected(true);
		aplicarTema(temaPadrao);
		
		itemDeMenuAumentarFonte = new ItemDeMenu(acaoAumentarFonte, tooltip);
		itemDeMenuAumentarFonte.setMnemonic('a');

		itemDeMenuRestaurarTamanhoPadraoDeFonte = new ItemDeMenu(acaoRestaurarTamanhoPadraoDeFonte, tooltip);
		itemDeMenuRestaurarTamanhoPadraoDeFonte.setMnemonic('r');
		
		itemDeMenuDiminuirFonte = new ItemDeMenu(acaoDiminuirFonte, tooltip);
		itemDeMenuDiminuirFonte.setMnemonic('d');
		
		itemDeMenuRelatorioDaAnalise = new ItemDeMenu(acaoRelatorioDaAnalise, tooltip);
		
		itemDeMenuExibirConsole = new ItemDeMenuComCheckbox(acaoExibirConsole, tooltip);

		menuAjuda = new JMenu("Ajuda");
		menuAjuda.setMnemonic('u');
		
		itemDeMenuSobre = new ItemDeMenu(acaoSobre, tooltip);
		itemDeMenuSobre.setMnemonic('s');
	}
	
	protected void destacarLinhaComErro() {
		editor.setCurrentLineHighlightColor(temaAtual.getCorDaLinhaComErro());
	}
	
	protected void diminuirFonte() {
		float novoZoom = zoom - ZOOM_AJUSTE;
		if (novoZoom < ZOOM_MINIMO) {
			return;
		}
		ajustarFonte(novoZoom);
		// Verifica se uma nova diminuição ultrapassaria o zoom mínimo
		if ((novoZoom - ZOOM_AJUSTE) < ZOOM_MINIMO) {
			// Se sim, desabilita a ação de diminuir fonte
			acaoDiminuirFonte.setEnabled(false);
		}
		acaoAumentarFonte.setEnabled(true);
	}
	
	protected void executar() {
		restaurarCorNormalDoRealceDaLinha();
		atualizarPosicaoDoCursor();
		String codigoFonte = editor.getText();
		if (terminal != null) {
			terminal.fechar();
		}
		terminal = new TerminalEmJanela(this);
		interpretador = new Interpretador(terminal);
		try {
			interpretador.analisar(codigoFonte);
			interpretador.adicionarEscutaDeExecutor(this);
			interpretador.executar();
			acaoExibirConsole.setEnabled(true);
			itemDeMenuExibirConsole.setSelected(true);
			((TerminalEmJanela) terminal).getJanela().addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent e) {
					botaoExibirConsole.setSelected(false);
					itemDeMenuExibirConsole.setSelected(false);
				}
				
				@Override
				public void componentShown(ComponentEvent e) {
					botaoExibirConsole.setSelected(true);
					itemDeMenuExibirConsole.setSelected(true);
				}
			});
		} catch (Exception erro) {
			tratarErro(erro);
		}
	}
	
	private void exibirConsole() {
		if (itemDeMenuExibirConsole.isSelected()) {
			((TerminalEmJanela) terminal).mostrar();
		} else {
			((TerminalEmJanela) terminal).esconder();
		}
	}
	
	protected void exibirRelatorioDaAnalise() {
		try {
			analisar();
			if (relatorioDaAnalise != null) {
				relatorioDaAnalise.setVisible(false);
			}
			relatorioDaAnalise = new RelatorioDaAnalise(interpretador);
			centralizarDialogo(relatorioDaAnalise, this);
			relatorioDaAnalise.setVisible(true);
		} catch (Exception erro) {
			tratarErro(erro);
		}
	}
	
	public RSyntaxTextArea getEditor() {
		return editor;
	}
	
	public LocalizarSubstituir getLocalizarSubstituir() {
		return localizarSubstituir;
	}
	
	protected void imprimir() {
		// http://docs.oracle.com/javase/tutorial/2d/printing/index.html
		PrinterJob trabalhoDeImpressao = PrinterJob.getPrinterJob();
		trabalhoDeImpressao.setPrintable(this);
		if (trabalhoDeImpressao.printDialog()) {
			try {
				trabalhoDeImpressao.print();
			} catch (PrinterException excecao) {
				excecao.printStackTrace();
			}
		}
	}
	
	protected void inserirDataHora() {
		editor.insert(
						DateFormat.getDateTimeInstance().format(
								new Date(System.currentTimeMillis())),
						editor.getCaretPosition());
	}
	
	protected void montarBarraDeFerramentas() {
		barraDeFerramentas.add(botaoNovo);
		barraDeFerramentas.add(botaoImprimir);
		barraDeFerramentas.addSeparator();
		barraDeFerramentas.add(botaoExecutar);
		barraDeFerramentas.add(botaoExibirConsole);
		barraDeFerramentas.addSeparator();
		barraDeFerramentas.add(botaoDesfazer);
		barraDeFerramentas.add(botaoRefazer);
		barraDeFerramentas.addSeparator();
		barraDeFerramentas.add(botaoRecortar);
		barraDeFerramentas.add(botaoCopiar);
		barraDeFerramentas.add(botaoColar);
		barraDeFerramentas.add(botaoDeletar);
		barraDeFerramentas.addSeparator();
		barraDeFerramentas.add(botaoLocalizar);
		barraDeFerramentas.add(botaoSubstituir);
		barraDeFerramentas.addSeparator();
		barraDeFerramentas.add(botaoAumentarFonte);
		barraDeFerramentas.add(botaoRestaurarTamanhoPadraoDeFonte);
		barraDeFerramentas.add(botaoDiminuirFonte);
		barraDeFerramentas.addSeparator();
		barraDeFerramentas.add(botaoSobre);
	}
	
	protected void montarBarraDeMenus() {
		barraDeMenus.add(menuArquivo);
		menuArquivo.add(itemDeMenuNovo);
		menuArquivo.add(submenuExemplos);
		menuArquivo.addSeparator();
		menuArquivo.add(itemDeMenuImprimir);
		menuArquivo.add(itemDeMenuExecutar);
		
		barraDeMenus.add(menuEditar);
		menuEditar.add(itemDeMenuDesfazer);
		menuEditar.add(itemDeMenuRefazer);
		menuEditar.addSeparator();
		menuEditar.add(itemDeMenuRecortar);
		menuEditar.add(itemDeMenuCopiar);
		menuEditar.add(itemDeMenuColar);
		menuEditar.add(itemDeMenuDeletar);
		menuEditar.addSeparator();
		menuEditar.add(itemDeMenuSelecionarTudo);
		menuEditar.add(itemDeMenuDataHora);
		
		barraDeMenus.add(menuLocalizar);
		menuLocalizar.add(itemDeMenuLocalizar);
		menuLocalizar.add(itemDeMenuLocalizarProxima);
		menuLocalizar.add(itemDeMenuSubstituir);
		
		barraDeMenus.add(menuExibir);
		menuExibir.add(submenuTemas);
		menuExibir.addSeparator();
		menuExibir.add(itemDeMenuAumentarFonte);
		menuExibir.add(itemDeMenuRestaurarTamanhoPadraoDeFonte);
		menuExibir.add(itemDeMenuDiminuirFonte);
		menuExibir.addSeparator();
		menuExibir.add(itemDeMenuRelatorioDaAnalise);
		menuExibir.add(itemDeMenuExibirConsole);
		
		barraDeMenus.add(menuAjuda);
		menuAjuda.add(itemDeMenuSobre);
	}

	public boolean novo() {
		if (arquivoModificado && !confirmarAntesDeDescartarAlteracoes()) {
			return false;
		}
		acompanharMudancas = false;
        arquivoModificado = false;
		editor.setText("");
		editor.discardAllEdits(); // Limpa histórico do desfazer/refazer
		acompanharMudancas = true;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				posicionarCursor(1, 1);
			}
		});
		return true;
	}
	
	protected void posicionarCursor(int linha, int coluna) {
		int posicaoDoCursor = 0;
		try {
			for (int l = 0; l < linha - 1; l++) {
				posicaoDoCursor += editor.getLineEndOffset(l) - editor.getLineStartOffset(l);
			}
			posicaoDoCursor += (coluna - 1);
			editor.setCaretPosition(posicaoDoCursor);
			editor.requestFocusInWindow();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		return RPrintUtilities.printDocumentMonospacedWordWrap(graphics, editor
				.getDocument(), editor.getFont().getSize(), pageIndex,
				pageFormat, editor.getTabSize());
	}
	
	protected void restaurarCorNormalDoRealceDaLinha() {
		if ((temaAtual != null) && (editor.getCurrentLineHighlightColor().equals(temaAtual.getCorDaLinhaComErro()))) {
			editor.setCurrentLineHighlightColor(temaAtual.getCorDaLinhaNormal());
		}
	}
	
	protected void restaurarTamanhoPadraoDeFonte() {
		ajustarFonte(ZOOM_INICIAL);
		acaoAumentarFonte.setEnabled(true);
		acaoDiminuirFonte.setEnabled(true);
	}

	protected void sobre() {
		JOptionPane.showMessageDialog(TelaPrincipalMinima.this, "Copyright " + ANO + " " + AUTORES, "Sobre o " + NOME_DO_PROGRAMA, JOptionPane.INFORMATION_MESSAGE);
	}
	
	protected void tratarErro(Exception erro) {
		int linha = -1;
		int coluna = -1;
		StringBuilder mensagemDeErro = new StringBuilder("Erro");
		if (erro instanceof LexerException) {
			LexerException erroLexico = (LexerException) erro;
			linha = erroLexico.getToken().getLine();
			coluna = erroLexico.getToken().getPos();
			mensagemDeErro.append(" léxico");
		} else if (erro instanceof ParserException) {
			ParserException erroSintatico = (ParserException) erro;
			linha = erroSintatico.getToken().getLine();
			coluna = erroSintatico.getToken().getPos();
			mensagemDeErro.append(" sintático");
		} else if (erro instanceof ErroSemantico) {
			ErroSemantico erroSemantico = (ErroSemantico) erro;
			linha = erroSemantico.getLinha();
			coluna = erroSemantico.getColuna();
			mensagemDeErro.append(" semântico");
		} else if (erro instanceof ErroEmTempoDeExecucao) {
			ErroEmTempoDeExecucao erroEmTempoDeExecucao = (ErroEmTempoDeExecucao) erro;
			linha = erroEmTempoDeExecucao.getLinha();
			coluna = erroEmTempoDeExecucao.getColuna();
			mensagemDeErro.append(" em tempo de execução");
		} else if (erro instanceof IOException) {
			mensagemDeErro.append(" de entrada/saída");
		}
		if ((linha != -1) && (coluna != -1)) {
			posicionarCursor(linha, coluna);
			editor.setCurrentLineHighlightColor(temaAtual.getCorDaLinhaComErro());
			destacarLinhaComErro();
			mensagemDeErro.append(" na linha ").append(linha).append(" coluna ").append(coluna);
		}
		mensagemDeErro.append("\n").append(erro.getLocalizedMessage());
		atualizarBarraDeStatus(mensagemDeErro.toString());
	}

	// TODO Verificar acessibilidade
	// http://docs.oracle.com/javase/tutorial/uiswing/misc/access.html
	
}