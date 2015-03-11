package br.com.vinyanalista.portugol.ide;

import java.awt.Color;
import java.io.IOException;

import org.fife.ui.rsyntaxtextarea.*;

public enum Tema {
	DARK("dark.xml", "Escuro"), DEFAULT_ALT("default-alt.xml",
			"Padrão do RSyntaxTextArea (alternativo)"), DEFAULT("default.xml",
			"Padrão do RSyntaxTextArea"), ECLIPSE("eclipse.xml", "Eclipse"), IDEA(
			"idea.xml", "IntelliJ Idea"), VS("vs.xml", "Visual Studio");

	public void aplicar(RSyntaxTextArea editor) {
		try {
			Theme theme = Theme.load(getClass().getResourceAsStream("/" + arquivo));
			theme.apply(editor);
			corDaLinhaNormal = editor.getCurrentLineHighlightColor();
		} catch (IOException excecao) {
			excecao.printStackTrace();
		}
	}

	private final String arquivo;
	private Color corDaLinhaComErro;
	private Color corDaLinhaNormal;
	private final String nome;

	private Tema(String arquivo, String nome) {
		this.arquivo = arquivo;
		corDaLinhaComErro = new Color(255, 200, 200);
		this.nome = nome;
		corDaLinhaNormal = null;
	}
	
	public Color getCorDaLinhaComErro() {
		return corDaLinhaComErro;
	}
	
	public Color getCorDaLinhaNormal() {
		return corDaLinhaNormal;
	}
	
	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}

}
