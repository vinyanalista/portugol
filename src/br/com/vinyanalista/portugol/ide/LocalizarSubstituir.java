package br.com.vinyanalista.portugol.ide;
//http://web.njit.edu/~pby2/FindReplace.htm
//http://www.javafaq.nu/java-bookpage-31-8.html

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.fife.ui.rtextarea.*;

import java.awt.event.*;

public class LocalizarSubstituir extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public static final int OPERACAO_LOCALIZAR = 0;
	public static final int OPERACAO_SUBSTITUIR = 1;

	private final JTabbedPane abas;
	
	private final Acao acaoLocalizar;
	private final Acao acaoLocalizarProxima;
	private final Acao acaoSubstituir;
	
	private final JRadioButton botaoAbaixo;
	
	private final JButton botaoLocalizarProxima1;
	private final JButton botaoLocalizarProxima2;
	private final JButton botaoSubstituir;
	private final JButton botaoSubstituirTudo;
	
	private final JTextField campoLocalizar1;
	private final JTextField campoLocalizar2;
	private final JTextField campoSubstituir;
	
	private final JCheckBox checkboxDiferenciar1;
	private final JCheckBox checkboxDiferenciar2;
	
	private final TelaPrincipalMinima telaPrincipal;
	
	private final RTextArea textArea;
	
	private String ultimaBusca = null;
	
	private void alertarSeNaoEncontrar(String busca, SearchResult resultado) {
		if (!resultado.wasFound()) {
			JOptionPane.showMessageDialog(getOwner(), "Não é possível encontrar \"" + busca + "\"");
		}
	}
	
	private SearchContext configurarBusca(String busca) {
		SearchContext configuracaoDaBusca = new SearchContext();
		configuracaoDaBusca.setMatchCase(getDiferenciarMaisculasDeMinusculas());
		configuracaoDaBusca.setRegularExpression(false);
		configuracaoDaBusca.setSearchFor(busca);
		configuracaoDaBusca.setWholeWord(false);
		ultimaBusca = busca;
		return configuracaoDaBusca;
	}
	
	private void definirBotaoPadrao() {
		switch (abas.getSelectedIndex()) {
		case OPERACAO_LOCALIZAR:
			getRootPane().setDefaultButton(botaoLocalizarProxima1);
			break;
		case OPERACAO_SUBSTITUIR:
			getRootPane().setDefaultButton(botaoSubstituir);
			break;
		}
	}
	
	public void esconderDialogo() {
		super.setVisible(false);
	}
	
	private void focarCampoPadrao() {
		switch (abas.getSelectedIndex()) {
		case OPERACAO_LOCALIZAR:
			campoLocalizar1.grabFocus();
			break;
		case OPERACAO_SUBSTITUIR:
			campoLocalizar2.grabFocus();
			break;
		}
	}
	
	public Acao getAcaoLocalizar() {
		return acaoLocalizar;
	}
	
	public Acao getAcaoLocalizarProxima() {
		return acaoLocalizarProxima;
	}
	
	public Acao getAcaoSubstituir() {
		return acaoSubstituir;
	}
	
	private String getBusca() {
		return campoLocalizar1.getText();
	}
	
	private boolean getBuscarAbaixo() {
		return botaoAbaixo.isSelected();
	}
	
	private boolean getDiferenciarMaisculasDeMinusculas() {
		return checkboxDiferenciar1.isSelected();
	}
	
	private String getSubstituicao() {
		return campoSubstituir.getText();
	}
	
	private void localizar() {
		localizarProxima(getBusca());
		verificarAcoes();
	}
	
	public void localizarProxima() {
		if (ultimaBusca != null) {
			localizarProxima(ultimaBusca);
		}
	}

	private void localizarProxima(String busca) {
		SearchContext configuracaoDaBusca = configurarBusca(busca);
		configuracaoDaBusca.setSearchForward(getBuscarAbaixo());
		SearchResult resultado = SearchEngine.find(textArea, configuracaoDaBusca);
		alertarSeNaoEncontrar(busca, resultado);
	}
	
	public void mostrarDialogo(int operacao) {
		// Centraliza o diálogo em relação à tela principal
		TelaPrincipalMinima.centralizarDialogo(this, telaPrincipal);
		// Seleciona a aba correspondente à operação desejada
		abas.setSelectedIndex(operacao);
		// Sugere localizar o texto selecionado, se houver
		String textoSelecionado = textArea.getSelectedText();
		if (textoSelecionado != null) {
			campoLocalizar1.setText(textoSelecionado);
		}
		// Exibe o diálogo
		super.setVisible(true);
		focarCampoPadrao();
		verificarBotoes();
	}
	
	@Deprecated
	@Override
	public void setVisible(boolean arg0) {
		// Não faz nada
	};
	
	private void substituir() {
		String busca = getBusca();
		SearchContext configuracaoDaBusca = configurarBusca(getBusca());
		configuracaoDaBusca.setSearchForward(true);
		configuracaoDaBusca.setReplaceWith(getSubstituicao());
		SearchResult resultado = SearchEngine.replace(textArea, configuracaoDaBusca);
		alertarSeNaoEncontrar(busca, resultado);
		verificarAcoes();
	}
	
	private void substituirTudo() {
		String busca = getBusca();
		SearchContext configuracaoDaBusca = configurarBusca(busca);
		configuracaoDaBusca.setReplaceWith(getSubstituicao());
		SearchResult resultado = SearchEngine.replaceAll(textArea, configuracaoDaBusca);
		alertarSeNaoEncontrar(busca, resultado);
		verificarAcoes();
	}

	public LocalizarSubstituir(TelaPrincipalMinima telaPrincipal) {
		super();
		setModal(false);
		setTitle("Localizar/substituir");
		
		this.telaPrincipal = telaPrincipal;
		this.textArea = telaPrincipal.getEditor();

        abas = new JTabbedPane();
        
        JPanel abaLocalizar = new JPanel();
        JLabel labelLocalizar1 = new JLabel();
        campoLocalizar1 = new JTextField();
        checkboxDiferenciar1 = new JCheckBox();
        JPanel painelDirecao = new JPanel();
        ButtonGroup grupoDirecao = new ButtonGroup();
        JRadioButton botaoAcima = new JRadioButton();
        botaoAbaixo = new JRadioButton();
        botaoLocalizarProxima1 = new JButton();
        JButton botaoCancelar1 = new JButton();
        
        JPanel abaSubstituir = new JPanel();
        JLabel labelLocalizar2 = new JLabel();
        campoLocalizar2 = new JTextField();
        JLabel labelSubstituir = new JLabel();
        campoSubstituir = new JTextField();
        checkboxDiferenciar2 = new JCheckBox();
        botaoLocalizarProxima2 = new JButton();
        botaoSubstituir = new JButton();
        botaoSubstituirTudo = new JButton();
        JButton botaoCancelar2 = new JButton();
	    
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evento) {
				esconderDialogo();
			}
			
			@Override
			public void windowOpened(WindowEvent evento) {
				setIconImage(Icone.obterIcone(Icone.PORTUGOL).getImage());
			}
		});
        
        /* Aba Localizar */
        
        labelLocalizar1.setDisplayedMnemonic('o');
        labelLocalizar1.setLabelFor(campoLocalizar1);
        labelLocalizar1.setText("Localizar:");
        
        campoLocalizar1.setText("");
        campoLocalizar1.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				verificarBotoes();
			}
		});
        
        Document conteudoDoCampoLocalizar = campoLocalizar1.getDocument();
        
        checkboxDiferenciar1.setMnemonic('m');
        checkboxDiferenciar1.setSelected(false);
        checkboxDiferenciar1.setText("Diferenciar maiúsculas de minúsculas");
        checkboxDiferenciar1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
            	checkboxDiferenciar2.setSelected(checkboxDiferenciar1.isSelected());
            }
        });
        
        painelDirecao.setBorder(BorderFactory.createTitledBorder("Direção"));
        
        botaoAcima.setMnemonic('a');
        botaoAcima.setText("Acima");
        grupoDirecao.add(botaoAcima);
        
        botaoAbaixo.setMnemonic('x');
        botaoAbaixo.setSelected(true);
        botaoAbaixo.setText("Abaixo");
        grupoDirecao.add(botaoAbaixo);
        
        ActionListener acaoDoBotaoLocalizarProxima = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	localizar();
            }
        }; 

        Icon iconeLocalizar = Icone.obterIcone(Icone.LOCALIZAR);
        
        botaoLocalizarProxima1.setIcon(iconeLocalizar);
        botaoLocalizarProxima1.setMnemonic('l');
        botaoLocalizarProxima1.setText("Localizar próxima");
        botaoLocalizarProxima1.addActionListener(acaoDoBotaoLocalizarProxima);
        
        ActionListener acaoDoBotaoCancelar = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	esconderDialogo();
            }
        };

        Icon iconeCancelar = Icone.obterIcone(Icone.CANCELAR);
        
        botaoCancelar1.setIcon(iconeCancelar);
        botaoCancelar1.setText("Cancelar");
        botaoCancelar1.addActionListener(acaoDoBotaoCancelar);
        botaoCancelar1.registerKeyboardAction(acaoDoBotaoCancelar, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        javax.swing.GroupLayout painelDirecaoLayout = new javax.swing.GroupLayout(painelDirecao);
        painelDirecao.setLayout(painelDirecaoLayout);
        painelDirecaoLayout.setHorizontalGroup(
            painelDirecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelDirecaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botaoAcima)
                .addGap(18, 18, 18)
                .addComponent(botaoAbaixo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        painelDirecaoLayout.setVerticalGroup(
            painelDirecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelDirecaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelDirecaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoAcima)
                    .addComponent(botaoAbaixo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        javax.swing.GroupLayout abaLocalizarLayout = new javax.swing.GroupLayout(abaLocalizar);
        abaLocalizar.setLayout(abaLocalizarLayout);
        abaLocalizarLayout.setHorizontalGroup(
            abaLocalizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abaLocalizarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abaLocalizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(abaLocalizarLayout.createSequentialGroup()
                        .addComponent(labelLocalizar1)
                        .addGap(18, 18, 18)
                        .addComponent(campoLocalizar1))
                    .addGroup(abaLocalizarLayout.createSequentialGroup()
                        .addComponent(checkboxDiferenciar1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(painelDirecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(abaLocalizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botaoCancelar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botaoLocalizarProxima1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        abaLocalizarLayout.setVerticalGroup(
            abaLocalizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abaLocalizarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abaLocalizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLocalizar1)
                    .addComponent(campoLocalizar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botaoLocalizarProxima1))
                .addGroup(abaLocalizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(abaLocalizarLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(abaLocalizarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(botaoCancelar1)
                            .addComponent(painelDirecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(abaLocalizarLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(checkboxDiferenciar1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        abas.addTab("Localizar", iconeLocalizar, abaLocalizar);
        
        /* Aba Substituir */
        
        labelLocalizar2.setDisplayedMnemonic('o');
        labelLocalizar2.setLabelFor(campoLocalizar2);
        labelLocalizar2.setText("Localizar:");

        campoLocalizar2.setDocument(conteudoDoCampoLocalizar);
        campoLocalizar2.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				verificarBotoes();
			}
		});

        labelSubstituir.setDisplayedMnemonic('b');
        labelSubstituir.setLabelFor(campoSubstituir);
        labelSubstituir.setText("Substituir por:");
        
        campoSubstituir.setText("");        
        campoSubstituir.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				verificarBotoes();
			}
		});

        checkboxDiferenciar2.setMnemonic('d');
        checkboxDiferenciar2.setSelected(false);
        checkboxDiferenciar2.setText("Diferenciar maiúsculas de minúsculas");
        checkboxDiferenciar2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
            	checkboxDiferenciar1.setSelected(checkboxDiferenciar2.isSelected());
            }
        });

        botaoLocalizarProxima2.setIcon(iconeLocalizar);
        botaoLocalizarProxima2.setMnemonic('l');
        botaoLocalizarProxima2.setText("Localizar próxima");
        botaoLocalizarProxima2.addActionListener(acaoDoBotaoLocalizarProxima);

        Icon iconeSubstituir = Icone.obterIcone(Icone.SUBSTITUIR);
        
        botaoSubstituir.setIcon(iconeSubstituir);
        botaoSubstituir.setMnemonic('s');
        botaoSubstituir.setText("Substituir");
        botaoSubstituir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	substituir();
            }
        });
        
        botaoSubstituirTudo.setMnemonic('t');
        botaoSubstituirTudo.setText("Substituir tudo");
        botaoSubstituirTudo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	substituirTudo();
            }
        });

        botaoCancelar2.setIcon(iconeCancelar);
        botaoCancelar2.setText("Cancelar");
        botaoCancelar2.addActionListener(acaoDoBotaoCancelar);
        botaoCancelar2.registerKeyboardAction(acaoDoBotaoCancelar, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        javax.swing.GroupLayout abaSubstituirLayout = new javax.swing.GroupLayout(abaSubstituir);
        abaSubstituir.setLayout(abaSubstituirLayout);
        abaSubstituirLayout.setHorizontalGroup(
            abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, abaSubstituirLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkboxDiferenciar2)
                    .addGroup(abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, abaSubstituirLayout.createSequentialGroup()
                            .addComponent(labelLocalizar2)
                            .addGap(41, 41, 41)
                            .addComponent(campoLocalizar2, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, abaSubstituirLayout.createSequentialGroup()
                            .addComponent(labelSubstituir)
                            .addGap(18, 18, 18)
                            .addComponent(campoSubstituir))))
                .addGap(18, 18, 18)
                .addGroup(abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(botaoLocalizarProxima2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botaoSubstituir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botaoSubstituirTudo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botaoCancelar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        abaSubstituirLayout.setVerticalGroup(
            abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(abaSubstituirLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLocalizar2)
                    .addComponent(campoLocalizar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botaoLocalizarProxima2))
                .addGroup(abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(abaSubstituirLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelSubstituir)
                            .addComponent(campoSubstituir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(abaSubstituirLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botaoSubstituir)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(abaSubstituirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoSubstituirTudo)
                    .addComponent(checkboxDiferenciar2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botaoCancelar2)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        abas.addTab("Substituir", iconeSubstituir, abaSubstituir);
        
		abas.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				definirBotaoPadrao();
			}
		});
		
		getContentPane().add(abas);

        pack();
		
		/* Ações */
		
		acaoLocalizar = new Acao("Localizar...", iconeLocalizar, "Localiza uma sequência de caracteres no código-fonte.", KeyStroke
				.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarDialogo(OPERACAO_LOCALIZAR);
			}
		};

		acaoLocalizarProxima = new Acao("Localizar próxima", null, "Localiza a próxima ocorrência da última sequência de caracteres pesquisada.", KeyStroke.getKeyStroke("F3")) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				localizarProxima();
			}
		};

		acaoSubstituir = new Acao("Substituir...", iconeSubstituir, "Localiza e substitui uma sequência de caracteres por outra no código-fonte.", KeyStroke
				.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK)) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarDialogo(OPERACAO_SUBSTITUIR);
			}
		};
		
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				verificarAcoes();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				verificarAcoes();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verificarAcoes();
			}
		});
	}
	
	public void verificarAcoes() {
		boolean haTexto = !textArea.getText().isEmpty();
		acaoLocalizar.setEnabled(haTexto);
		acaoLocalizarProxima.setEnabled(haTexto && (ultimaBusca != null));
		acaoSubstituir.setEnabled(haTexto);
	}

	private void verificarBotoes() {
		String busca = getBusca();
		boolean habilitar = (busca != null) && (!busca.isEmpty());
		botaoLocalizarProxima1.setEnabled(habilitar);
		botaoLocalizarProxima2.setEnabled(habilitar);
		botaoSubstituir.setEnabled(habilitar);
		botaoSubstituirTudo.setEnabled(habilitar);
	}

}