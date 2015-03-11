package br.com.vinyanalista.portugol.ide;
//http://www.planet-source-code.com/vb/scripts/showcode.asp?lngWId=2&txtCodeId=3153
//http://www.javaworld.com/jw-06-1998/jw-06-undoredo.html

import java.io.*;
import java.awt.event.*;

import javax.swing.*;

import br.com.vinyanalista.portugol.ide.componente.ItemDeMenu;
import br.com.vinyanalista.portugol.interpretador.*;

public abstract class TelaPrincipalCompleta extends TelaPrincipalMinima {
	private static final long serialVersionUID = 1L;
	
    protected static final String TIPO_DE_ARQUIVO = "Código em Portugol";
    protected static final String EXTENSAO = "por";
    protected static final String ARQUIVO_SEM_NOME = "Sem nome";
    
	protected String caminhoDoArquivo = null;
	protected final JFrame janelaDoAplicativo;

	/* Ações */
	
	protected Acao acaoAbrir, acaoSalvar, acaoSalvarComo, acaoSair;

	/* Menus */
	
	protected JMenuItem itemDeMenuAbrir, itemDeMenuSalvar,
			itemDeMenuSalvarComo, itemDeMenuSair;

	/* Barra de ferramentas */
	
	protected BotaoDaBarraDeFerramentas botaoAbrir, botaoSalvar,
			botaoSalvarComo, botaoSair;
	
	public TelaPrincipalCompleta(JFrame janelaDoAplicativo) {
		super();
		
		this.janelaDoAplicativo = janelaDoAplicativo;
		
		novo();
	}
	
	public void abrir() {
		String caminhoDoArquivoDesejado = mostrarDialogoAbrirArquivo();
		if (caminhoDoArquivoDesejado == null) {
			return;
		}
		caminhoDoArquivoDesejado = adicionarExtensaoAutomaticamente(caminhoDoArquivoDesejado);
		String codigo;
		try {
			codigo = lerDoArquivo(caminhoDoArquivoDesejado);
		} catch (FileNotFoundException excecao) {
			atualizarBarraDeStatus("Erro: arquivo não encontrado");
			return;
		} catch (IOException excecao) {
			atualizarBarraDeStatus("Erro de leitura/escrita");
			return;
		}
		// Atualiza a interface gráfica
		acompanharMudancas = false;
        arquivoModificado = false;
		this.caminhoDoArquivo = caminhoDoArquivoDesejado;
		setTitle(obterNomeDoArquivo());
		editor.setText(codigo);
		editor.discardAllEdits(); // Limpa histórico do desfazer/refazer
		acompanharMudancas = true;
	}
	
	@Override
	protected boolean abrirExemplo(Exemplo exemplo) {
		if (!super.abrirExemplo(exemplo)) {
			return false;
		}
        caminhoDoArquivo = null;
        return true;
	}
	
	protected String adicionarExtensaoAutomaticamente(String caminhoDoArquivo) {
		if (!caminhoDoArquivo.toLowerCase().endsWith("." + EXTENSAO.toLowerCase())) {
			caminhoDoArquivo = caminhoDoArquivo + "." + EXTENSAO;
        }
		return caminhoDoArquivo;
	}
	
	@Override
	protected void aoModificarArquivo() {
		super.aoModificarArquivo();
		if (!acompanharMudancas) {
            return;
        }
        if (arquivoModificado) {
        	setTitle(obterNomeDoArquivo() + " [modificado]");
        } else {
        	setTitle(obterNomeDoArquivo());
        }
	}
	
	protected void aoSalvar(String caminhoDoArquivo) {
		acompanharMudancas = false;
        arquivoModificado = false;
        this.caminhoDoArquivo = caminhoDoArquivo;
        setTitle(obterNomeDoArquivo());
        editor.discardAllEdits(); // Limpa histórico do desfazer/refazer
        acompanharMudancas = true;
	}

	@Override
	protected boolean confirmarAntesDeDescartarAlteracoes() {
		boolean podeProsseguir = true;
		String[] opcoes = {"Salvar", "Descartar", "Cancelar"};
		int resposta = JOptionPane.showOptionDialog(this, "O arquivo \"" + obterNomeDoArquivo() +"\" foi modificado.\n\nDeseja salvar as alterações ou descartá-las?", "Salvar ou descartar alterações - " + NOME_DO_PROGRAMA, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[2]);
		switch (resposta) {
			case JOptionPane.YES_OPTION:
				if (caminhoDoArquivo == null) {
					podeProsseguir = salvarComo();
				} else {
					podeProsseguir = salvar();
				}
				break;
			case JOptionPane.CANCEL_OPTION:
				podeProsseguir = false;
				break;
		}
		return podeProsseguir;
	}
	
	@Override
	@SuppressWarnings("serial")
	protected void criarAcoes() {
		super.criarAcoes();
		
		acaoAbrir = new Acao("Abrir...", Icone.obterIcone(Icone.ABRIR), "Abre um código-fonte existente.", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				abrir();
			}
		};

