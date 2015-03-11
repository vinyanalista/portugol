package br.com.vinyanalista.portugol.interpretador.execucao;

import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class VetorOuMatriz {
	private final Object[] celulas;

	public VetorOuMatriz(TipoVetorOuMatriz tipo) {
		this(tipo.getTipoDasCelulas(), tipo.getCapacidades());
	}
	
	private VetorOuMatriz(Tipo tipoDasCelulas, int[] capacidades) {
		int capacidadeDessaDimensao = extrairInteiroDessaDimensao(capacidades);
		celulas = new Object[capacidadeDessaDimensao];
		if (haOutrasDimensoes(capacidades)) {
			int[] capacidadesDasOutrasDimensoes = extrairInteirosDasOutrasDimensoes(capacidades);
			for (int c = 0; c < celulas.length; c++) {
				celulas[c] = new VetorOuMatriz(tipoDasCelulas, capacidadesDasOutrasDimensoes);
			}
		} else {
			for (int c = 0; c < celulas.length; c++) {
				PosicaoDeMemoria celula = new PosicaoDeMemoria();
				celula.setValor(tipoDasCelulas.getValorPadrao());
				celulas[c] = celula;
			}
		}
	}
	
	public boolean ehVetor() {
		return (celulas[0] instanceof PosicaoDeMemoria);
	}

	private int[] extrairInteirosDasOutrasDimensoes(int[] inteiros) {
		if (!haOutrasDimensoes(inteiros)) {
			return null;
		}
		int[] inteirosDasOutrasDimensoes = new int[inteiros.length - 1];
		System.arraycopy(inteiros, 1, inteirosDasOutrasDimensoes, 0,
				inteiros.length - 1);
		return inteirosDasOutrasDimensoes;
	}

	private int extrairInteiroDessaDimensao(int[] inteiros) {
		return inteiros[0];
	}

	private boolean haOutrasDimensoes(int[] inteiros) {
		return (inteiros.length > 1);
	}

	public PosicaoDeMemoria obterCelula(int[] posicoes) throws ErroEmTempoDeExecucao {
		int posicaoNessaDimensao = extrairInteiroDessaDimensao(posicoes);
		if (posicaoNessaDimensao <= celulas.length) {
			Object celulaNessaDimensao = celulas[posicaoNessaDimensao - 1];
			boolean saoEsperadasOutrasDimensoes = haOutrasDimensoes(posicoes);
			boolean haOutrasDimensoes = (celulaNessaDimensao instanceof VetorOuMatriz);
			if (haOutrasDimensoes && saoEsperadasOutrasDimensoes) {
				VetorOuMatriz vetorOuMatriz = (VetorOuMatriz) celulaNessaDimensao;
				int[] posicoesNasOutrasDimensoes = extrairInteirosDasOutrasDimensoes(posicoes);
				return vetorOuMatriz.obterCelula(posicoesNasOutrasDimensoes);
			}
			if (!haOutrasDimensoes && !saoEsperadasOutrasDimensoes) {
				return (PosicaoDeMemoria) celulaNessaDimensao;
			}
		}
		throw new ErroEmTempoDeExecucao("A posição indicada não existe no vetor ou matriz");
	}

}