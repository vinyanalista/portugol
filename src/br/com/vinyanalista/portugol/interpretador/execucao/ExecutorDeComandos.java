package br.com.vinyanalista.portugol.interpretador.execucao;

import java.util.*;

import br.com.vinyanalista.portugol.base.analysis.DepthFirstAdapter;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.TerminalEncerrado;
import br.com.vinyanalista.portugol.interpretador.referencia.Referencia;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class ExecutorDeComandos extends DepthFirstAdapter {

	private final Executor executor;

	public ExecutorDeComandos(Executor executor) {
		this.executor = executor;
	}
	
	@Override
	public void caseAAtribuicaoComando(AAtribuicaoComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
        if (executor.execucaoEncerrada()) {
            return;
        }
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(comando.getExpressao());
		Object valorDaExpressao = atributosDaExpressao.obter(Atributo.VALOR);
		TabelaDeAtributos atributosDaPosicaoDeMemoria = executor.obterAtributos(comando.getPosicaoDeMemoria());
		Referencia referenciaDaPosicaoDeMemoria = (Referencia) atributosDaPosicaoDeMemoria.obter(Atributo.REFERENCIA);
		PosicaoDeMemoria posicaoDeMemoria;
		try {
			posicaoDeMemoria = executor.obterPosicaoDeMemoria(referenciaDaPosicaoDeMemoria);
		} catch (ErroEmTempoDeExecucao excecao) {
			int linhaDaPosicaoDeMemoria = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.LINHA);
			int colunaDaPosicaoDeMemoria = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.COLUNA);
			executor.lancarErroDeExecucao(comando, linhaDaPosicaoDeMemoria,
					colunaDaPosicaoDeMemoria,
					"Não foi possível acessar a posição de memória "
							+ referenciaDaPosicaoDeMemoria);
			return;
		}
		posicaoDeMemoria.setValor(valorDaExpressao);
	}
	
	@Override
	public void caseAEntradaComando(AEntradaComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
        if (executor.execucaoEncerrada()) {
            return;
        }
        for (PPosicaoDeMemoria noPosicaoDeMemoria : comando.getPosicaoDeMemoria()) {
        	TabelaDeAtributos atributosDaPosicaoDeMemoria = executor.obterAtributos(noPosicaoDeMemoria);
        	int linhaDaPosicaoDeMemoria = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.LINHA);
			int colunaDaPosicaoDeMemoria = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.COLUNA);
        	Tipo tipoDaPosicaoDeMemoria = (Tipo) atributosDaPosicaoDeMemoria.obter(Atributo.TIPO);
        	Referencia referenciaDaPosicaoDeMemoria = (Referencia) atributosDaPosicaoDeMemoria.obter(Atributo.REFERENCIA);
        	PosicaoDeMemoria posicaoDeMemoria;
        	try {
    			posicaoDeMemoria = executor.obterPosicaoDeMemoria(referenciaDaPosicaoDeMemoria);
    		} catch (ErroEmTempoDeExecucao excecao) {
				executor.lancarErroDeExecucao(comando, linhaDaPosicaoDeMemoria,
						colunaDaPosicaoDeMemoria,
						"Não foi possível acessar a posição de memória "
								+ referenciaDaPosicaoDeMemoria);
    			return;
    		}
        	Object valor = null;
        	try {
	        	if (tipoDaPosicaoDeMemoria.ehLiteral()) {
					valor = executor.getTerminal().lerLiteral();
	        	} else if (tipoDaPosicaoDeMemoria.ehLogico()) {
	        		valor = executor.getTerminal().lerLogico();
	        	} else {
	        		valor = executor.getTerminal().lerNumerico();
	        	}
        	} catch (ErroEmTempoDeExecucao excecao) {
				executor
						.lancarErroDeExecucao(comando, linhaDaPosicaoDeMemoria,
								colunaDaPosicaoDeMemoria, excecao
										.getLocalizedMessage());
				return;
			} catch (TerminalEncerrado excecao) {
				executor.encerrar();
				return;
			}
        	posicaoDeMemoria.setValor(valor);
        }
	}
	
	@Override
	public void caseASaidaComando(ASaidaComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		List<PExpressao> expressoes = comando.getExpressao();
		List<Object> valores = new ArrayList<Object>(expressoes.size());
		for (PExpressao expressao : expressoes) {
			TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
			Object valorDaExpressao = atributosDaExpressao.obter(Atributo.VALOR);
			valores.add(valorDaExpressao);
		}
		executor.getTerminal().escrever(valores.toArray());
	}
	
	@Override
	public void caseACondicionalComando(ACondicionalComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(comando.getExpressao());
		Boolean valorDaExpressao = (Boolean) atributosDaExpressao.obter(Atributo.VALOR);
		if (valorDaExpressao) {
			comando.getEntao().apply(executor);
		} else if (comando.getSenao() != null) {
			comando.getSenao().apply(executor);
		}
	}
	
	@Override
	public void caseARepeticaoParaComando(ARepeticaoParaComando comandoDeRepeticao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos do comando, da variável e das expressões
		TabelaDeAtributos atributosDoComando = executor.obterAtributos(comandoDeRepeticao);
		Integer passo = (Integer) atributosDoComando.obter(Atributo.PASSO);
		TabelaDeAtributos atributosDaVariavel = executor.obterAtributos(comandoDeRepeticao.getVariavel());
		Simbolo simboloDaVariavel = (Simbolo) atributosDaVariavel.obter(Atributo.SIMBOLO);
		TabelaDeAtributos atributosDaExpressaoInicio = executor.obterAtributos(comandoDeRepeticao.getInicio());
        TabelaDeAtributos atributosDaExpressaoFim = executor.obterAtributos(comandoDeRepeticao.getFim());
        // Obtém os valores das expressões
		Object valorDaExpressaoInicio = atributosDaExpressaoInicio.obter(Atributo.VALOR);
		Object valorDaExpressaoFim = atributosDaExpressaoFim.obter(Atributo.VALOR);
		int inicio = (Integer) valorDaExpressaoInicio;
		int fim = (Integer) valorDaExpressaoFim;
		// Caso o passo não tenha sido especificado, ocorre incremento
		// A contagem regressiva automática é sugerida nas páginas 93 e 94, mas
		// isso causaria um problema em alguns programas (por exemplo, o
		// exercício resolvido 1 do capítulo 10)
		if (passo == null) {
			passo = 1;
		}
		// Obtém a variável que controla a repetição
		PosicaoDeMemoria posicaoDeMemoria = executor.obterPosicaoDeMemoria(simboloDaVariavel);
        for (int iteracao = inicio; (passo > 0 && iteracao <= fim) || (passo < 0 && iteracao >= fim); iteracao = iteracao + passo) {
        	posicaoDeMemoria.setValor(iteracao);
        	comandoDeRepeticao.getComando().apply(executor);
        	if (executor.execucaoEncerrada()) {
    			return;
    		}
		}
	}
	
	@Override
	public void caseARepeticaoEnquantoComando(ARepeticaoEnquantoComando comandoDeRepeticao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		PExpressao expressaoLogica = comandoDeRepeticao.getExpressao();
		boolean valorDaExpressaoComoLogico = false;
		do {
			executor.avaliarExpressao(expressaoLogica);
			TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressaoLogica);
			Object valorDaExpressao = atributosDaExpressao.obter(Atributo.VALOR);
			valorDaExpressaoComoLogico = (Boolean) valorDaExpressao;
			if (!valorDaExpressaoComoLogico) {
				break;
			}
			comandoDeRepeticao.getComando().apply(executor);
			if (executor.execucaoEncerrada()) {
				return;
			}
		} while (true);
	}
	
	@Override
	public void caseARepeticaoRepitaComando(ARepeticaoRepitaComando comandoDeRepeticao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		PExpressao expressaoLogica = comandoDeRepeticao.getExpressao();
		List<PComando> comandosRepetidos = comandoDeRepeticao.getComando();
		boolean valorDaExpressaoComoLogico = false;
		do {
			for (PComando comando : comandosRepetidos) {
				comando.apply(executor);
				if (executor.execucaoEncerrada()) {
					return;
				}
			}
			executor.avaliarExpressao(expressaoLogica);
			TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressaoLogica);
			Object valorDaExpressao = atributosDaExpressao.obter(Atributo.VALOR);
			valorDaExpressaoComoLogico = (Boolean) valorDaExpressao;
		} while (!valorDaExpressaoComoLogico);
	}
	
	@Override
	public void caseARetorneComando(ARetorneComando comando) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(comando.getExpressao());
		Object valorDaExpressao = atributosDaExpressao.obter(Atributo.VALOR);
		// Armazena o valor a ser retornado
		PilhaDeExecucao pilhaDeExecucao = executor.getPilhaDeExecucao();
		RegistroDeAtivacao registroDeAtivacaoDaSubrotina = pilhaDeExecucao.obterRegistroAtual();
		registroDeAtivacaoDaSubrotina.setValorRetornado(valorDaExpressao);
	}

}