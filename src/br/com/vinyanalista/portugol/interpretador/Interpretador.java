package br.com.vinyanalista.portugol.interpretador;

import br.com.vinyanalista.portugol.base.lexer.*;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.base.parser.*;
import br.com.vinyanalista.portugol.interpretador.analise.*;
import br.com.vinyanalista.portugol.interpretador.auxiliar.*;
import br.com.vinyanalista.portugol.interpretador.execucao.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Interpretador implements EscutaDeExecutor {
	public static PushbackReader paraPushbackReader(String programaFonte) {
		return new PushbackReader(new InputStreamReader(
				new ByteArrayInputStream(programaFonte
						.getBytes(Charset.defaultCharset()))));
	}
	
	private static void tratarErro(Exception erro, Terminal terminal) {
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
			mensagemDeErro.append(" na linha ").append(linha).append(" coluna ").append(coluna);
		}
		mensagemDeErro.append("\n").append(erro.getLocalizedMessage());
		terminal.erro(mensagemDeErro.toString());
	}
	
	private Start arvoreSintaticaAbstrata;
	private final List<EscutaDeExecutor> escutasDeExecutor;
	private Exception ultimoErro;
    private String log;
    private TabelaDeSimbolos tabelaDeSimbolos;
    private HashMap<Node, TabelaDeAtributos> tabelasDeAtributos;
	private final Terminal terminal;

    public Interpretador(Terminal terminal) {
    	escutasDeExecutor = new ArrayList<EscutaDeExecutor>();
    	this.terminal = terminal;
		inicializar();
    }
    
    public void adicionarEscutaDeExecutor(EscutaDeExecutor escuta) {
		escutasDeExecutor.add(escuta);
	}

	public void analisar(String programaFonte) throws IOException,
			LexerException, ParserException, ErroSemantico {
		inicializar();
		try {
			// Obtém a árvore sintática abstrata reduzida (resultado das
			// análises léxica e sintática)
			Parser parser = new Parser(new CustomLexer(
					paraPushbackReader(programaFonte)));
			arvoreSintaticaAbstrata = parser.parse();
			// Obtém as tabelas de símbolos e de atributos (resultado da análise
			// semântica)
			AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico(
					arvoreSintaticaAbstrata);
			analisadorSemantico.analisar();
			log = analisadorSemantico.getLog();
			tabelaDeSimbolos = analisadorSemantico.getTabelaDeSimbolos();
			tabelasDeAtributos = analisadorSemantico.getTabelasDeAtributos();
		} catch (IOException erroDeES) {
			ultimoErro = erroDeES;
			throw erroDeES;
		} catch (LexerException erroLexico) {
			ultimoErro = new LexerException(erroLexico.getToken(),
					"Token desconhecido: " + erroLexico.getToken().getText());
			throw (LexerException) ultimoErro;
		} catch (ParserException erroSintatico) {
			ultimoErro = new ParserException(erroSintatico.getToken(), erroSintatico.getMessage().replaceAll("expecting", "Esperando um dos seguintes tokens"));
			throw (ParserException) ultimoErro;
		} catch (ErroSemantico erroSemantico) {
			ultimoErro = erroSemantico;
			throw erroSemantico;
		}
	}
	
	@Override
	public void aoEncerrarExecucao(ErroEmTempoDeExecucao erroEmTempoDeExecucao) {
		if (erroEmTempoDeExecucao != null) {
			tratarErro(erroEmTempoDeExecucao, terminal);
        	terminal.informacao("\nA execução do programa foi interrompida.");
        	terminal.encerrar();
		}
	}
    
	public void executar() throws IOException, LexerException, ParserException,
			ErroSemantico {
    	if (ultimoErro != null) {
    		if (ultimoErro instanceof IOException) {
    			throw (IOException) ultimoErro;
    		} else if (ultimoErro instanceof LexerException) {
    			throw (LexerException) ultimoErro;
    		} else if (ultimoErro instanceof ParserException) {
    			throw (ParserException) ultimoErro;
    		} else if (ultimoErro instanceof ErroSemantico) {
    			throw (ErroSemantico) ultimoErro;
    		}
    	}
    	if (arvoreSintaticaAbstrata != null) {
			Executor executor = new Executor(arvoreSintaticaAbstrata,
					tabelaDeSimbolos, tabelasDeAtributos, terminal);
			executor.adicionarEscuta(this);
			executor.adicionarEscutas(escutasDeExecutor);
			new Thread(executor).start();
    	}
    }
    
    public Start getArvoreSintatica() {
        return arvoreSintaticaAbstrata;
    }
    
    public TabelaDeAtributos getAtributos(Node no) {
        return tabelasDeAtributos.get(no);
    }
    
    public String getLog() {
        return log;
    }
    
    public TabelaDeSimbolos getTabelaDeSimbolos() {
        return tabelaDeSimbolos;
    }
    
    public Exception getUltimoErro() {
		return ultimoErro;
	}
    
    private void inicializar() {
    	arvoreSintaticaAbstrata = null;
    	log = "";
        tabelaDeSimbolos = new TabelaDeSimbolos();
        tabelasDeAtributos = new HashMap<Node, TabelaDeAtributos>();
    }

    public static void main(String[] args) {
    	String programaFonte = null;
    	Terminal terminal = new TerminalDeTexto();

        if (args.length > 0) {
            // Verifica se deve ser utilizado um exemplo ao invés de um arquivo
            if ((args[0].equals("--exemplo")) && (args.length == 2)) {
                try {
                    int id = Integer.parseInt(args[1]);
                    if (id <= Exemplo.values().length) {
                        Exemplo exemplo = Exemplo.values()[id - 1];
                        terminal.informacao("Usando exemplo " + id + " - " + exemplo.getNome() + "\n");
                        programaFonte = exemplo.getProgramaFonte();
                        terminal.informacao(NumeraLinhas.numerar(programaFonte) + "\n");
                    }
                } catch (NumberFormatException excecao) {
                }
            // Leitura do arquivo
            } else if (args.length == 1) {
                try {
                    BufferedReader leitor = new BufferedReader(new FileReader(args[0]));
                    String linha = leitor.readLine();
                    StringBuilder conteudoDoArquivo = new StringBuilder();
                    while (true) {
                        if (linha != null) {
                            conteudoDoArquivo.append(linha);
                        } else {
                            break;
                        }
                        linha = leitor.readLine();
                        if (linha != null) {
                            conteudoDoArquivo.append("\n");
                        }
                    }
                    leitor.close();
                    programaFonte = conteudoDoArquivo.toString();
                } catch (IOException excecao) {
                	terminal.erro("Erro ao ler do arquivo:\n");
                	terminal.erro(excecao.getLocalizedMessage());
                    System.exit(-1);
                }
            }
        }

        // Se o código fonte não foi obtido (porque o compilador foi utilizado
        // de maneira indevida), imprime na tela uma mensagem informando como o
        // compilador deve ser utilizado
        if (programaFonte == null) {
        	terminal.escrever("Uso:\n");
        	terminal.escrever("java -jar portugol.jar /caminho/para/o/codigo/fonte.por");
        	terminal.escrever("OU");
        	terminal.escrever("java -jar portugol.jar --exemplo numero\n");
        	terminal.escrever("Exemplos disponíveis:\n");
            for (int e = 0; e < Exemplo.values().length; e++) {
                Exemplo exemplo = Exemplo.values()[e];
                terminal.escrever((e + 1), " - ", exemplo.getNome());
            }
        } else {
            // Se foi invocado corretamente, o compilador executa a compilação
            Interpretador interpretador = new Interpretador(terminal);
            try {
            	// TODO Permitir impressão do relatório da análise ao invés da execução
                /* terminal.escrever("Relatório da análise:\n");
                terminal.escrever(NumeraLinhas.numerar(interpretador.getLog())); */
                interpretador.analisar(programaFonte);
                interpretador.executar();
            } catch (Exception excecao) {
            	tratarErro(excecao, terminal);
            	terminal.informacao("\nNão foi possível executar o programa.");
            	System.exit(-1);
            }
        }
    }

}