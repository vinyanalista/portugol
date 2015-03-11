package br.com.vinyanalista.portugol.ide;

import java.io.*;
import java.util.*;

import javax.swing.*;

public class TelaPrincipalCompletaDesktop extends TelaPrincipalCompleta {
	private static final long serialVersionUID = 1L;
	
	protected final JFileChooser dialogoArquivo;

	public TelaPrincipalCompletaDesktop(JFrame janelaDoAplicativo) {
		super(janelaDoAplicativo);
		
        /* Diálogo de arquivo */

		dialogoArquivo = new JFileChooser();
		ExampleFileFilter filtroDeArquivos = new ExampleFileFilter();
		filtroDeArquivos.addExtension(EXTENSAO);
		filtroDeArquivos.setDescription(TIPO_DE_ARQUIVO);
		dialogoArquivo.setFileFilter(filtroDeArquivos);
	}
	
	@Override
	protected String lerDoArquivo(String caminhoDoArquivo) throws FileNotFoundException, IOException {
		StringBuffer codigo = null;
		File arquivo = new File(caminhoDoArquivo);
		Reader leitor = new FileReader(arquivo);
		char[] buffer = new char[100000];
		int numeroDeCaracteresLidos;
		codigo = new StringBuffer();
		while ((numeroDeCaracteresLidos = leitor.read(buffer, 0, buffer.length)) != -1)
			codigo.append(new String(buffer, 0, numeroDeCaracteresLidos));
		leitor.close();
		return codigo.toString();
	}
	
	@Override
	protected String mostrarDialogoAbrirArquivo() {
		if (dialogoArquivo.showOpenDialog(janelaDoAplicativo) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		if (arquivoModificado && !confirmarAntesDeDescartarAlteracoes()) {
			return null;
		}
		return dialogoArquivo.getSelectedFile().getPath();
	}

	@Override
	protected String mostrarDialogoSalvarArquivo() {
		String caminhoDoArquivo = null;
		if (dialogoArquivo.showSaveDialog(janelaDoAplicativo) != JFileChooser.APPROVE_OPTION) {
			// Usuário cancelou
			return null;
		}
		// Adiciona extensão ao nome do arquivo
		caminhoDoArquivo = dialogoArquivo.getSelectedFile().getPath();
		caminhoDoArquivo = adicionarExtensaoAutomaticamente(caminhoDoArquivo);
		File arquivo = new File(caminhoDoArquivo);
		// Verifica se o arquivo já existe
		if (arquivo.exists()) {
			String[] opcoes = { "Sobrescrever", "Cancelar" };
			int resposta = JOptionPane.showOptionDialog(this,
					"Já existe um arquivo chamado \"" + arquivo.getName()
							+ "\". Tem certeza de que deseja sobrescrevê-lo?",
					"Sobrescrever o arquivo? - " + NOME_DO_PROGRAMA,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, opcoes, opcoes[1]);
			if (resposta == JOptionPane.NO_OPTION) {
				// Arquivo já existe e usuário não deseja sobrescrevê-lo
				return null;
			}
		}
		return caminhoDoArquivo;
	}
	
	@Override
	protected void salvarNoArquivo(String codigoFonte, String caminhoDoArquivo) throws IOException {
		PrintWriter impressora = new PrintWriter(new FileWriter(new File(caminhoDoArquivo)));
		StringTokenizer codigoFonteComQuebraDeLinha = new StringTokenizer(
				codigoFonte, System.getProperty(PROPRIEDADE_FIM_DE_LINHA));
		while (codigoFonteComQuebraDeLinha.hasMoreTokens()) {
			impressora.println(codigoFonteComQuebraDeLinha.nextToken());
		}
		impressora.close();
	}

}