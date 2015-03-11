package br.com.vinyanalista.portugol.ide;

import javax.swing.ImageIcon;

public enum Icone {
	ABRIR("document-open.png"),
	AUMENTAR_FONTE("zoom-in.png"),
	CANCELAR("dialog-cancel.png"),
	COLAR("paste.png"),
	CONSOLE("console.png"),
	COPIAR("copy.png"),
	DATA_HORA("user-away.png"),
	DELETAR("delete.png"),
	DESFAZER("undo.png"),
	DIMINUIR_FONTE("zoom-out.png"),
	EXECUTAR("arrow-right.png"),
	IMPRIMIR("document-print.png"),
	LOCALIZAR("edit-find.png"),
	NO_RAIZ("user-online.png"),
	NOVO("document-new.png"),
	PASTA("folder.png"),
	PORTUGOL("brasil.png"),
	RECORTAR("cut.png"),
	REFAZER("redo.png"),
	RESTAURAR_TAMANHO_PADRAO_DE_FONTE("zoom-original.png"),
	SAIR("application-exit.png"),
	SALVAR("document-save.png"),
	SALVAR_COMO("document-save-as.png"),
	SELECIONAR_TUDO("selectall.png"),
	SOBRE("help-about.png"),
	SUBSTITUIR("edit-find-replace.png");

	private static final String PACOTE_DOS_ICONES = "br/com/vinyanalista/portugol/ide/icones/";

	public static ImageIcon obterIcone(Icone icone) {
		return new ImageIcon(TelaPrincipalMinima.class.getClassLoader()
				.getResource(PACOTE_DOS_ICONES + icone.nomeDoArquivo));
	}

	private final String nomeDoArquivo;

	private Icone(String nomeDoArquivo) {
		this.nomeDoArquivo = nomeDoArquivo;
	}

}