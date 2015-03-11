package br.com.vinyanalista.portugol.interpretador.tipo;

import br.com.vinyanalista.portugol.interpretador.execucao.VetorOuMatriz;

public class TipoVetorOuMatriz extends Tipo {
	public static String dimensoesParaString(String[] dimensoes) {
		StringBuilder string = new StringBuilder("[");
		for (int d = 0; d < dimensoes.length; d++) {
			string.append(dimensoes[d].trim().toUpperCase());
			if (d < dimensoes.length - 1) {
				string.append(", ");
			}
		}
		string.append("]");
		return string.toString();
	}
	
	public static String dimensoesParaString(int[] dimensoes) {
		int quantidadeDeDimensoes = dimensoes.length;
		String[] dimensoesComoString = new String[quantidadeDeDimensoes];
		for (int d = 0; d < quantidadeDeDimensoes; d++) {
			dimensoesComoString[d] = String.valueOf(dimensoes[d]);
		}
		return dimensoesParaString(dimensoesComoString);
	}
	
	private final int[] capacidades;
	private final Tipo tipoDasCelulas;

	public TipoVetorOuMatriz(Tipo tipo, int[] capacidades) {
        super(tipo.getTipoPrimitivo());
		this.capacidades = capacidades;
		tipoDasCelulas = tipo;
    }
	
	public TipoVetorOuMatriz(TipoPrimitivo tipoPrimitivo, int[] capacidades) {
		this(new Tipo(tipoPrimitivo), capacidades);
    }
	
	public boolean ehVetor() {
		return (capacidades.length == 1);
	}
    
    public int getCapacidade(int dimensao) {
    	return capacidades[dimensao];
    }
    
    public int[] getCapacidades() {
		return capacidades;
	}
    
    public int getDimensoes() {
		return capacidades.length;
	}
    
    public Tipo getTipoDasCelulas() {
		return tipoDasCelulas;
	}

    @Override
    public boolean ehCompativel(Tipo outroTipo) {
    	if (outroTipo instanceof TipoVetorOuMatriz) {
    		return ehCompativel((TipoVetorOuMatriz) outroTipo);
    	} else {
    		return false;
    	}
    }

    public boolean ehCompativel(TipoVetorOuMatriz outroTipoVetorOuMatriz) {
        return this.equals(outroTipoVetorOuMatriz);
    }
    
    @Override
    public boolean ehLogico() {
    	return false; // Um vetor ou matriz não é um valor lógico
    }

    @Override
    public boolean ehNumerico() {
        return false; // Um vetor ou matriz não é um número
    }
    
    @Override
    public boolean equals(Object objeto) {
        if (!(objeto instanceof TipoVetorOuMatriz)) {
            return false;
        }
        TipoVetorOuMatriz outroTipo = (TipoVetorOuMatriz) objeto;
        return tipoDasCelulas.equals(outroTipo.tipoDasCelulas) && capacidades.equals(capacidades);
    }
    
    @Override
	public Object getValorPadrao() {
		return new VetorOuMatriz(this);
	}
    
    @Override
    public boolean podeReceberEntradaDoUsuario() {
    	return false;
    }

    @Override
    public String toString() {
        return getTipoDasCelulas().toString() + dimensoesParaString(capacidades);
    }
    
}