package br.com.vinyanalista.portugol.ide;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import br.com.vinyanalista.portugol.interpretador.Interpretador;
import br.com.vinyanalista.portugol.interpretador.auxiliar.NumeraLinhas;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class RelatorioDaAnalise extends JDialog {
	private static class ReadOnlyTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public ReadOnlyTableModel(Object[][] cells, Object[] columns) {
			super(cells, columns);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	private final JScrollPane scrollPaneLog;
	private final JSplitPane abaArvoreSemantica;
	private final JSplitPane abaTabelaDeSimbolos;
	private final JTable tabelaDeAtributos1;
	private final JTable tabelaDeAtributos2;
	private NoComTabelaDeAtributos noSubrotinasDaLinguagem = null;

	private void aoSelecionarNoDaArvoreDeSimbolos(NoComTabelaDeAtributos no) {
		String[] colunas = { "Atributo", "Valor" };
		String[][] celulas = null;
		if (no != null) {
			// Obtém os atributos correspondentes ao nó selecionado
			TabelaDeAtributos tabelaDeAtributos = no.getTabelaDeAtributos();
			if (tabelaDeAtributos != null) {
				// Preenche a tabela com atributos e valores
				celulas = new String[tabelaDeAtributos.tamanho()][2];
				int linha = -1;
				for (Atributo atributo : tabelaDeAtributos.atributos()) {
					linha++;
					// Atributo
					celulas[linha][0] = atributo.toString();
					// Valor
					Object valor = tabelaDeAtributos.obter(atributo);
					celulas[linha][1] = valor.toString();
				}
			}
		}
		if (celulas == null) {
			celulas = new String[0][2];
		}
		this.tabelaDeAtributos1.setModel(new ReadOnlyTableModel(celulas, colunas));
		centralizarCelulas(this.tabelaDeAtributos1);
	}
	
	private void aoSelecionarNoDaArvoreSemantica(NoComTabelaDeAtributos no) {
		String[] colunas = { "Atributo", "Valor" };
		String[][] celulas = null;
		if (no != null) {
			// Obtém os atributos correspondentes ao nó selecionado
			TabelaDeAtributos tabelaDeAtributos = no.getTabelaDeAtributos();
			if (tabelaDeAtributos != null) {
				// Preenche a tabela com atributos e valores
				celulas = new String[tabelaDeAtributos.tamanho()][2];
				int linha = -1;
				for (Atributo atributo : tabelaDeAtributos.atributos()) {
					linha++;
					// Atributo
					celulas[linha][0] = atributo.toString();
					// Valor
					Object valor = tabelaDeAtributos.obter(atributo);
					celulas[linha][1] = valor.toString();
				}
			}
		}
		if (celulas == null) {
			celulas = new String[0][2];
		}
		this.tabelaDeAtributos2.setModel(new ReadOnlyTableModel(celulas, colunas));
		centralizarCelulas(this.tabelaDeAtributos2);
	}

	private void centralizarCelulas(JTable tabela) {
		// Centraliza os cabeçalhos
		DefaultTableCellRenderer renderizador;
		renderizador = (DefaultTableCellRenderer) tabela.getTableHeader()
				.getDefaultRenderer();
		renderizador.setHorizontalAlignment(javax.swing.JLabel.CENTER);
		// Centraliza cada célula
		renderizador = new DefaultTableCellRenderer();
		renderizador.setHorizontalAlignment(javax.swing.JLabel.CENTER);
		for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
			tabela.getColumnModel().getColumn(coluna).setCellRenderer(
					renderizador);
		}
	}
	
	private NoComTabelaDeAtributos renderizarArvoreDeSimbolos(
			TabelaDeSimbolos tabelaDeSimbolos) {
		final NoComTabelaDeAtributos noTabelaDeSimbolos = new NoComTabelaDeAtributos("Tabela de símbolos");
		final NoComTabelaDeAtributos noVariaveisGlobais = new NoComTabelaDeAtributos("Variáveis globais");
		final NoComTabelaDeAtributos noSubrotinasDoUsuario = new NoComTabelaDeAtributos("Sub-rotinas do usuário");
		noSubrotinasDaLinguagem = new NoComTabelaDeAtributos("Sub-rotinas da linguagem");
		List<Simbolo> listaOrdenadaDeSimbolos = tabelaDeSimbolos.obterListaOrdenadaDeSimbolos();
		for (Simbolo simbolo : listaOrdenadaDeSimbolos) {
			final TabelaDeAtributos tabelaDeAtributos = tabelaDeSimbolos.obter(simbolo);
			final String identificador = (String) tabelaDeAtributos.obter(Atributo.ID);
			final Tipo tipo = (Tipo) tabelaDeAtributos.obter(Atributo.TIPO);
			final NoComTabelaDeAtributos novoNo = new NoComTabelaDeAtributos(identificador);
			// Sub-rotinas da linguagem e sub-rotinas do usuário
			if (tipo instanceof TipoSubrotina || tipo instanceof TipoSubrotinaPredefinida) {
				TipoSubrotina tipoDaSubrotina = (TipoSubrotina) tipo;
				List<Simbolo> parametros = tipoDaSubrotina.getParametros();
				TabelaDeSimbolos variaveisLocais = tipoDaSubrotina.getTabelaDeSimbolos();
				if (parametros.size() > 0) {
					final NoComTabelaDeAtributos noParametros = new NoComTabelaDeAtributos("Parâmetros");
					for (Simbolo simboloDoParametro : parametros) {
						TabelaDeAtributos atributosDoParametro = variaveisLocais.obter(simboloDoParametro);
						String identificadorDoParametro = (String) atributosDoParametro.obter(Atributo.ID);
						NoComTabelaDeAtributos noDoParametro = new NoComTabelaDeAtributos(identificadorDoParametro);
						noDoParametro.setTabelaDeAtributos(atributosDoParametro);
						noParametros.add(noDoParametro);
					}
					novoNo.add(noParametros);
				}
				// As sub-rotinas do usuário também podem conter variáveis locais
				if (tipo instanceof TipoSubrotinaPredefinida) {
					noSubrotinasDaLinguagem.add(novoNo);
				} else {
					if (variaveisLocais.tamanho() - parametros.size() > 0) {
						List<Simbolo> listaOrdenadaDeVariaveisLocais = variaveisLocais.obterListaOrdenadaDeSimbolos();
						final NoComTabelaDeAtributos noVariaveisLocais = new NoComTabelaDeAtributos("Variáveis locais");
						for (Simbolo simboloDaVariavelLocal : listaOrdenadaDeVariaveisLocais) {
							TabelaDeAtributos atributosDaVariavelLocal = variaveisLocais.obter(simboloDaVariavelLocal);
							if (parametros.contains(simboloDaVariavelLocal)) {
								continue;
							}
							String identificadorDaVariavelLocal = (String) atributosDaVariavelLocal.obter(Atributo.ID);
							NoComTabelaDeAtributos noDaVariavelLocal = new NoComTabelaDeAtributos(identificadorDaVariavelLocal);
							noDaVariavelLocal.setTabelaDeAtributos(atributosDaVariavelLocal);
							noVariaveisLocais.add(noDaVariavelLocal);
						}
						novoNo.add(noVariaveisLocais);
					}
					noSubrotinasDoUsuario.add(novoNo);
				}
			} else {
				// Registros
				if (tipo instanceof TipoRegistro) {
					final NoComTabelaDeAtributos noCampos = new NoComTabelaDeAtributos("Campos");
					TabelaDeSimbolos campos = ((TipoRegistro) tipo).getCampos();
					List<Simbolo> listaOrdenadaDeCampos = campos.obterListaOrdenadaDeSimbolos();
					for (Simbolo campo : listaOrdenadaDeCampos) {
						TabelaDeAtributos atributosDoCampo = campos.obter(campo);
						String identificadorDoCampo = (String) atributosDoCampo.obter(Atributo.ID);
						NoComTabelaDeAtributos noDoCampo = new NoComTabelaDeAtributos(identificadorDoCampo);
						noDoCampo.setTabelaDeAtributos(atributosDoCampo);
						noCampos.add(noDoCampo);
					}
					novoNo.add(noCampos);
				// Vetores e matrizes
				} else if (tipo instanceof TipoVetorOuMatriz) {
					TipoVetorOuMatriz tipoDoVetorOuMatriz = (TipoVetorOuMatriz) tipo;
					Tipo tipoDasCelulas = tipoDoVetorOuMatriz.getTipoDasCelulas();
					if (tipoDasCelulas instanceof TipoRegistro) {
						final NoComTabelaDeAtributos noCampos = new NoComTabelaDeAtributos("Campos");
						TabelaDeSimbolos campos = ((TipoRegistro) tipoDasCelulas).getCampos();
						List<Simbolo> listaOrdenadaDeCampos = campos.obterListaOrdenadaDeSimbolos();
						for (Simbolo campo : listaOrdenadaDeCampos) {
							TabelaDeAtributos atributosDoCampo = campos.obter(campo);
							String identificadorDoCampo = (String) atributosDoCampo.obter(Atributo.ID);
							NoComTabelaDeAtributos noDoCampo = new NoComTabelaDeAtributos(identificadorDoCampo);
							noDoCampo.setTabelaDeAtributos(atributosDoCampo);
							noCampos.add(noDoCampo);
						}
						novoNo.add(noCampos);
					}
				}
				noVariaveisGlobais.add(novoNo);
			}
			novoNo.setTabelaDeAtributos(tabelaDeAtributos);
		}
		if (!noVariaveisGlobais.isLeaf()) {
			noTabelaDeSimbolos.add(noVariaveisGlobais);
		}
		if (!noSubrotinasDoUsuario.isLeaf()) {
			noTabelaDeSimbolos.add(noSubrotinasDoUsuario);
		}
		noTabelaDeSimbolos.add(noSubrotinasDaLinguagem);
		return noTabelaDeSimbolos;
	}

	/* private ReadOnlyTableModel gerarTabelaDeSimbolos(
			TabelaDeSimbolos tabelaDeSimbolos) {
		String[] colunas = { "ID", "Linha", "Coluna", "Tipo",
				"Somente leitura", "Valor" };
		String[][] celulas = new String[tabelaDeSimbolos.tamanho()][6];
		for (int l = 0; l < tabelaDeSimbolos.tamanho(); l++) {
			TabelaDeAtributos tabelaDeAtributos = tabelaDeSimbolos.obter(l);
			// Obtém atributos
			String id = (String) tabelaDeAtributos.obter(Atributo.ID);
			Integer linha = (Integer) tabelaDeAtributos.obter(Atributo.LINHA);
			Integer coluna = (Integer) tabelaDeAtributos.obter(Atributo.COLUNA);
			Tipo tipo = (Tipo) tabelaDeAtributos.obter(Atributo.TIPO);
			Boolean somenteLeitura = (Boolean) tabelaDeAtributos
					.obter(Atributo.SOMENTE_LEITURA);
			Object valor = tabelaDeAtributos.obter(Atributo.VALOR);
			// Preenche a tabela com os atributos obtidos
			celulas[l][0] = id;
			celulas[l][1] = (linha == null ? "-" : String.valueOf(linha));
			celulas[l][2] = (coluna == null ? "-" : String.valueOf(coluna));
			celulas[l][3] = (tipo == null ? "-" : tipo.toString());
			celulas[l][4] = (somenteLeitura == null ? "-"
					: (somenteLeitura ? "sim" : "não"));
			celulas[l][5] = (valor == null ? "-" : valor.toString());
		}
		return new ReadOnlyTableModel(celulas, colunas);
	} */

	//public RelatorioDaAnalise(TelaPrincipal telaPrincipal, String log, Interpretador compilador) {
	public RelatorioDaAnalise(Interpretador interpretador) {
		super();
		setLayout(new BorderLayout(5, 5));
		setModal(false);
		setTitle("Relatório da análise");

		JTabbedPane abas = new JTabbedPane();

		JPanel abaLog = new JPanel();
		scrollPaneLog = new JScrollPane();
		JTextArea textAreaLog = new JTextArea();

		JScrollPane scrollPaneArvoreDeSimbolos = new JScrollPane();
		JTree arvoreDeSimbolos = new JTree();
		JScrollPane scrollPaneTabelaDeAtributos1 = new JScrollPane();
		tabelaDeAtributos1 = new JTable();
		abaTabelaDeSimbolos = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				scrollPaneArvoreDeSimbolos, scrollPaneTabelaDeAtributos1);
		
		JScrollPane scrollPaneArvoreSemantica = new JScrollPane();
		JTree arvoreSemantica = new JTree();
		JScrollPane scrollPaneTabelaDeAtributos2 = new JScrollPane();
		tabelaDeAtributos2 = new JTable();
		abaArvoreSemantica = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				scrollPaneArvoreSemantica, scrollPaneTabelaDeAtributos2);

		JPanel painelFechar = new JPanel();
		JButton botaoFechar = new JButton("Fechar");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		/* Aba Log */

		abaLog.setLayout(new BoxLayout(abaLog, BoxLayout.LINE_AXIS));

		textAreaLog.setEditable(false);
		textAreaLog.setText(NumeraLinhas.numerar(interpretador.getLog()));

		scrollPaneLog.setViewportView(textAreaLog);

		abaLog.add(scrollPaneLog);
		abaLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		abas.addTab("Log", abaLog);
		
		/* Aba Tabela de símbolos */
		
		// Adiciona ícones à árvore
		// http://www.javafaq.nu/java-bookpage-28-2.html
		DefaultTreeCellRenderer renderizadorDeIcones = new DefaultTreeCellRenderer();
		renderizadorDeIcones.setClosedIcon(Icone.obterIcone(Icone.PASTA));
		renderizadorDeIcones.setOpenIcon(Icone.obterIcone(Icone.PASTA));
		renderizadorDeIcones.setLeafIcon(Icone.obterIcone(Icone.NO_RAIZ));
		arvoreDeSimbolos.setCellRenderer(renderizadorDeIcones);
		
		arvoreDeSimbolos.setModel(new DefaultTreeModel(renderizarArvoreDeSimbolos(interpretador.getTabelaDeSimbolos())));
		arvoreDeSimbolos.setShowsRootHandles(true);
		arvoreDeSimbolos.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evento) {
				NoComTabelaDeAtributos noSelecionado = (NoComTabelaDeAtributos) evento
						.getPath().getLastPathComponent();
				aoSelecionarNoDaArvoreDeSimbolos(noSelecionado);
			}
		});
		
		// Expande todos os nós da árvore de símbolos
		for (int linha = 0; linha < arvoreDeSimbolos.getRowCount(); linha++) {
			arvoreDeSimbolos.expandRow(linha);
		}
		
		arvoreDeSimbolos.setSelectionRow(0);

		scrollPaneArvoreDeSimbolos.setViewportView(arvoreDeSimbolos);

		scrollPaneTabelaDeAtributos1.setViewportView(tabelaDeAtributos1);

		abaTabelaDeSimbolos.setOneTouchExpandable(true);

		abas.addTab("Tabela de símbolos", abaTabelaDeSimbolos);
	
		/* Aba Árvore semântica */

		arvoreSemantica.setCellRenderer(renderizadorDeIcones);
		
		arvoreSemantica.setModel(new DefaultTreeModel(new RenderizadorDeArvoreSemantica(interpretador).renderizar()));
		arvoreSemantica.setShowsRootHandles(true);
		arvoreSemantica.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evento) {
				NoComTabelaDeAtributos noSelecionado = (NoComTabelaDeAtributos) evento
						.getPath().getLastPathComponent();
				aoSelecionarNoDaArvoreSemantica(noSelecionado);
			}
		});
		
		// Expande todos os nós da árvore semântica
		for (int linha = 0; linha < arvoreSemantica.getRowCount(); linha++) {
			arvoreSemantica.expandRow(linha);
		}
		
		arvoreSemantica.setSelectionRow(0);

		scrollPaneArvoreSemantica.setViewportView(arvoreSemantica);

		scrollPaneTabelaDeAtributos2.setViewportView(tabelaDeAtributos2);

		abaArvoreSemantica.setOneTouchExpandable(true);

		abas.addTab("Árvore semântica", abaArvoreSemantica);
		
		add(abas, BorderLayout.CENTER);

		ActionListener acaoDoBotaoFechar = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	RelatorioDaAnalise.this.setVisible(false);
            }
        };
        
        Icon iconeFechar = Icone.obterIcone(Icone.SAIR);
		
        botaoFechar.addActionListener(acaoDoBotaoFechar);
        botaoFechar.setIcon(iconeFechar);
        botaoFechar.registerKeyboardAction(acaoDoBotaoFechar, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		painelFechar.add(botaoFechar);

		add(painelFechar, BorderLayout.PAGE_END);

		setMinimumSize(new Dimension(640, 200));
		setPreferredSize(new Dimension(700, 480));

		pack();

		setResizable(true);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent evento) {
				setIconImage(Icone.obterIcone(Icone.PORTUGOL).getImage());
				scrollPaneLog.getVerticalScrollBar().setValue(0);
				abaTabelaDeSimbolos.setDividerLocation(0.3);
				abaArvoreSemantica.setDividerLocation(0.3);
			}
		});
	}

}