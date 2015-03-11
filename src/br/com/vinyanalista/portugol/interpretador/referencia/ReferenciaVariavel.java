package br.com.vinyanalista.portugol.interpretador.referencia;

import br.com.vinyanalista.portugol.interpretador.execucao.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.Simbolo;

public class ReferenciaVariavel extends Referencia {
	private final Simbolo simbolo;

	public ReferenciaVariavel(Simbolo simbolo, Referencia proximaReferencia) {
		super(proximaReferencia);
		this.simbolo = simbolo;
	}

	@Override
	public PosicaoDeMemoria resolver(RegistroDeAtivacao registroDeAtivacao) throws ErroEmTempoDeExecucao {
		PosicaoDeMemoria posicaoDeMemoria = registroDeAtivacao.obterPosicaoDeMemoria(simbolo);
		if (getProximaReferencia() == null) {
			return posicaoDeMemoria;
		} else {
			return getProximaReferencia().resolver(posicaoDeMemoria);
		}
	}

	@Override
	protected PosicaoDeMemoria resolver(PosicaoDeMemoria resultadoDaReferenciaAnterior) {
		if (resultadoDaReferenciaAnterior.getValor() instanceof Registro) {
			Registro registro = (Registro) resultadoDaReferenciaAnterior.getValor();
			return registro.getCampo(simbolo);
		}
		return null;
	}
	
	@Override
	public String toString() {
		String referenciaComoString = simbolo.toString();
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