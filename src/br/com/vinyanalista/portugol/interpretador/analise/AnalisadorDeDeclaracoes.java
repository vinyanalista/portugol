package br.com.vinyanalista.portugol.interpretador.analise;

import br.com.vinyanalista.portugol.base.analysis.*;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class AnalisadorDeDeclaracoes extends DepthFirstAdapter {

    private final AnalisadorSemantico analisadorSemantico;

    public AnalisadorDeDeclaracoes(AnalisadorSemantico analisadorSemantico) {
        this.analisadorSemantico = analisadorSemantico;
    }
    
    @Override
    public void outADeclaracao(ADeclaracao declaracao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos do tipo
        TabelaDeAtributos atributosDoTipo = analisadorSemantico.obterAtributos(declaracao.getTipo());
        // Determina os atributos da declaração
        Tipo tipo = (Tipo) atributosDoTipo.obter(Atributo.TIPO);
        String string = "[...] " + tipo.toString();
        // Para cada variável declarada
        for (PVariavel variavel : declaracao.getVariavel()) {
            // Obtém os atributos da variável
            TabelaDeAtributos atributosDaVariavel = analisadorSemantico.obterAtributos(variavel);
            String identificador = (String) atributosDaVariavel.obter(Atributo.ID);
            Simbolo simbolo = (Simbolo) atributosDaVariavel.obter(Atributo.SIMBOLO);
            int linha = (Integer) atributosDaVariavel.obter(Atributo.LINHA);
            int coluna = (Integer) atributosDaVariavel.obter(Atributo.COLUNA);
            Tipo tipoDaVariavel;
            // Verifica se é uma variável simples ou um vetor/matriz
            if (variavel instanceof ASimplesVariavel) {
                // Variável
                tipoDaVariavel = tipo;
            } else {
                // Vetor ou matriz
            	AVetorOuMatrizVariavel vetorOuMatriz = (AVetorOuMatrizVariavel) variavel;
                int dimensoes = vetorOuMatriz.getExpressao().size();
                int[] capacidades = new int[dimensoes];
                for (int d = 0; d < dimensoes; d++) {
                	int capacidade = 0;
                	PExpressao expressao = vetorOuMatriz.getExpressao().get(d);
                	if (expressao instanceof AValorExpressao) {
                		AValorExpressao expressaoValor = (AValorExpressao) expressao;
                		if (expressaoValor.getValor() instanceof AInteiroValor) {
                			AInteiroValor valorInteiro = (AInteiroValor) expressaoValor.getValor();
                			analisadorSemantico.analisarExpressao(valorInteiro);
                			TabelaDeAtributos atributosDoValor = analisadorSemantico.obterAtributos(valorInteiro);
                    		capacidade = (Integer) atributosDoValor.obter(Atributo.VALOR);
                		}
                	}
                	if (capacidade == 0) {
						analisadorSemantico.lancarErroSemantico(
										expressao, linha, coluna,
										(dimensoes > 1 ? "A capacidade de uma dimensão de uma matriz"
												: "A capacidade de um vetor")
												+ " deve ser indicada por um número inteiro");
                        return;
                	}
                	capacidades[d] = capacidade;
                }
                tipoDaVariavel = new TipoVetorOuMatriz(tipo, capacidades);
                String stringDaVariavel = identificador + TipoVetorOuMatriz.dimensoesParaString(capacidades);
                atributosDaVariavel.inserir(Atributo.STRING, stringDaVariavel);
            }
            atributosDaVariavel.inserir(Atributo.TIPO, tipoDaVariavel);
			// Se é uma declaração de campos de um registro, não há mais o que
			// verificar
            if (declaracao.parent() instanceof ARegistroTipo) {
            	continue;
            }
        	// Verifica se o identificador já foi utilizado
        	if (analisadorSemantico.getTabelaDeSimbolos().existe(simbolo, false)) {
        		TabelaDeAtributos tabelaDeAtributosOriginal = analisadorSemantico.getTabelaDeSimbolos().obter(simbolo);
        		int linhaOriginal = (Integer) tabelaDeAtributosOriginal.obter(Atributo.LINHA);
                int colunaOriginal = (Integer) tabelaDeAtributosOriginal.obter(Atributo.COLUNA);
                analisadorSemantico.lancarErroSemantico(variavel, linha, coluna, "O identificador " + identificador + " já foi utilizado na linha " + linhaOriginal + ", coluna " + colunaOriginal + ".");
                return;
        	}
            // Adiciona a variável à tabela de símbolos
        	analisadorSemantico.getTabelaDeSimbolos().inserir(simbolo, atributosDaVariavel);
        	analisadorSemantico.logar("Variável " + identificador + " do tipo " + tipoDaVariavel + " declarada na linha " + linha + ", coluna " + coluna + "\n");
        }
        // Armazena os atributos na tabela de atributos
	    TabelaDeAtributos atributosDaDeclaracao = new TabelaDeAtributos();
	    atributosDaDeclaracao.inserir(Atributo.TIPO, tipo);
	    atributosDaDeclaracao.inserir(Atributo.STRING, string);
	    analisadorSemantico.gravarAtributos(declaracao, atributosDaDeclaracao);
    }
    
    @Override
    public void outASimplesVariavel(ASimplesVariavel variavel) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos da variável
        String identificador = variavel.getIdentificador().getText().toUpperCase();
        Simbolo simbolo = Simbolo.obter(identificador);
        int linha = variavel.getIdentificador().getLine();
        int coluna = variavel.getIdentificador().getPos();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaVariavel = new TabelaDeAtributos();
        atributosDaVariavel.inserir(Atributo.ID, identificador);
        atributosDaVariavel.inserir(Atributo.SIMBOLO, simbolo);
        atributosDaVariavel.inserir(Atributo.LINHA, linha);
        atributosDaVariavel.inserir(Atributo.COLUNA, coluna);
        atributosDaVariavel.inserir(Atributo.STRING, identificador);
        analisadorSemantico.gravarAtributos(variavel, atributosDaVariavel);
    }
    
    @Override
    public void outAVetorOuMatrizVariavel(AVetorOuMatrizVariavel vetorOuMatriz) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do vetor ou matriz
        String identificador = vetorOuMatriz.getIdentificador().getText().toUpperCase();
        Simbolo simbolo = Simbolo.obter(identificador);
        int linha = vetorOuMatriz.getIdentificador().getLine();
        int coluna = vetorOuMatriz.getIdentificador().getPos();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoVetorOuMatriz = new TabelaDeAtributos();
        atributosDoVetorOuMatriz.inserir(Atributo.ID, identificador);
        atributosDoVetorOuMatriz.inserir(Atributo.SIMBOLO, simbolo);
        atributosDoVetorOuMatriz.inserir(Atributo.LINHA, linha);
        atributosDoVetorOuMatriz.inserir(Atributo.COLUNA, coluna);
        analisadorSemantico.gravarAtributos(vetorOuMatriz, atributosDoVetorOuMatriz);
    }
    
    @Override
    public void outANumericoTipo(ANumericoTipo tipoNumerico) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do tipo
        Tipo tipo = new Tipo(TipoPrimitivo.NUMERICO);
        String string = tipo.toString();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoTipo = new TabelaDeAtributos();
        atributosDoTipo.inserir(Atributo.TIPO, tipo);
        atributosDoTipo.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(tipoNumerico, atributosDoTipo);
    }
    
    @Override
    public void outALiteralTipo(ALiteralTipo tipoLiteral) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do tipo
        Tipo tipo = new Tipo(TipoPrimitivo.LITERAL);
        String string = tipo.toString();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoTipo = new TabelaDeAtributos();
        atributosDoTipo.inserir(Atributo.TIPO, tipo);
        atributosDoTipo.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(tipoLiteral, atributosDoTipo);
    }
    
    @Override
    public void outALogicoTipo(ALogicoTipo tipoLogico) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do tipo
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        String string = tipo.toString();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoTipo = new TabelaDeAtributos();
        atributosDoTipo.inserir(Atributo.TIPO, tipo);
        atributosDoTipo.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(tipoLogico, atributosDoTipo);
    }
    
    @Override
    public void outARegistroTipo(ARegistroTipo tipoRegistro) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        TipoRegistro tipo = new TipoRegistro();
    	// Verifica e adiciona à tabela de símbolos cada campo do registro
    	for (PDeclaracao declaracao : tipoRegistro.getDeclaracao()) {
    		for (PVariavel campo : ((ADeclaracao) declaracao).getVariavel()) {
    			TabelaDeAtributos tabelaDeAtributosDoCampo = analisadorSemantico.obterAtributos(campo);
    			Simbolo simbolo = (Simbolo) tabelaDeAtributosDoCampo.obter(Atributo.SIMBOLO);
    			// Verifica se o identificador já foi utilizado
            	if (tipo.getCampos().existe(simbolo, false)) {
            		int linha = (Integer) tabelaDeAtributosDoCampo.obter(Atributo.LINHA);
                    int coluna = (Integer) tabelaDeAtributosDoCampo.obter(Atributo.COLUNA);
                    String identificador = (String) tabelaDeAtributosDoCampo.obter(Atributo.ID);
            		TabelaDeAtributos tabelaDeAtributosOriginal = tipo.getCampos().obter(simbolo);
            		int linhaOriginal = (Integer) tabelaDeAtributosOriginal.obter(Atributo.LINHA);
                    int colunaOriginal = (Integer) tabelaDeAtributosOriginal.obter(Atributo.COLUNA);
                    analisadorSemantico.lancarErroSemantico(campo, linha, coluna, "O identificador " + identificador + " já foi utilizado na linha " + linhaOriginal + ", coluna " + colunaOriginal + ".");
                    return;
            	}
    			tipo.getCampos().inserir(simbolo, tabelaDeAtributosDoCampo);
    		}
    	}
    	// Determina os atributos do tipo
        String string = tipo.toString();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoTipo = new TabelaDeAtributos();
        atributosDoTipo.inserir(Atributo.TIPO, tipo);
        atributosDoTipo.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(tipoRegistro, atributosDoTipo);
    }
    
    @Override
    public void caseASubRotina(ASubRotina subrotina) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos da subrotina
        String identificador = subrotina.getIdentificador().getText().toUpperCase();
        Simbolo simbolo = Simbolo.obter(identificador);
        int linha = subrotina.getIdentificador().getLine();
        int coluna = subrotina.getIdentificador().getPos();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaSubrotina = new TabelaDeAtributos();
        atributosDaSubrotina.inserir(Atributo.ID, identificador);
        atributosDaSubrotina.inserir(Atributo.SIMBOLO, simbolo);
        atributosDaSubrotina.inserir(Atributo.LINHA, linha);
        atributosDaSubrotina.inserir(Atributo.COLUNA, coluna);
        analisadorSemantico.gravarAtributos(subrotina, atributosDaSubrotina);
        // Verifica se o identificador já foi utilizado
    	if (analisadorSemantico.getTabelaDeSimbolos().existe(simbolo, false)) {
    		TabelaDeAtributos tabelaDeAtributosOriginal = analisadorSemantico.getTabelaDeSimbolos().obter(simbolo);
    		int linhaOriginal = (Integer) tabelaDeAtributosOriginal.obter(Atributo.LINHA);
            int colunaOriginal = (Integer) tabelaDeAtributosOriginal.obter(Atributo.COLUNA);
            analisadorSemantico.lancarErroSemantico(subrotina, linha, coluna, "O identificador " + identificador + " já foi utilizado na linha " + linhaOriginal + ", coluna " + colunaOriginal + ".");
            return;
    	}
		// Verifica se o identificador repetido após o fim da sub-rotina
		// corresponde ao identificador da declaração
    	String identificadorRepetido = subrotina.getIdentificadorRepetido().getText().toUpperCase();
    	if (!identificadorRepetido.equals(identificador)) {
    		int linhaDoIdentificadorRepetido = subrotina.getIdentificadorRepetido().getLine();
    		int colunaDoIdentificadorRepetido = subrotina.getIdentificadorRepetido().getPos();
			analisadorSemantico
					.lancarErroSemantico(
							subrotina,
							linhaDoIdentificadorRepetido,
							colunaDoIdentificadorRepetido,
							"O identificador informado após a palavra reservada FIM_SUB_ROTINA ("
									+ identificadorRepetido
									+ ") não é o mesmo informado após a palavra reservada SUB-ROTINA ("
									+ identificador + ")");
			return;
    	}
    	// TODO Permitir sobrecarga?
    	// Determina o tipo da subrotina
    	TipoSubrotina tipo = new TipoSubrotina(subrotina, analisadorSemantico.getTabelaDeSimbolos());
    	for (PDeclaracao declaracaoDeParametros : subrotina.getParametros()) {
    		for (PVariavel parametro : ((ADeclaracao) declaracaoDeParametros).getVariavel()) {
    			TabelaDeAtributos atributosDoParametro = analisadorSemantico.obterAtributos(parametro);
        		Simbolo simboloDoParametro = (Simbolo) atributosDoParametro.obter(Atributo.SIMBOLO);
        		tipo.getParametros().add(simboloDoParametro);
        		tipo.getTabelaDeSimbolos().inserir(simboloDoParametro, atributosDoParametro);
    		}
    	}
    	atributosDaSubrotina.inserir(Atributo.TIPO, tipo);
    	String string = tipo.toString().replaceFirst("SUB-ROTINA", identificador);
    	atributosDaSubrotina.inserir(Atributo.STRING, string);
    	analisadorSemantico.getTabelaDeSimbolos().inserir(simbolo, atributosDaSubrotina);
    	analisadorSemantico.logar("Declaração da sub-rotina " + string + " analisada\n");
    }   
}