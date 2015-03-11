package br.com.vinyanalista.portugol.interpretador.execucao;

import java.util.*;

import br.com.vinyanalista.portugol.base.analysis.DepthFirstAdapter;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.Terminal;
import br.com.vinyanalista.portugol.interpretador.referencia.Referencia;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;

public class Executor extends DepthFirstAdapter implements Runnable {
	public static Integer converterParaInteiro(Number numero) {
		return numero.intValue(); 
	}
	
	public static Double converterParaReal(Number numero) {
		return numero.doubleValue(); 
	}
	
	public static boolean ehInteiro(Double numero) {
		// http://stackoverflow.com/questions/9898512/how-to-test-if-a-double-is-an-integer
		return ((numero == Math.floor(numero)) && !Double.isInfinite(numero));
	}
	
	private final Start arvoreSintaticaAbstrata;
	private final AvaliadorDeExpressoes avaliadorDeExpressoes;
	private int contadorDeComandos;
	private ErroEmTempoDeExecucao erroEmTempoDeExecucao;
	private final List<EscutaDeExecutor> escutasDeExecutor;
	private boolean execucaoEncerrada;
	private final ExecutorDeComandos executorDeComandos;
	private PilhaDeExecucao pilhaDeExecucao;
	private HashMap<Node, TabelaDeAtributos> tabelasDeAtributos;
	private Terminal terminal;

	public Executor(Start arvoreSintaticaAbstrata,
			TabelaDeSimbolos tabelaDeSimbolos,
			HashMap<Node, TabelaDeAtributos> tabelasDeAtributos,
			Terminal terminal) {
		this.arvoreSintaticaAbstrata = arvoreSintaticaAbstrata;
		avaliadorDeExpressoes = new AvaliadorDeExpressoes(this);
		contadorDeComandos = 0;
		escutasDeExecutor = new ArrayList<EscutaDeExecutor>();
		executorDeComandos = new ExecutorDeComandos(this);
		erroEmTempoDeExecucao = null;
		execucaoEncerrada = false;
		pilhaDeExecucao = new PilhaDeExecucao();
		RegistroDeAtivacao registroDeAtivacao = new RegistroDeAtivacao(null, tabelaDeSimbolos, null);
		pilhaDeExecucao.inserir(registroDeAtivacao);
		this.tabelasDeAtributos = tabelasDeAtributos;
		this.terminal = terminal;
	}
	
	public void adicionarEscuta(EscutaDeExecutor escuta) {
		escutasDeExecutor.add(escuta);
	}
	
	public void adicionarEscutas(List<EscutaDeExecutor> escutas) {
		escutasDeExecutor.addAll(escutas);
	}
	
	void avaliarExpressao(Node expressao) {
		expressao.apply(avaliadorDeExpressoes);
	}
	
	public synchronized void encerrar() {
		execucaoEncerrada = true;
	}
	
	public synchronized boolean execucaoEncerrada() {
		return execucaoEncerrada;
	}

	void executarComando(Node comando) {
		if (comando instanceof ABlocoComando) {
    		comando.apply(this);
    	} else {
    		comando.apply(executorDeComandos);
    	}
	}
	