		acaoSalvar = new Acao("Salvar", Icone.obterIcone(Icone.SALVAR), "Salva o código-fonte.", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)) {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvar();				
			}
		};

		acaoSalvarComo = new Acao("Salvar como...", Icone.obterIcone(Icone.SALVAR_COMO), "Salva o código-fonte com outro nome.", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvarComo();
			}
		};

		acaoSair = new Acao("Sair", Icone.obterIcone(Icone.SAIR), "Fecha o Portugol Online.", KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK)) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sair();				
			}
		};
	}
	
	@Override
	protected void criarBotoes() {
		super.criarBotoes();
		
		botaoAbrir = new BotaoDaBarraDeFerramentas(acaoAbrir);
		
		botaoSalvar = new BotaoDaBarraDeFerramentas(acaoSalvar);
		
		botaoSalvarComo = new BotaoDaBarraDeFerramentas(acaoSalvarComo);

		botaoSair = new BotaoDaBarraDeFerramentas(acaoSair);
	}
	
	@Override
	protected void criarMenus() {
		super.criarMenus();
		
		itemDeMenuAbrir = new ItemDeMenu(acaoAbrir, tooltip);
		itemDeMenuAbrir.setMnemonic('b');

		itemDeMenuSalvar = new ItemDeMenu(acaoSalvar, tooltip);
		itemDeMenuSalvar.setMnemonic('r');

		itemDeMenuSalvarComo = new ItemDeMenu(acaoSalvarComo, tooltip);
		itemDeMenuSalvarComo.setMnemonic('c');

		itemDeMenuSair = new ItemDeMenu(acaoSair, tooltip);
		itemDeMenuSair.setMnemonic('s');
	}
	
	protected abstract String lerDoArquivo(String caminhoDoArquivo) throws FileNotFoundException, IOException;
	
	@Override
	protected void montarBarraDeFerramentas() {
		barraDeFerramentas.add(botaoNovo);
		barraDeFerramentas.add(botaoAbrir);
		barraDeFerramentas.add(botaoSalvar);
		barraDeFerramentas.add(botaoSalvarComo);
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
		barraDeFerramentas.addSeparator();
		barraDeFerramentas.add(botaoSair);
	}
	
	@Override
	protected void montarBarraDeMenus() {
		barraDeMenus.add(menuArquivo);
		menuArquivo.add(itemDeMenuNovo);
		menuArquivo.add(itemDeMenuAbrir);
		menuArquivo.add(submenuExemplos);
		menuArquivo.add(itemDeMenuSalvar);
		menuArquivo.add(itemDeMenuSalvarComo);
		menuArquivo.addSeparator();
		menuArquivo.add(itemDeMenuImprimir);
		menuArquivo.add(itemDeMenuExecutar);
		menuArquivo.addSeparator();
		menuArquivo.add(itemDeMenuSair);
		
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
	
	protected abstract String mostrarDialogoAbrirArquivo();
	
	protected abstract String mostrarDialogoSalvarArquivo();
	
	@Override
	public boolean novo() {
		if (!super.novo()) {
			return false;
		}
		caminhoDoArquivo = null;
		setTitle(obterNomeDoArquivo());
		return true;
	}
	
	protected String obterNomeDoArquivo() {
        return (caminhoDoArquivo == null) ? ARQUIVO_SEM_NOME : (new File(caminhoDoArquivo)).getName();
    }
	
	public void sair() {
		if (arquivoModificado && !confirmarAntesDeDescartarAlteracoes()) {
			return;
		}
		System.exit(0);
	}
	
	public boolean salvar() {
		if (caminhoDoArquivo == null) {
			// Se o arquivo é novo, age como salvar como
			return salvarComo();
		}
		// Grava conteúdo do editor no arquivo
		String codigoFonte = editor.getText();
		try {
			salvarNoArquivo(codigoFonte, caminhoDoArquivo);
			aoSalvar(caminhoDoArquivo);
			return true;
		} catch (IOException excecao) {
			atualizarBarraDeStatus("Erro de leitura/escrita");
			return false;
		}
	}

	public boolean salvarComo() {
		// Obtém o caminho do arquivo
		String caminhoDoArquivo = mostrarDialogoSalvarArquivo();
		if (caminhoDoArquivo == null) {
			return false;
		}
		// Grava conteúdo do editor no arquivo
		String codigoFonte = editor.getText();
		try {
			salvarNoArquivo(codigoFonte, caminhoDoArquivo);
			// Atualiza a interface gráfica
			aoSalvar(caminhoDoArquivo);
			return true;
		} catch (IOException excecao) {
			atualizarBarraDeStatus("Erro de leitura/escrita");
			return false;
		}
	}
	
	protected abstract void salvarNoArquivo(String codigoFonte, String caminhoDoArquivo) throws IOException;

	public void setTitle(String title) {
		if (janelaDoAplicativo != null) {
			janelaDoAplicativo.setTitle(title + " - " + NOME_DO_PROGRAMA);
		}
	}

}