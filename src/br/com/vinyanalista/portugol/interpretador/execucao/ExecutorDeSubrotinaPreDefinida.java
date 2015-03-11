package br.com.vinyanalista.portugol.interpretador.execucao;

import java.util.Calendar;

import br.com.vinyanalista.portugol.interpretador.simbolo.Simbolo;
import br.com.vinyanalista.portugol.interpretador.subrotina.SubrotinaPreDefinida;

public class ExecutorDeSubrotinaPreDefinida {
	private static void executarArredonda(Executor executor) {
		SubrotinaPreDefinida subrotinaArredonda = SubrotinaPreDefinida.ARREDONDA;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaArredonda.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Calcula o arredondamento de X
		double x = ((Number) valorDeX).doubleValue();
		int arredondamento = (int) Math.round(x);
		// Retorna o resultado
		Number valorRetornado = arredondamento;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaArredonda.getTipoDoRetorno());
	}
	
	private static void executarCosseno(Executor executor) {
		SubrotinaPreDefinida subrotinaCosseno = SubrotinaPreDefinida.COSSENO;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaCosseno.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Calcula o cosseno de X
		double x = ((Number) valorDeX).doubleValue();
		double cosseno = Math.cos(x);
		// Verifica se é possível armazenar o resultado como um inteiro
		Object valorRetornado;
		if (Executor.ehInteiro(cosseno)) {
			valorRetornado = Executor.converterParaInteiro(cosseno);
		} else {
			valorRetornado = cosseno;
		}
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaCosseno.getTipoDoRetorno());
	}
	
	private static void executarLimparTela(Executor executor) {
		// Limpa a tela
		executor.getTerminal().limpar();
	}
	
	private static void executarObtenhaAno(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaAno = SubrotinaPreDefinida.OBTENHA_ANO;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaObtenhaAno.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Extrai o ano de X
		long valorDeXComoInteiro = (Long) valorDeX;
		Calendar data = Calendar.getInstance();
		data.setTimeInMillis(valorDeXComoInteiro);
		int ano = data.get(Calendar.YEAR);
		// Retorna o resultado
		Number valorRetornado = ano;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaAno.getTipoDoRetorno());
	}
	
	private static void executarObtenhaData(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaData = SubrotinaPreDefinida.OBTENHA_DATA;
		// Obtém a data do sistema
		Number valorRetornado = System.currentTimeMillis();
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaData.getTipoDoRetorno());
	}
	
	private static void executarObtenhaDia(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaDia = SubrotinaPreDefinida.OBTENHA_DIA;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaObtenhaDia.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Extrai o dia do mês de X
		long valorDeXComoInteiro = (Long) valorDeX;
		Calendar data = Calendar.getInstance();
		data.setTimeInMillis(valorDeXComoInteiro);
		int diaDoMes = data.get(Calendar.DAY_OF_MONTH);
		// Retorna o resultado
		Number valorRetornado = diaDoMes;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaDia.getTipoDoRetorno());
	}
	
	private static void executarObtenhaHora(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaHora = SubrotinaPreDefinida.OBTENHA_HORA;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaObtenhaHora.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Extrai a hora de X
		long valorDeXComoInteiro = (Long) valorDeX;
		Calendar data = Calendar.getInstance();
		data.setTimeInMillis(valorDeXComoInteiro);
		int hora = data.get(Calendar.HOUR_OF_DAY);
		// Retorna o resultado
		Number valorRetornado = hora;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaHora.getTipoDoRetorno());
	}
	
	private static void executarObtenhaHorario(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaHorario = SubrotinaPreDefinida.OBTENHA_HORARIO;
		// Obtém a data do sistema
		Number valorRetornado = System.currentTimeMillis();
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaHorario.getTipoDoRetorno());
	}
	
	private static void executarObtenhaMes(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaMes = SubrotinaPreDefinida.OBTENHA_MES;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaObtenhaMes.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Extrai o dia do mês de X
		long valorDeXComoInteiro = (Long) valorDeX;
		Calendar data = Calendar.getInstance();
		data.setTimeInMillis(valorDeXComoInteiro);
		int mes = data.get(Calendar.MONTH) + 1;
		// Retorna o resultado
		Number valorRetornado = mes;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaMes.getTipoDoRetorno());
	}
	
	private static void executarObtenhaMinuto(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaMinuto = SubrotinaPreDefinida.OBTENHA_MINUTO;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaObtenhaMinuto.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Extrai o minuto de X
		long valorDeXComoInteiro = (Long) valorDeX;
		Calendar data = Calendar.getInstance();
		data.setTimeInMillis(valorDeXComoInteiro);
		int minuto = data.get(Calendar.MINUTE);
		// Retorna o resultado
		Number valorRetornado = minuto;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaMinuto.getTipoDoRetorno());
	}
	
	private static void executarObtenhaSegundo(Executor executor) {
		SubrotinaPreDefinida subrotinaObtenhaSegundo = SubrotinaPreDefinida.OBTENHA_SEGUNDO;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaObtenhaSegundo.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Extrai o segundo de X
		long valorDeXComoInteiro = (Long) valorDeX;
		Calendar data = Calendar.getInstance();
		data.setTimeInMillis(valorDeXComoInteiro);
		int segundo = data.get(Calendar.SECOND);
		// Retorna o resultado
		Number valorRetornado = segundo;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaObtenhaSegundo.getTipoDoRetorno());
	}
	
	private static void executarParteInteira(Executor executor) {
		SubrotinaPreDefinida subrotinaParteInteira = SubrotinaPreDefinida.PARTE_INTEIRA;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaParteInteira.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Calcula o arredondamento de X
		double x = ((Number) valorDeX).doubleValue();
		int parteInteira = (int) Math.floor(x);
		// Retorna o resultado
		Number valorRetornado = parteInteira;
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaParteInteira.getTipoDoRetorno());
	}
	
	private static void executarPotencia(Executor executor) {
		SubrotinaPreDefinida subrotinaPotencia = SubrotinaPreDefinida.POTENCIA;
		// Obtém os parâmetros
		Simbolo simboloDoParametroA = subrotinaPotencia.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroA = executor.obterPosicaoDeMemoria(simboloDoParametroA);
		Simbolo simboloDoParametroB = subrotinaPotencia.getParametros().get(1).getSimbolo();
		PosicaoDeMemoria parametroB = executor.obterPosicaoDeMemoria(simboloDoParametroB);
		// Obtém os valores dos parâmetros
		Object valorDeA = parametroA.getValor();
		Object valorDeB = parametroB.getValor();
		// Calcula A elevado a B
		double a = ((Number) valorDeA).doubleValue();
		double b = ((Number) valorDeB).doubleValue();
		double potencia = Math.pow(a, b);
		// Verifica se é possível armazenar o resultado como um inteiro
		Number valorRetornado;
		if (Executor.ehInteiro(potencia)) {
			valorRetornado = Executor.converterParaInteiro(potencia);
		} else {
			valorRetornado = potencia;
		}
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaPotencia.getTipoDoRetorno());
	}
	
	private static void executarRaizEnesima(Executor executor) {
		SubrotinaPreDefinida subrotinaRaizEnesima = SubrotinaPreDefinida.RAIZ_ENESIMA;
		// Obtém os parâmetros
		Simbolo simboloDoParametroX = subrotinaRaizEnesima.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		Simbolo simboloDoParametroY = subrotinaRaizEnesima.getParametros().get(1).getSimbolo();
		PosicaoDeMemoria parametroY = executor.obterPosicaoDeMemoria(simboloDoParametroY);
		// Obtém os valores dos parâmetros
		Object valorDeX = parametroX.getValor();
		Object valorDeY = parametroY.getValor();
		// Calcula a raiz X de Y
		double x = ((Number) valorDeX).doubleValue();
		double y = ((Number) valorDeY).doubleValue();
		double raizEnesima = Math.pow(y, (1 / (double) x));
		// Verifica se é possível armazenar o resultado como um inteiro
		Number valorRetornado;
		if (Executor.ehInteiro(raizEnesima)) {
			valorRetornado = Executor.converterParaInteiro(raizEnesima);
		} else {
			valorRetornado = raizEnesima;
		}
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaRaizEnesima.getTipoDoRetorno());
	}
	
	private static void executarRaizQuadrada(Executor executor) {
		SubrotinaPreDefinida subrotinaRaizQuadrada = SubrotinaPreDefinida.RAIZ_QUADRADA;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaRaizQuadrada.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Calcula a raiz quadrada de X
		double x = ((Number) valorDeX).doubleValue();
		double raizQuadrada = Math.pow(x, 0.5);
		// Verifica se é possível armazenar o resultado como um inteiro
		Number valorRetornado;
		if (Executor.ehInteiro(raizQuadrada)) {
			valorRetornado = Executor.converterParaInteiro(raizQuadrada);
		} else {
			valorRetornado = raizQuadrada;
		}
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaRaizQuadrada.getTipoDoRetorno());
	}
	
	private static void executarResto(Executor executor) {
		SubrotinaPreDefinida subrotinaResto = SubrotinaPreDefinida.RESTO;
		// Obtém os parâmetros
		Simbolo simboloDoParametroX = subrotinaResto.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		Simbolo simboloDoParametroY = subrotinaResto.getParametros().get(1).getSimbolo();
		PosicaoDeMemoria parametroY = executor.obterPosicaoDeMemoria(simboloDoParametroY);
		// Obtém os valores dos parâmetros
		Object valorDeX = parametroX.getValor();
		Object valorDeY = parametroY.getValor();
		// Calcula o resto da divisão de X por Y
        Number valorRetornado;
		if ((valorDeX instanceof Integer) && (valorDeY instanceof Integer)) {
			// Se ambos os operandos são inteiros, é realizada aritmética de inteiros
			int valorDeXComoInteiro = (Integer) valorDeX;
        	int valorDeYComoInteiro = (Integer) valorDeY;
        	valorRetornado = valorDeXComoInteiro % valorDeYComoInteiro;
		} else {
			// Se um dos operandos não é inteiro, é realizada aritmética de ponto flutuante
        	double valorDeXComoReal = Executor.converterParaReal((Number) valorDeX);
        	double valorDeYComoReal = Executor.converterParaReal((Number) valorDeY);
        	valorRetornado = valorDeXComoReal % valorDeYComoReal;
        	// Verifica se é possível armazenar o resultado como um inteiro
        	if (Executor.ehInteiro(valorRetornado.doubleValue())) {
    			valorRetornado = Executor.converterParaInteiro(valorRetornado);
    		}
		}
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaResto.getTipoDoRetorno());
	}
	
	private static void executarSeno(Executor executor) {
		SubrotinaPreDefinida subrotinaSeno = SubrotinaPreDefinida.SENO;
		// Obtém o parâmetro
		Simbolo simboloDoParametroX = subrotinaSeno.getParametros().get(0).getSimbolo();
		PosicaoDeMemoria parametroX = executor.obterPosicaoDeMemoria(simboloDoParametroX);
		// Obtém o valor do parâmetro
		Object valorDeX = parametroX.getValor();
		// Calcula o seno de X
		double x = ((Number) valorDeX).doubleValue();
		double seno = Math.sin(x);
		// Verifica se é possível armazenar o resultado como um inteiro
		Object valorRetornado;
		if (Executor.ehInteiro(seno)) {
			valorRetornado = Executor.converterParaInteiro(seno);
		} else {
			valorRetornado = seno;
		}
		// Retorna o resultado
		executor.getPilhaDeExecucao().obterRegistroAtual().setValorRetornado(valorRetornado);
		executor.getPilhaDeExecucao().obterRegistroAtual().setTipoDoRetorno(subrotinaSeno.getTipoDoRetorno());
	}
	
	public static void executar(SubrotinaPreDefinida subrotinaPreDefinida,
			Executor executor) {
		switch (subrotinaPreDefinida) {
		case ARREDONDA:
			executarArredonda(executor);
			break;
		case COSSENO:
			executarCosseno(executor);
			break;
		case LIMPAR_TELA:
			executarLimparTela(executor);
			break;
		case OBTENHA_ANO:
			executarObtenhaAno(executor);
			break;
		case OBTENHA_DATA:
			executarObtenhaData(executor);
			break;
		case OBTENHA_DIA:
			executarObtenhaDia(executor);
			break;
		case OBTENHA_HORA:
			executarObtenhaHora(executor);
			break;
		case OBTENHA_HORARIO:
			executarObtenhaHorario(executor);
			break;
		case OBTENHA_MES:
			executarObtenhaMes(executor);
			break;
		case OBTENHA_MINUTO:
			executarObtenhaMinuto(executor);
			break;
		case OBTENHA_SEGUNDO:
			executarObtenhaSegundo(executor);
			break;
		case PARTE_INTEIRA:
			executarParteInteira(executor);
			break;
		case POTENCIA:
			executarPotencia(executor);
			break;
		case RAIZ_ENESIMA:
			executarRaizEnesima(executor);
			break;
		case RAIZ_QUADRADA:
			executarRaizQuadrada(executor);
			break;
		case RESTO:
			executarResto(executor);
			break;
		case SENO:
			executarSeno(executor);
			break;
		}
	}

}