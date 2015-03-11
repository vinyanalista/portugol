package br.com.vinyanalista.portugol.ide;
//http://www.planet-source-code.com/vb/scripts/showcode.asp?lngWId=2&txtCodeId=3153
//http://www.javaworld.com/jw-06-1998/jw-06-undoredo.html

import java.io.*;

import javax.jnlp.*;
import javax.swing.*;

public class TelaPrincipalCompletaJavaWebStart extends TelaPrincipalCompleta {
	private static final long serialVersionUID = 1L;
	
    protected static final String JNLP_FILE_OPEN_SERVICE = "javax.jnlp.FileOpenService";
    protected static final String JNLP_FILE_SAVE_SERVICE = "javax.jnlp.FileSaveService";
    protected static final String JNLP_SERVICE_MANAGER = "javax.jnlp.ServiceManager";
    
    // http://stackoverflow.com/questions/216315/what-is-the-best-way-to-detect-whether-an-application-is-launched-by-webstart
    public static boolean executandoViaJavaWebStart() {
		try {
			Class.forName(JNLP_SERVICE_MANAGER);
			return true;
	    } catch (Exception excecao) {
	    	return false;
	    }
	}

	protected FileContents conteudoDoArquivo = null;
	protected final FileOpenService servicoDeAberturaDeArquivos;
	protected final FileSaveService servicoDeGravacaoDeArquivos;

	public TelaPrincipalCompletaJavaWebStart(JFrame janelaDoAplicativo) {
		super(janelaDoAplicativo);
		
        /* Diálogo de arquivo */

		// http://docs.oracle.com/javase/tutorial/deployment/doingMoreWithRIA/usingJNLPAPI.html
		FileOpenService servicoDeAberturaDeArquivos;
		FileSaveService servicoDeGravacaoDeArquivos;
		try {
			servicoDeAberturaDeArquivos = (FileOpenService) ServiceManager.lookup(JNLP_FILE_OPEN_SERVICE);
			servicoDeGravacaoDeArquivos = (FileSaveService) ServiceManager.lookup(JNLP_FILE_SAVE_SERVICE);
        } catch (Exception excecao) {
        	servicoDeAberturaDeArquivos = null;
        	servicoDeGravacaoDeArquivos = null;
        }
		this.servicoDeAberturaDeArquivos = servicoDeAberturaDeArquivos;
		this.servicoDeGravacaoDeArquivos = servicoDeGravacaoDeArquivos;
	}
	
	@Override
	protected String lerDoArquivo(String caminhoDoArquivo) throws IOException {
		StringBuffer codigo = new StringBuffer((int) conteudoDoArquivo.getLength());
		BufferedReader leitor = new BufferedReader(new InputStreamReader(conteudoDoArquivo.getInputStream()));
        String linha = leitor.readLine();
        while (linha != null) {
        	codigo.append(linha).append("\n");
        	linha = leitor.readLine();
        }
        leitor.close();
        return codigo.toString();
	}
	
	@Override
	protected String mostrarDialogoAbrirArquivo() {
		try {
			conteudoDoArquivo = servicoDeAberturaDeArquivos.openFileDialog(null, new String[] { EXTENSAO });
			if (conteudoDoArquivo == null) {
				atualizarBarraDeStatus("Erro: arquivo não encontrado");
				return null;
	        }
			return conteudoDoArquivo.getName();
		} catch (IOException excecao) {
			atualizarBarraDeStatus("Erro de leitura/escrita: não foi possível abrir o arquivo");
			return null;
		}
	}

	@Override
	protected String mostrarDialogoSalvarArquivo() {
		try {
			conteudoDoArquivo = servicoDeGravacaoDeArquivos
					.saveAsFileDialog(null, null, conteudoDoArquivo);
			return conteudoDoArquivo.getName();
		} catch (IOException excecao) {
			atualizarBarraDeStatus("Erro de leitura/escrita: não foi possível abrir o arquivo para gravação");
			return null;
		}
	}
	
	@Override
	public boolean novo() {
		if (!super.novo()) {
			return false;
		}
		conteudoDoArquivo = null;
		return true;
	}
	
	@Override
	protected void salvarNoArquivo(String codigoFonte, String caminhoDoArquivo) throws IOException {
		int tamanhoNecessario = codigoFonte.length() * 2;
        if (tamanhoNecessario > conteudoDoArquivo.getMaxLength()) {
        	conteudoDoArquivo.setMaxLength(tamanhoNecessario);
        }
		BufferedWriter arquivo = new BufferedWriter(
				new OutputStreamWriter(conteudoDoArquivo.getOutputStream(true)));
        arquivo.write(codigoFonte);
        arquivo.close();
	}

}