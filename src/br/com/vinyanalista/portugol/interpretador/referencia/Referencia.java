package br.com.vinyanalista.portugol.interpretador.referencia;

import br.com.vinyanalista.portugol.interpretador.execucao.*;

public abstract class Referencia {
	private Referencia proximaReferencia;
	private Referencia referenciaAnterior;

	public Referencia(Referencia proximaReferencia) {
		this.proximaReferencia = proximaReferencia;
		if (proximaReferencia != null) {
			proximaReferencia.referenciaAnterior = this;
		}
	}

	public Referencia getProximaReferencia() {
		return proximaReferencia;
	}
	
	public Referencia getReferenciaAnterior() {
		return referenciaAnterior;
	}
	
	public abstract PosicaoDeMemoria resolver(RegistroDeAtivacao registroDeAtivacao) throws ErroEmTempoDeExecucao;
	
	protected abstract PosicaoDeMemoria resolver(PosicaoDeMemoria resultadoDaReferenciaAnterior) throws ErroEmTempoDeExecucao;
	
	public void setProximaReferencia(Referencia proximaReferencia) {
		this.proximaReferencia = proximaReferencia;
	}

}