	public PilhaDeExecucao getPilhaDeExecucao() {
		return pilhaDeExecucao;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	private void incrementarContadorDeComandos() {
		contadorDeComandos++;
	}
	
	void lancarErroDeExecucao(Node no, int linha, int coluna, String mensagem) {
    	if (erroEmTempoDeExecucao == null) {
    		erroEmTempoDeExecucao = new ErroEmTempoDeExecucao(no, "[" + linha + "," + coluna + "] " + mensagem, linha, coluna);
    	}
    	execucaoEncerrada = true;
    }

	public TabelaDeAtributos obterAtributos(Node no) {
		return tabelasDeAtributos.get(no);
	}
	
	public PosicaoDeMemoria obterPosicaoDeMemoria(Referencia referencia) throws ErroEmTempoDeExecucao {
		return referencia.resolver(pilhaDeExecucao.obterRegistroAtual());
	}
	
	public PosicaoDeMemoria obterPosicaoDeMemoria(Simbolo simbolo) {
		return pilhaDeExecucao.obterRegistroAtual().obterPosicaoDeMemoria(simbolo);
	}
	
	public TabelaDeSimbolos obterTabelaDeSimbolos() {
		return pilhaDeExecucao.obterRegistroAtual().getTabelaDeSimbolos();
	}
	
	public int[] paraVetorDePosicoes(List<PExpressao> expressoes) {
		int[] posicoes = new int[expressoes.size()];
		for (int p = 0; p < posicoes.length; p++) {
			PExpressao expressao = expressoes.get(p);
			avaliarExpressao(expressao);
			TabelaDeAtributos atributosDaExpressao = obterAtributos(expressao);
			Object valorDaExpressao = atributosDaExpressao.obter(Atributo.VALOR);
			int valorDaExpressaoComoInteiro = (Integer) valorDaExpressao;
			posicoes[p] = valorDaExpressaoComoInteiro;
		}
		return posicoes;
	}
	
	@Override
    public void caseAAlgoritmo(AAlgoritmo algoritmo) {
		for (PComando comando : algoritmo.getComando()) {
    		comando.apply(this);
    	}
	}
	
	@Override
	public void caseAAtribuicaoComando(AAtribuicaoComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
        if (execucaoEncerrada()) {
            return;
        }
        avaliarExpressao(comando.getPosicaoDeMemoria());
        avaliarExpressao(comando.getExpressao());
        executarComando(comando);
        incrementarContadorDeComandos();
	}
	
	@Override
	public void caseAEntradaComando(AEntradaComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		for (PPosicaoDeMemoria posicaoDeMemoria : comando.getPosicaoDeMemoria()) {
			avaliarExpressao(posicaoDeMemoria);
		}
		executarComando(comando);
		incrementarContadorDeComandos();
	}
	
	@Override
	public void caseASaidaComando(ASaidaComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		for (PExpressao expressao : comando.getExpressao()) {
            avaliarExpressao(expressao);
        }
		executarComando(comando);
		incrementarContadorDeComandos();
	}
	
	@Override
    public void caseABlocoComando(ABlocoComando bloco) {
    	// A execução é suspensa quando ocorre um erro de execução
        if (execucaoEncerrada()) {
            return;
        }
        for (PComando comando : bloco.getComando()) {
        	comando.apply(this);
        }
    }
	
	@Override
    public void caseACondicionalComando(ACondicionalComando comandoCondicional) {
    	// A execução é suspensa quando ocorre um erro de execução
        if (execucaoEncerrada()) {
            return;
        }
        avaliarExpressao(comandoCondicional.getExpressao());
        executarComando(comandoCondicional);
        incrementarContadorDeComandos();
    }
	
	@Override
	public void caseARepeticaoParaComando(ARepeticaoParaComando comandoDeRepeticao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		avaliarExpressao(comandoDeRepeticao.getInicio());
		avaliarExpressao(comandoDeRepeticao.getFim());
		executarComando(comandoDeRepeticao);
		incrementarContadorDeComandos();
	}
	
	@Override
	public void caseARepeticaoEnquantoComando(ARepeticaoEnquantoComando comandoDeRepeticao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		executarComando(comandoDeRepeticao);
		incrementarContadorDeComandos();
	}
	
	@Override
	public void caseARepeticaoRepitaComando(ARepeticaoRepitaComando comandoDeRepeticao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		executarComando(comandoDeRepeticao);
		incrementarContadorDeComandos();
	}
	
	@Override
	public void caseAChamadaASubRotinaComando(AChamadaASubRotinaComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		avaliarExpressao(comando.getChamadaASubRotina());
		incrementarContadorDeComandos();
	}
	
	@Override
	public void caseARetorneComando(ARetorneComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		avaliarExpressao(comando.getExpressao());
		executarComando(comando);
		incrementarContadorDeComandos();
	}
	
	@Override
	public void caseASubRotina(ASubRotina subrotina) {
		// A execução é suspensa quando ocorre um erro de execução
		if (execucaoEncerrada()) {
			return;
		}
		RegistroDeAtivacao registroDeAtivacaoDaSubrotina = pilhaDeExecucao.obterRegistroAtual();
		// Executa cada comando da sub-rotina
		for (PComando comando : subrotina.getComando()) {
    		comando.apply(this);
    		// Após a execução de cada comando, verifica se há um valor a retornar
    		if (registroDeAtivacaoDaSubrotina.getValorRetornado() != null) {
    			// Se há um valor a retornar, interrompe a execução da sub-rotina
    			break;
    		}
    	}
	}

	@Override
	public void run() {
		long horaDeInicio = System.currentTimeMillis();
		arvoreSintaticaAbstrata.apply(this);
		float tempo = (System.currentTimeMillis() - horaDeInicio) / 1000f;
		if (!execucaoEncerrada) {
			execucaoEncerrada = true;
			if (erroEmTempoDeExecucao == null) {
				terminal.informacao("\n" + contadorDeComandos + " comandos executados com sucesso em " + tempo + " segundos");
				terminal.encerrar();
			}
		}
		for (EscutaDeExecutor escuta : escutasDeExecutor) {
			escuta.aoEncerrarExecucao(erroEmTempoDeExecucao);
		}
	}

}