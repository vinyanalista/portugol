package br.com.vinyanalista.portugol.interpretador.subrotina;

import java.util.ArrayList;
import java.util.List;

import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public enum SubrotinaPreDefinida {
	ARREDONDA() {
		@Override
		protected String inicializarDescricaoDoRetorno() {
			return "o arredondamento do número real x";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("X",
					"número real a ser arredondado",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
		
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
	}, COSSENO() {
		@Override
		protected String inicializarDescricaoDoRetorno() {
			return "o cosseno do ângulo x";
		}
    	
    	@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("X",
					"ângulo representado em radianos",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
		
    	@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, LIMPAR_TELA() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "apaga todos os caracteres da tela";
		}

		@Override
		public void inicializarParametros() {
		}
		
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return null;
		}
    }, OBTENHA_ANO() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "o ano da data fornecida";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("DATA",
					"data obtida pela sub-rotina OBTENHA_DATA()",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
		
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, OBTENHA_DATA() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "a diferença, medida em milissegundos, entre a data atual e 01/01/1970";
		}

		@Override
		public void inicializarParametros() {
		}
		
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, OBTENHA_DIA() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "o dia da data fornecida";
		}
    	
    	@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("DATA",
					"data obtida pela sub-rotina OBTENHA_DATA()",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
    	
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, OBTENHA_HORA() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "as horas do horário fornecido";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("HORARIO",
					"horário obtido pela sub-rotina OBTENHA_HORARIO()",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
    	
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, OBTENHA_HORARIO() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "a diferença, medida em milissegundos, entre a data e hora atuais e 01/01/1970 00:00";
		}

		@Override
		public void inicializarParametros() {
		}
    	
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, OBTENHA_MES() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "o mês da data fornecida";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("DATA",
					"data obtida pela sub-rotina OBTENHA_DATA()",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
    	
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, OBTENHA_MINUTO() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "os minutos do horário fornecido";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("HORARIO",
					"horário obtido pela sub-rotina OBTENHA_HORARIO()",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
		
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, OBTENHA_SEGUNDO() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "os segundos do horário fornecido";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("HORARIO",
					"horário obtido pela sub-rotina OBTENHA_HORARIO()",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
		
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, PARTE_INTEIRA() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "a parte inteira do número real x";
		}

        @Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("X",
					"número real do qual se deseja obter a parte inteira",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
        
        @Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, POTENCIA() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "o número a elevado ao número b";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("A", "a base", new Tipo(
					TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
			Parametro parametro2 = new Parametro("B", "o expoente", new Tipo(
					TipoPrimitivo.NUMERICO));
			parametros.add(parametro2);
		}
		
		@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, RAIZ_ENESIMA() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "a raiz n do número x";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("N", "índice da raiz",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
			Parametro parametro2 = new Parametro("X",
					"número do qual se deseja obter a raiz n", new Tipo(
							TipoPrimitivo.NUMERICO));
			parametros.add(parametro2);
		}
    	
    	@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
	}, RAIZ_QUADRADA() {
		@Override
		protected String inicializarDescricaoDoRetorno() {
			return "a raiz quadrada do número x";
		}

        @Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("X",
					"número do qual se deseja obter a raiz quadrada",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
        
        @Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    }, RESTO() {
    	@Override
		protected String inicializarDescricaoDoRetorno() {
    		return "o resto da divisão do número x pelo número y";
		}

		@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("X", "dividendo", new Tipo(
					TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
			Parametro parametro2 = new Parametro("Y", "divisor", new Tipo(
					TipoPrimitivo.NUMERICO));
			parametros.add(parametro2);
		}
    	
    	@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
	}, SENO() {
		@Override
		protected String inicializarDescricaoDoRetorno() {
			return "o seno do ângulo x";
		}

    	@Override
		public void inicializarParametros() {
			Parametro parametro1 = new Parametro("X",
					"ângulo representado em radianos",
					new Tipo(TipoPrimitivo.NUMERICO));
			parametros.add(parametro1);
		}
    	
    	@Override
		protected Tipo inicializarTipoDoRetorno() {
			return new Tipo(TipoPrimitivo.NUMERICO);
		}
    };
    
    protected final String descricaoDoRetorno;
    protected final List<Parametro> parametros;
    protected final Simbolo simbolo;
    protected final TabelaDeAtributos tabelaDeAtributos;
    protected final Tipo tipoDoRetorno;
    
    private SubrotinaPreDefinida() {
    	descricaoDoRetorno = inicializarDescricaoDoRetorno();
    	parametros = new ArrayList<Parametro>();
    	inicializarParametros();
		simbolo = Simbolo.obter(getIdentificador());
		tipoDoRetorno = inicializarTipoDoRetorno();
		
		TipoSubrotinaPredefinida tipo = new TipoSubrotinaPredefinida();
    	for (Parametro parametro : parametros) {
    		Simbolo simboloDoParametro = parametro.getSimbolo();
    		TabelaDeAtributos tabelaDeAtributosDoParametro = new TabelaDeAtributos();
        	tabelaDeAtributosDoParametro.inserir(Atributo.ID, parametro.getIdentificador());
        	tabelaDeAtributosDoParametro.inserir(Atributo.SIMBOLO, parametro.getSimbolo());
        	tabelaDeAtributosDoParametro.inserir(Atributo.TIPO, parametro.getTipo());
        	tipo.getParametros().add(simboloDoParametro);
        	tipo.getTabelaDeSimbolos().inserir(simboloDoParametro, tabelaDeAtributosDoParametro);
    	}
    	
    	String identificador = getIdentificador();
    	
    	tabelaDeAtributos = new TabelaDeAtributos();
    	tabelaDeAtributos.inserir(Atributo.ID, identificador);
    	tabelaDeAtributos.inserir(Atributo.SIMBOLO, simbolo);
    	tabelaDeAtributos.inserir(Atributo.TIPO, tipo);
    	tabelaDeAtributos.inserir(Atributo.STRING, tipo.toString().replace("SUB-ROTINA", identificador));
	}
    
    public String getDescricaoDoRetorno() {
		return descricaoDoRetorno;
	}
    
    public String getIdentificador() {
		return toString();
	}
    
    public List<Parametro> getParametros() {
		return parametros;
	}
    
    public Simbolo getSimbolo() {
		return simbolo;
	}
    
    public TabelaDeAtributos getTabelaDeAtributos() {
    	return tabelaDeAtributos;
    }
    
    public Tipo getTipoDoRetorno() {
    	return tipoDoRetorno;
    }
    
    protected abstract String inicializarDescricaoDoRetorno();
    
    protected abstract void inicializarParametros();
    
    protected abstract Tipo inicializarTipoDoRetorno();
    
    public static SubrotinaPreDefinida obterSubrotina(Simbolo simbolo) {
    	for (SubrotinaPreDefinida subrotina : SubrotinaPreDefinida.values()) {
    		if (subrotina.simbolo.equals(simbolo)) {
    			return subrotina;
    		}
    	}
    	return null;
	}
    
}