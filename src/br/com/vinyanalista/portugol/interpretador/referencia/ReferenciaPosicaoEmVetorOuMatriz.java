package br.com.vinyanalista.portugol.interpretador.referencia;

import br.com.vinyanalista.portugol.interpretador.execucao.*;
import br.com.vinyanalista.portugol.interpretador.tipo.TipoVetorOuMatriz;

public class ReferenciaPosicaoEmVetorOuMatriz extends Referencia {

	private final int[] posicoes;

	public ReferenciaPosicaoEmVetorOuMatriz(int[] posicoes, Referencia proximaReferencia) {
		super(proximaReferencia);
		this.posicoes = posicoes;
	}

	@Override
	public PosicaoDeMemoria resolver(RegistroDeAtivacao registroDeAtivacao) {
		return null;
	}

	@Override
	protected PosicaoDeMemoria resolver(PosicaoDeMemoria resultadoDaReferenciaAnterior) throws ErroEmTempoDeExecucao {
		VetorOuMatriz vetorOuMatriz = (VetorOuMatriz) resultadoDaReferenciaAnterior.getValor();
		PosicaoDeMemoria celula = vetorOuMatriz.obterCelula(posicoes);
		if (getProximaReferencia() == null) {
			return celula;
		} else {
			return getProximaReferencia().resolver(celula);
		}
	}
	
	@Override
	public String toString() {
		String referenciaComoString = TipoVetorOuMatriz.dimensoesParaString(posicoes);
		if (getProximaReferencia() != null) {
			if (getProximaReferencia() instanceof ReferenciaVariavel) {
				return referenciaComoString + "." + getProximaReferencia().toString();
			} else if (getProximaReferencia() instanceof ReferenciaPosicaoEmVetorOuMatriz) {
				return referenciaComoString + getProximaReferencia().toString();
			}
		}
		return referenciaComoString;
	}

}