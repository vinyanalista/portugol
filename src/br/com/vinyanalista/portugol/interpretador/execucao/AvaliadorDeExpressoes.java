package br.com.vinyanalista.portugol.interpretador.execucao;

import java.util.List;

import br.com.vinyanalista.portugol.base.analysis.DepthFirstAdapter;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.referencia.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.subrotina.SubrotinaPreDefinida;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class AvaliadorDeExpressoes extends DepthFirstAdapter {
	public static boolean ehNumero(Object objeto) {
		return Number.class.isAssignableFrom(objeto.getClass());
	}

	private final Executor executor;

	public AvaliadorDeExpressoes(Executor executor) {
		this.executor = executor;
	}
	
	public boolean chamadaASubrotinaNaoRetornouValor(PExpressao expressao) {
		if (!ehChamadaASubrotina(expressao)) {
			return false;
		}
		AChamadaASubRotinaExpressao chamada = (AChamadaASubRotinaExpressao) expressao;
		TabelaDeAtributos atributosDaChamada = executor.obterAtributos(chamada);
		Object valorRetornado = atributosDaChamada.obter(Atributo.VALOR);
		boolean naoRetornouValor = (valorRetornado == null);
		if (naoRetornouValor) {
			int linhaDaChamada = (Integer) atributosDaChamada
					.obter(Atributo.LINHA);
			int colunaDaChamada = (Integer) atributosDaChamada
					.obter(Atributo.COLUNA);
			executor
					.lancarErroDeExecucao(
							chamada,
							linhaDaChamada,
							colunaDaChamada,
							"A chamada à sub-rotina não retornou um valor que pudesse ser utilizado n	a expressão");
		}
		return naoRetornouValor;
	}

	public boolean chamadaASubrotinaNaoRetornouValor(
			PExpressao expressaoDaEsquerda, PExpressao expressaoDaDireita) {
		return chamadaASubrotinaNaoRetornouValor(expressaoDaEsquerda)
				|| chamadaASubrotinaNaoRetornouValor(expressaoDaDireita);
	}
	
	public boolean ehChamadaASubrotina(PExpressao expressao) {
		return (expressao instanceof AChamadaASubRotinaExpressao);
	}
	
	public boolean haChamadaASubrotina(PExpressao expressaoDaEsquerda,
			PExpressao expressaoDaDireita) {
		return ehChamadaASubrotina(expressaoDaEsquerda)
				|| ehChamadaASubrotina(expressaoDaDireita);
	}
	
	@Override
	public void outASimplesVariavel(ASimplesVariavel variavel) {
		// A execução é suspensa quando ocorre um erro de execução
        if (executor.execucaoEncerrada()) {
            return;
        }
        TabelaDeAtributos atributosDaVariavel = executor.obterAtributos(variavel);
        Simbolo simboloDaVariavel = (Simbolo) atributosDaVariavel.obter(Atributo.SIMBOLO);
        Referencia referencia = new ReferenciaVariavel(simboloDaVariavel, null);
        atributosDaVariavel.inserir(Atributo.REFERENCIA, referencia);
	}
	
	@Override
	public void outAVetorOuMatrizVariavel(AVetorOuMatrizVariavel vetorOuMatriz) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		TabelaDeAtributos atributosDoVetorOuMatriz = executor.obterAtributos(vetorOuMatriz);
        Simbolo simboloDoVetorOuMatriz = (Simbolo) atributosDoVetorOuMatriz.obter(Atributo.SIMBOLO);
        int posicoes[] = executor.paraVetorDePosicoes(vetorOuMatriz.getExpressao());
        Referencia referenciaDaPosicao = new ReferenciaPosicaoEmVetorOuMatriz(posicoes, null);
        Referencia referenciaDoVetorOuMatriz = new ReferenciaVariavel(simboloDoVetorOuMatriz, referenciaDaPosicao);
        atributosDoVetorOuMatriz.inserir(Atributo.REFERENCIA, referenciaDoVetorOuMatriz);
	}
	
	@Override
	public void outACampoPosicaoDeMemoria(ACampoPosicaoDeMemoria posicaoDeMemoria) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		TabelaDeAtributos atributosDoRegistro = executor.obterAtributos(posicaoDeMemoria.getRegistro());
		TabelaDeAtributos atributosDoCampo = executor.obterAtributos(posicaoDeMemoria.getCampo());
		TabelaDeAtributos atributosDaPosicaoDeMemoria = executor.obterAtributos(posicaoDeMemoria);
		Referencia referenciaDoRegistro = (Referencia) atributosDoRegistro.obter(Atributo.REFERENCIA);
		Referencia referenciaDoCampo = (Referencia) atributosDoCampo.obter(Atributo.REFERENCIA);
		Referencia ultimaReferenciaDoRegistro = referenciaDoRegistro;
		while (ultimaReferenciaDoRegistro.getProximaReferencia() != null) {
			ultimaReferenciaDoRegistro = ultimaReferenciaDoRegistro.getProximaReferencia();
		}
		ultimaReferenciaDoRegistro.setProximaReferencia(referenciaDoCampo);
		atributosDaPosicaoDeMemoria.inserir(Atributo.REFERENCIA, referenciaDoRegistro);
	}
	
	@Override
	public void outADisjuncaoExpressao(ADisjuncaoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são lógicos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehLogico() && tipoDaDireita.ehLogico())) {
	        	// Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível efetuar uma operação lógica entre " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        boolean valorDaEsquerdaComoLogico = (Boolean) valorDaEsquerda;
    	boolean valorDaDireitaComoLogico = (Boolean) valorDaDireita;
    	boolean valorDaExpressaoComoLogico = (valorDaEsquerdaComoLogico || valorDaDireitaComoLogico);
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressaoComoLogico);
	}
	
	@Override
	public void outAConjuncaoExpressao(AConjuncaoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são lógicos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehLogico() && tipoDaDireita.ehLogico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível efetuar uma operação lógica entre " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        boolean valorDaEsquerdaComoLogico = (Boolean) valorDaEsquerda;
    	boolean valorDaDireitaComoLogico = (Boolean) valorDaDireita;
    	boolean valorDaExpressaoComoLogico = (valorDaEsquerdaComoLogico && valorDaDireitaComoLogico);
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressaoComoLogico);
	}
	
	@Override
	public void outAIgualdadeExpressao(AIgualdadeExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são compatíveis
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehCompativel(tipoDaDireita))) {
	            // Se não são compatíveis, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Boolean valorDaExpressao;
        if (ehNumero(valorDaEsquerda) && ehNumero(valorDaDireita)) {
			// Se ambos os operandos são números, o operador aritmético é preferido
        	if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
    			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
    			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
            	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
            	valorDaExpressao = (valorDaEsquerdaComoInteiro == valorDaDireitaComoInteiro);
    		} else {
    			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
            	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
            	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
            	valorDaExpressao = (valorDaEsquerdaComoReal == valorDaDireitaComoReal);
    		}
		} else {
			// Se um dos operandos não é número, o método equals() é preferido
			valorDaExpressao = valorDaEsquerda.equals(valorDaDireita);
		}
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outADiferencaExpressao(ADiferencaExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são compatíveis
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehCompativel(tipoDaDireita))) {
	            // Se não são compatíveis, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Boolean valorDaExpressao;
        if (ehNumero(valorDaEsquerda) && ehNumero(valorDaDireita)) {
			// Se ambos os operandos são números, o operador aritmético é preferido
        	if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
    			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
    			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
            	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
            	valorDaExpressao = (valorDaEsquerdaComoInteiro != valorDaDireitaComoInteiro);
    		} else {
    			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
            	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
            	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
            	valorDaExpressao = (valorDaEsquerdaComoReal != valorDaDireitaComoReal);
    		}
		} else {
			// Se um dos operandos não é número, o método equals() é preferido
			valorDaExpressao = !valorDaEsquerda.equals(valorDaDireita);
		}
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outAMenorExpressao(AMenorExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Boolean valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	valorDaExpressao = (valorDaEsquerdaComoInteiro < valorDaDireitaComoInteiro);
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = valorDaEsquerdaComoReal < valorDaDireitaComoReal;
		}
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outAMenorOuIgualExpressao(AMenorOuIgualExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Boolean valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	valorDaExpressao = (valorDaEsquerdaComoInteiro <= valorDaDireitaComoInteiro);
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = valorDaEsquerdaComoReal <= valorDaDireitaComoReal;
		}
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outAMaiorExpressao(AMaiorExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Boolean valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	valorDaExpressao = (valorDaEsquerdaComoInteiro > valorDaDireitaComoInteiro);
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = valorDaEsquerdaComoReal > valorDaDireitaComoReal;
		}
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outAMaiorOuIgualExpressao(AMaiorOuIgualExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Boolean valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	valorDaExpressao = (valorDaEsquerdaComoInteiro >= valorDaDireitaComoInteiro);
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = (valorDaEsquerdaComoReal >= valorDaDireitaComoReal);
		}
        // Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outASomaExpressao(ASomaExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível somar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Number valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	valorDaExpressao = valorDaEsquerdaComoInteiro + valorDaDireitaComoInteiro;
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = valorDaEsquerdaComoReal + valorDaDireitaComoReal;
		}
		// Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outASubtracaoExpressao(ASubtracaoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível subtrair " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") por " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Number valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	valorDaExpressao = valorDaEsquerdaComoInteiro - valorDaDireitaComoInteiro;
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = valorDaEsquerdaComoReal - valorDaDireitaComoReal;
		}
		// Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outAMultiplicacaoExpressao(AMultiplicacaoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível multiplicar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") por " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Number valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	valorDaExpressao = valorDaEsquerdaComoInteiro * valorDaDireitaComoInteiro;
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = valorDaEsquerdaComoReal * valorDaDireitaComoReal;
		}
		// Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outADivisaoExpressao(ADivisaoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seus operandos
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDaEsquerda = executor.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = executor.obterAtributos(expressao.getDireita());
        // Verifica se algum dos operandos é uma chamada a uma sub-rotina
		if (haChamadaASubrotina(expressao.getEsquerda(), expressao.getDireita())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getEsquerda(), expressao.getDireita())) {
				return;
			}
			// Obtém os tipos dos operandos
	        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
	        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
	        // Verifica se os tipos são numéricos
	        if (!(tipoDaEsquerda != null && tipoDaDireita != null && tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
	            // Se não são lógicos, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
	            String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível dividir " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") por " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
        // Obtém os valores dos operandos
        Object valorDaEsquerda = atributosDaEsquerda.obter(Atributo.VALOR);
        Object valorDaDireita = atributosDaDireita.obter(Atributo.VALOR);
        // Determina o valor da expressão
        Number valorDaExpressao;
		if ((valorDaEsquerda instanceof Integer) && (valorDaDireita instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDaEsquerdaComoInteiro = (Integer) valorDaEsquerda;
        	int valorDaDireitaComoInteiro = (Integer) valorDaDireita;
        	if (valorDaEsquerdaComoInteiro % valorDaDireitaComoInteiro == 0) {
        		// Se o resultado da divisão é um número inteiro, é armazenado como tal
        		valorDaExpressao = valorDaEsquerdaComoInteiro / valorDaDireitaComoInteiro;
    		} else {
    			// Se o resultado da divisão é um número real, é armazenado como tal
    			valorDaExpressao = valorDaEsquerdaComoInteiro / (double) valorDaDireitaComoInteiro;
    		}
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDaEsquerdaComoReal = Executor.converterParaReal((Number) valorDaEsquerda);
        	double valorDaDireitaComoReal = Executor.converterParaReal((Number) valorDaDireita);
        	valorDaExpressao = valorDaEsquerdaComoReal / (double) valorDaDireitaComoReal;
		}
		// Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outAPositivoExpressao(APositivoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seu operando
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDoOperando = executor.obterAtributos(expressao.getExpressao());
		// Verifica se o operando é uma chamada a uma sub-rotina
		if (ehChamadaASubrotina(expressao.getExpressao())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getExpressao())) {
				return;
			}
			// Obtém o tipo do operando
	        Tipo tipoDoOperando = (Tipo) atributosDoOperando.obter(Atributo.TIPO);
	        // Verifica se o tipo é numérico
	        if (!(tipoDoOperando != null && tipoDoOperando.ehNumerico())) {
	            // Se não é numérico, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDoOperando = (String) atributosDoOperando.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível tornar " + stringDoOperando + "(um " + tipoDoOperando + ") um número positivo na expressão " + stringDaExpressao);
	            return;
	        }
		}
		// Obtém o valor do operando
		Object valorDoOperando = atributosDoOperando.obter(Atributo.VALOR);
		// Determina o valor da expressão
		Number valorDaExpressao = (Number) valorDoOperando;
		// Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outANegativoExpressao(ANegativoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seu operando
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDoOperando = executor.obterAtributos(expressao.getExpressao());
		// Verifica se o operando é uma chamada a uma sub-rotina
		if (ehChamadaASubrotina(expressao.getExpressao())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getExpressao())) {
				return;
			}
			// Obtém o tipo do operando
	        Tipo tipoDoOperando = (Tipo) atributosDoOperando.obter(Atributo.TIPO);
	        // Verifica se o tipo é numérico
	        if (!(tipoDoOperando != null && tipoDoOperando.ehNumerico())) {
	            // Se não é numérico, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDoOperando = (String) atributosDoOperando.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível tornar " + stringDoOperando + "(um " + tipoDoOperando + ") um número negativo na expressão " + stringDaExpressao);
	            return;
	        }
		}
		// Obtém o valor do operando
		Object valorDoOperando = atributosDoOperando.obter(Atributo.VALOR);
		// Determina o valor da expressão
		Number valorDaExpressao;
		if (valorDoOperando instanceof Integer) {
			// Se o operando é inteiro, é realizada aritmética de inteiros
			int valorDoOperandoComoInteiro = (Integer) valorDoOperando;
			valorDaExpressao = valorDoOperandoComoInteiro * (-1);
		} else {
			// Se o operando não é inteiro, é realizada aritmética de ponto flutuante
			double valorDoOperandoComoReal = (Double) valorDoOperando;
			valorDaExpressao = valorDoOperandoComoReal * (-1);
		}
		// Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
	public void outANegacaoExpressao(ANegacaoExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da expressão e de seu operando
		TabelaDeAtributos atributosDaExpressao = executor.obterAtributos(expressao);
		TabelaDeAtributos atributosDoOperando = executor.obterAtributos(expressao.getExpressao());
		// Verifica se o operando é uma chamada a uma sub-rotina
		if (ehChamadaASubrotina(expressao.getExpressao())) {
			// Verifica se houve uma chamada a uma sub-rotina sem retorno de valor
			if (chamadaASubrotinaNaoRetornouValor(expressao.getExpressao())) {
				return;
			}
			// Obtém o tipo do operando
	        Tipo tipoDoOperando = (Tipo) atributosDoOperando.obter(Atributo.TIPO);
	        // Verifica se o tipo é lógico
	        if (!(tipoDoOperando != null && tipoDoOperando.ehLogico())) {
	            // Se não é lógico, lança um erro em tempo de execução
	        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
	        	String stringDoOperando = (String) atributosDoOperando.obter(Atributo.STRING);
	            String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
	        	executor.lancarErroDeExecucao(expressao, linhaDaExpressao, 0, "Não é possível negar " + stringDoOperando + "(um " + tipoDoOperando + ") na expressão " + stringDaExpressao);
	            return;
	        }
		}
		// Obtém o valor do operando
		Object valorDoOperando = atributosDoOperando.obter(Atributo.VALOR);
		// Determina o valor da expressão
		boolean valorDoOperandoComoLogico = (Boolean) valorDoOperando; 
		boolean valorDaExpressao = !valorDoOperandoComoLogico;
		// Armazena o valor da expressão
		atributosDaExpressao.inserir(Atributo.VALOR, valorDaExpressao);
	}
	
	@Override
    public void outAPosicaoDeMemoriaExpressao(APosicaoDeMemoriaExpressao expressao) {
		// A execução é suspensa quando ocorre um erro de execução
        if (executor.execucaoEncerrada()) {
            return;
        }
		TabelaDeAtributos atributosDaPosicaoDeMemoria = executor.obterAtributos(expressao);
    	Referencia referenciaDaPosicaoDeMemoria = (Referencia) atributosDaPosicaoDeMemoria.obter(Atributo.REFERENCIA);
    	PosicaoDeMemoria posicaoDeMemoria;
    	try {
			posicaoDeMemoria = executor.obterPosicaoDeMemoria(referenciaDaPosicaoDeMemoria);
		} catch (ErroEmTempoDeExecucao excecao) {
			int linhaDaPosicaoDeMemoria = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.LINHA);
			int colunaDaPosicaoDeMemoria = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.COLUNA);
			executor.lancarErroDeExecucao(expressao, linhaDaPosicaoDeMemoria,
					colunaDaPosicaoDeMemoria,
					"Não foi possível acessar a posição de memória "
							+ referenciaDaPosicaoDeMemoria);
			return;
		}
    	Object valorDaPosicaoDeMemoria = posicaoDeMemoria.getValor();
    	atributosDaPosicaoDeMemoria.inserir(Atributo.VALOR, valorDaPosicaoDeMemoria);
	}
	
	@Override
	public void outAChamadaASubRotina(AChamadaASubRotina chamada) {
		// A execução é suspensa quando ocorre um erro de execução
		if (executor.execucaoEncerrada()) {
			return;
		}
		// Obtém os atributos da sub-rotina
		TabelaDeAtributos atributosDaChamada = executor.obterAtributos(chamada);
		Simbolo simboloDaSubrotina = (Simbolo) atributosDaChamada.obter(Atributo.SIMBOLO);
		TabelaDeAtributos atributosDaSubrotina = executor.obterTabelaDeSimbolos().obter(simboloDaSubrotina);
		TipoSubrotina tipoDaSubrotina = (TipoSubrotina) atributosDaSubrotina.obter(Atributo.TIPO);
		// Mapeia os argumentos nos parâmetros da sub-rotina
		PilhaDeExecucao pilhaDeExecucao = executor.getPilhaDeExecucao();
		RegistroDeAtivacao registroDeAtivacaoDaSubrotina = new RegistroDeAtivacao(
				simboloDaSubrotina, tipoDaSubrotina.getTabelaDeSimbolos(),
				pilhaDeExecucao.obterRegistroAtual());
		List<PExpressao> argumentos = chamada.getExpressao();
		if (argumentos.size() > 0) {
			List<Simbolo> parametros = tipoDaSubrotina.getParametros();
			for (int a = 0; a < argumentos.size(); a++) {
				PExpressao argumento = argumentos.get(a);
				if (chamadaASubrotinaNaoRetornouValor(argumento)) {
					return;
				}
				TabelaDeAtributos atributosDoArgumento = executor.obterAtributos(argumento);
				Referencia referenciaDoArgumento = (Referencia) atributosDoArgumento.obter(Atributo.REFERENCIA);
				PosicaoDeMemoria posicaoDeMemoria;
				if (referenciaDoArgumento == null) {
					// Trata-se de um valor
					posicaoDeMemoria = new PosicaoDeMemoria();
					Object valorDoArgumento = atributosDoArgumento.obter(Atributo.VALOR);
					posicaoDeMemoria.setValor(valorDoArgumento);
				} else {
					// Trata-se de uma posição de memória
					try {
						posicaoDeMemoria = executor
								.obterPosicaoDeMemoria(referenciaDoArgumento);
					} catch (ErroEmTempoDeExecucao excecao) {
						int linhaDoArgumento = (Integer) atributosDoArgumento
								.obter(Atributo.LINHA);
						int colunaDoArgumento = (Integer) atributosDoArgumento
								.obter(Atributo.COLUNA);
						executor.lancarErroDeExecucao(argumento,
								linhaDoArgumento, colunaDoArgumento,
								"Não foi possível acessar a posição de memória "
										+ referenciaDoArgumento);
						return;
					}
				}
				Simbolo parametro = parametros.get(a);
				registroDeAtivacaoDaSubrotina.atribuirPosicaoDeMemoria(parametro, posicaoDeMemoria);
			}
		}
		// Executa a sub-rotina
		pilhaDeExecucao.inserir(registroDeAtivacaoDaSubrotina);
		if (tipoDaSubrotina instanceof TipoSubrotinaPredefinida) {
			SubrotinaPreDefinida subrotinaPreDefinida = SubrotinaPreDefinida.obterSubrotina(simboloDaSubrotina);
			ExecutorDeSubrotinaPreDefinida.executar(subrotinaPreDefinida, executor);
		} else {
			tipoDaSubrotina.getImplementacao().apply(executor);
		}
		pilhaDeExecucao.remover();
		// Se houve retorno, armazena o valor retornado e seu tipo
		Object valorRetornado = registroDeAtivacaoDaSubrotina.getValorRetornado();
		atributosDaChamada.inserir(Atributo.VALOR, valorRetornado);
		Tipo tipoDoRetorno = registroDeAtivacaoDaSubrotina.getTipoDoRetorno();
		atributosDaChamada.inserir(Atributo.TIPO, tipoDoRetorno);
	}

}