package br.com.vinyanalista.portugol.interpretador.analise;

import java.util.*;

import br.com.vinyanalista.portugol.base.analysis.*;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class AnalisadorDeExpressoes extends DepthFirstAdapter {

    private final AnalisadorSemantico analisadorSemantico;

    public AnalisadorDeExpressoes(AnalisadorSemantico analisadorSemantico) {
        this.analisadorSemantico = analisadorSemantico;
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
        // Verifica se o identificador corresponde na verdade a um campo de um registro
        if (variavel.parent() instanceof ACampoPosicaoDeMemoria) {
        	ACampoPosicaoDeMemoria posicaoDeMemoria = (ACampoPosicaoDeMemoria) variavel.parent();
        	if (posicaoDeMemoria.getCampo().equals(variavel)) {
        		return; // Se sim, por hora não há mais o que verificar
        	}
        }
        // Verifica se o identificador já foi declarado
    	if (!analisadorSemantico.getTabelaDeSimbolos().existe(simbolo)) {
    		analisadorSemantico.lancarErroSemantico(variavel, linha, coluna, "O identificador " + identificador + " não foi declarado.");
            return;
    	}
        TabelaDeAtributos atributosDaDeclaracao = analisadorSemantico.getTabelaDeSimbolos().obter(simbolo);
        // Obtém o tipo da variável
        Tipo tipoDaVariavel = (Tipo) atributosDaDeclaracao.obter(Atributo.TIPO);
        atributosDaVariavel.inserir(Atributo.TIPO, tipoDaVariavel);
        // Observação: um vetor ou matriz pode ser passado como argumento sem posição de memória (ver exercício resolvido 22 do capítulo 8) 
        // TODO Verificar se o identificador corresponde na verdade a um registro
        // TODO Verificar se o identificador corresponde na verdade a uma sub-rotina
    }
    
    @Override
    public void outAVetorOuMatrizVariavel(AVetorOuMatrizVariavel vetorOuMatriz) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do vetor/matriz
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
        // Verifica se o identificador já foi declarado
        if (!analisadorSemantico.getTabelaDeSimbolos().existe(simbolo)) {
        	analisadorSemantico.lancarErroSemantico(vetorOuMatriz, linha, coluna, "O identificador " + identificador + " não foi declarado.");
            return;
    	}
        TabelaDeAtributos atributosDaDeclaracao = analisadorSemantico.getTabelaDeSimbolos().obter(simbolo);
        // Verifica se o identificador corresponde a um vetor/matriz
        Tipo tipo = (Tipo) atributosDaDeclaracao.obter(Atributo.TIPO);
        LinkedList<PExpressao> expressoes = vetorOuMatriz.getExpressao();
        if (!(tipo instanceof TipoVetorOuMatriz)) {
            analisadorSemantico.lancarErroSemantico(vetorOuMatriz, linha, coluna, "O identificador " + identificador + " não corresponde a " + (expressoes.size() > 1 ? "um vetor" : "uma matriz"));
            return;
        }
        TipoVetorOuMatriz tipoDoVetorOuMatriz = (TipoVetorOuMatriz) tipo;
        // Verifica se a quantidade de dimensões corresponde
        int dimensoes = tipoDoVetorOuMatriz.getDimensoes();
        if (dimensoes != expressoes.size()) {
        	analisadorSemantico.lancarErroSemantico(vetorOuMatriz, linha, coluna, "O identificador " + identificador + " não corresponde a " + (expressoes.size() > 1 ? "um vetor" : "uma matriz de " + expressoes.size() + "dimensões"));
            return;
        }
        // Verifica se as expressões que indicam os índices são numéricas
        String[] dimensoesComoString = new String[dimensoes];
        for (int d = 0; d < dimensoes; d++) {
        	PExpressao expressao = expressoes.get(d);
        	TabelaDeAtributos atributosDaExpressao = analisadorSemantico.obterAtributos(expressao);
        	Tipo tipoDaExpressao = (Tipo) atributosDaExpressao.obter(Atributo.TIPO);
        	// TODO Verificar
        	//boolean posicaoValida = false;
        	boolean posicaoValida = tipoDaExpressao.ehNumerico();
        	/* if (expressao instanceof APosicaoDeMemoriaExpressao) {
        		APosicaoDeMemoriaExpressao expressaoPosicaoDeMemoria = (APosicaoDeMemoriaExpressao) expressao;
        		TabelaDeAtributos atributosDaPosicaoDeMemoria = analisadorSemantico.obterAtributos(expressaoPosicaoDeMemoria.getPosicaoDeMemoria());
        		Tipo tipoDaPosicaoDeMemoria = (Tipo) atributosDaPosicaoDeMemoria.obter(Atributo.TIPO);
        		posicaoValida = tipoDaPosicaoDeMemoria.ehNumerico();
        	}
        	if (expressao instanceof AValorExpressao) {
        		AValorExpressao expressaoValor = (AValorExpressao) expressao;
        		posicaoValida = (expressaoValor.getValor() instanceof AInteiroValor);
        	} */
        	if (!posicaoValida) {
        		int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
                int colunaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.COLUNA);
				analisadorSemantico.lancarErroSemantico(
								expressao,
								linhaDaExpressao,
								colunaDaExpressao,
								"A indicação da posição a ser acessada "
										+ (dimensoes > 1 ? "em uma dimensão de uma matriz"
												: "em um vetor")
										+ " deve ser indicada por um valor ou variável do tipo NUMÉRICO");
                return;
        	}
        	String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
        	dimensoesComoString[d] = stringDaExpressao;
        }
        // Determina o tipo da expressão resultante
        Tipo tipoDaExpressao = tipoDoVetorOuMatriz.getTipoDasCelulas();
        atributosDoVetorOuMatriz.inserir(Atributo.TIPO, tipoDaExpressao);
        String string = identificador + TipoVetorOuMatriz.dimensoesParaString(dimensoesComoString);
        atributosDoVetorOuMatriz.inserir(Atributo.STRING, string);
    }
    
    @Override
    public void outAVariavelPosicaoDeMemoria(AVariavelPosicaoDeMemoria posicaoDeMemoria) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        TabelaDeAtributos atributosDaVariavel = analisadorSemantico.obterAtributos(posicaoDeMemoria.getVariavel());
        analisadorSemantico.gravarAtributos(posicaoDeMemoria, atributosDaVariavel);
        String string = (String) atributosDaVariavel.obter(Atributo.STRING);
        Tipo tipo = (Tipo) atributosDaVariavel.obter(Atributo.TIPO);
        analisadorSemantico.logar("A posição de memória " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outACampoPosicaoDeMemoria(ACampoPosicaoDeMemoria posicaoDeMemoria) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos do registro
        TabelaDeAtributos atributosDoRegistro = analisadorSemantico.obterAtributos(posicaoDeMemoria.getRegistro());
        String identificadorDoRegistro = (String) atributosDoRegistro.obter(Atributo.ID);
        Simbolo simboloDoRegistro = (Simbolo) atributosDoRegistro.obter(Atributo.SIMBOLO);
        TabelaDeAtributos atributosDaDeclaracaoDoRegistro = analisadorSemantico.getTabelaDeSimbolos().obter(simboloDoRegistro);
        // Determina os atributos da posição de memória
        int linha = (Integer) atributosDoRegistro.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDoRegistro.obter(Atributo.COLUNA);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaPosicaoDeMemoria = new TabelaDeAtributos();
        atributosDaPosicaoDeMemoria.inserir(Atributo.LINHA, linha);
        atributosDaPosicaoDeMemoria.inserir(Atributo.COLUNA, coluna);
        analisadorSemantico.gravarAtributos(posicaoDeMemoria, atributosDaPosicaoDeMemoria);
        // Verifica se o primeiro identificador corresponde de fato a um registro
        Tipo tipo = (Tipo) atributosDaDeclaracaoDoRegistro.obter(Atributo.TIPO);
		TipoRegistro tipoDoRegistro = null;
		if (tipo instanceof TipoRegistro) {
			tipoDoRegistro = (TipoRegistro) tipo;
		} else if (tipo instanceof TipoVetorOuMatriz) {
			TipoVetorOuMatriz tipoDoVetorOuMatriz = (TipoVetorOuMatriz) tipo;
			Tipo tipoDasCelulas = tipoDoVetorOuMatriz.getTipoDasCelulas();
			if (tipoDasCelulas instanceof TipoRegistro) {
				tipoDoRegistro = (TipoRegistro) tipoDasCelulas;
			}
		}
		if (tipoDoRegistro == null) {
            analisadorSemantico.lancarErroSemantico(posicaoDeMemoria, linha, coluna, "O identificador " + identificadorDoRegistro + " não corresponde a um registro");
            return;
        }
        atributosDoRegistro.inserir(Atributo.TIPO, tipoDoRegistro);
        // Obtém os atributos do campo
        TabelaDeAtributos atributosDoCampo = analisadorSemantico.obterAtributos(posicaoDeMemoria.getCampo());
        String identificadorDoCampo = (String) atributosDoCampo.obter(Atributo.ID);
        Simbolo simboloDoCampo = (Simbolo) atributosDoCampo.obter(Atributo.SIMBOLO);
        // Verifica se o campo pertence ao registro
        if (!tipoDoRegistro.getCampos().existe(simboloDoCampo)) {
        	analisadorSemantico.lancarErroSemantico(posicaoDeMemoria, linha, coluna, "O registro " + identificadorDoRegistro + " não possui um campo " + identificadorDoCampo);
            return;
        }
    	TabelaDeAtributos atributosDaDeclaracaoDoCampo = tipoDoRegistro.getCampos().obter(simboloDoCampo);
        // Determina o tipo da expressão resultante
    	Tipo tipoDoCampo = (Tipo) atributosDaDeclaracaoDoCampo.obter(Atributo.TIPO);
    	atributosDaPosicaoDeMemoria.inserir(Atributo.TIPO, tipoDoCampo);
    	String stringDoRegistro = (String) atributosDoRegistro.obter(Atributo.STRING);
    	String stringDoCampo = (String) atributosDoCampo.obter(Atributo.STRING);
    	String string = Campo.string(stringDoRegistro, stringDoCampo);
    	atributosDaPosicaoDeMemoria.inserir(Atributo.STRING, string);
    	analisadorSemantico.logar("A posição de memória " + string + " é do tipo " + tipoDoCampo + "\n");
    }
    
    @Override
    public void outADisjuncaoExpressao(ADisjuncaoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " OU " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são lógicos
        if (!(tipoDaEsquerda.ehLogico() && tipoDaDireita.ehLogico())) {
            // Se não são lógicos, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível efetuar uma operação lógica entre " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAConjuncaoExpressao(AConjuncaoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
         // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " E " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são lógicos
        if (!(tipoDaEsquerda.ehLogico() && tipoDaDireita.ehLogico())) {
            // Se não são lógicos, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível efetuar uma operação lógica entre " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAIgualdadeExpressao(AIgualdadeExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " = " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são compatíveis
        if (!tipoDaEsquerda.ehCompativel(tipoDaDireita)) {
            // Se não são compatíveis, lança um erro semântico
        	analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outADiferencaExpressao(ADiferencaExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " <> " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são compatíveis
        if (!tipoDaEsquerda.ehCompativel(tipoDaDireita)) {
            // Se não são compatíveis, lança um erro semântico
        	analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAMenorExpressao(AMenorExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
         // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " < " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())){
            // Se não são numéricos, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAMenorOuIgualExpressao(AMenorOuIgualExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
         // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " <= " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())){
            // Se não são numéricos, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAMaiorExpressao(AMaiorExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
         // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " > " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())){
            // Se não são numéricos, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível comparar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAMaiorOuIgualExpressao(AMaiorOuIgualExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String string = "(" + atributosDaEsquerda.obter(Atributo.STRING) + " >= " + atributosDaDireita.obter(Atributo.STRING) + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())){
            // Se não são numéricos, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, coluna, "Não é possível comparar um " + tipoDaEsquerda + " com um " + tipoDaDireita + " na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outASomaExpressao(ASomaExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " + " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.NUMERICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
            // Se não são numéricos, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível somar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") e " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outASubtracaoExpressao(ASubtracaoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " - " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.NUMERICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())) {
            // Se não são numéricos, lança um erro semântico
        	analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível subtrair " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") por " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAMultiplicacaoExpressao(AMultiplicacaoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " * " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.NUMERICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())){
            // Se não são numéricos, lança um erro semântico
        	analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível multiplicar " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") por " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outADivisaoExpressao(ADivisaoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaEsquerda = analisadorSemantico.obterAtributos(expressao.getEsquerda());
        TabelaDeAtributos atributosDaDireita = analisadorSemantico.obterAtributos(expressao.getDireita());
        // Obtém os tipos dos operandos
        Tipo tipoDaEsquerda = (Tipo) atributosDaEsquerda.obter(Atributo.TIPO);
        Tipo tipoDaDireita = (Tipo) atributosDaDireita.obter(Atributo.TIPO);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDaEsquerda.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaEsquerda.obter(Atributo.COLUNA);
        String stringDaEsquerda = (String) atributosDaEsquerda.obter(Atributo.STRING);
        String stringDaDireita = (String) atributosDaDireita.obter(Atributo.STRING);
        String string = "(" + stringDaEsquerda + " / " + stringDaDireita + ")";
        Tipo tipo = new Tipo(TipoPrimitivo.NUMERICO);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se os tipos são numéricos
        if (!(tipoDaEsquerda.ehNumerico() && tipoDaDireita.ehNumerico())){
            // Se não são numéricos, lança um erro semântico
        	analisadorSemantico.lancarErroSemantico(expressao, linha, 0, "Não é possível dividir " + stringDaEsquerda + " (um " + tipoDaEsquerda + ") por " + stringDaDireita + " (um " + tipoDaDireita + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAPositivoExpressao(APositivoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos do operando
        TabelaDeAtributos atributosDoOperando = analisadorSemantico.obterAtributos(expressao.getExpressao());
        String stringDoOperando = (String) atributosDoOperando.obter(Atributo.STRING);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDoOperando.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDoOperando.obter(Atributo.COLUNA);
        Tipo tipo = (Tipo) atributosDoOperando.obter(Atributo.TIPO);
        String string = "+(" + stringDoOperando + ")";
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se o tipo é numérico
        if (!tipo.ehNumerico()) {
            // Se não é numérico, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, coluna, "Não é possível tornar " + stringDoOperando + "(um " + tipo + ") um número positivo na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outANegativoExpressao(ANegativoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos do operando
        TabelaDeAtributos atributosDoOperando = analisadorSemantico.obterAtributos(expressao.getExpressao());
        String stringDoOperando = (String) atributosDoOperando.obter(Atributo.STRING);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDoOperando.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDoOperando.obter(Atributo.COLUNA);
        Tipo tipo = (Tipo) atributosDoOperando.obter(Atributo.TIPO);
        String string = "-(" + stringDoOperando + ")";
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se o tipo é numérico
        if (!tipo.ehNumerico()) {
            // Se não é numérico, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, coluna, "Não é possível tornar " + stringDoOperando + "(um " + tipo + ") um número negativo na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outANegacaoExpressao(ANegacaoExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos do operando
        TabelaDeAtributos atributosDoOperando = analisadorSemantico.obterAtributos(expressao.getExpressao());
        String stringDoOperando = (String) atributosDoOperando.obter(Atributo.STRING);
        // Determina os atributos da expressão resultante
        int linha = (Integer) atributosDoOperando.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDoOperando.obter(Atributo.COLUNA);
        Tipo tipo = (Tipo) atributosDoOperando.obter(Atributo.TIPO);
        String string = "NAO (" + stringDoOperando + ")";
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaExpressao = new TabelaDeAtributos();
        atributosDaExpressao.inserir(Atributo.LINHA, linha);
        atributosDaExpressao.inserir(Atributo.COLUNA, coluna);
        atributosDaExpressao.inserir(Atributo.TIPO, tipo);
        atributosDaExpressao.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(expressao, atributosDaExpressao);
        // Verifica se o tipo é lógico
        if (!tipo.ehLogico()) {
            // Se não é lógico, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(expressao, linha, coluna, "Não é possível negar " + stringDoOperando + "(um " + tipo + ") na expressão " + string);
            return;
        }
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAPosicaoDeMemoriaExpressao(APosicaoDeMemoriaExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
		// Como a posição de memória é a expressão resultante, os atributos da
		// posição de memória são também os atributos da expressão resultante
        TabelaDeAtributos atributosDaPosicaoDeMemoria = analisadorSemantico.obterAtributos(expressao.getPosicaoDeMemoria());
        String string = (String) atributosDaPosicaoDeMemoria.obter(Atributo.STRING);
        Tipo tipo = (Tipo) atributosDaPosicaoDeMemoria.obter(Atributo.TIPO);
        analisadorSemantico.gravarAtributos(expressao, atributosDaPosicaoDeMemoria);
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAChamadaASubRotinaExpressao(AChamadaASubRotinaExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
		// Como a chamada à sub-rotina é a expressão resultante, os atributos da
		// chamada são também os atributos da expressão resultante
        TabelaDeAtributos atributosDaChamada = analisadorSemantico.obterAtributos(expressao.getChamadaASubRotina());
        String string = (String) atributosDaChamada.obter(Atributo.STRING);
        Tipo tipo = (Tipo) atributosDaChamada.obter(Atributo.TIPO);
        analisadorSemantico.gravarAtributos(expressao, atributosDaChamada);
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAValorExpressao(AValorExpressao expressao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
		// Como o valor é a expressão resultante, os atributos do valor são
		// também os atributos da expressão resultante
        TabelaDeAtributos atributosDoValor = analisadorSemantico.obterAtributos(expressao.getValor());
        String string = (String) atributosDoValor.obter(Atributo.STRING);
        Tipo tipo = (Tipo) atributosDoValor.obter(Atributo.TIPO);
        analisadorSemantico.gravarAtributos(expressao, atributosDoValor);
        analisadorSemantico.logar("A expressão " + string + " é do tipo " + tipo + "\n");
    }
    
    @Override
    public void outAChamadaASubRotina(AChamadaASubRotina chamada) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
    	// Determina os atributos da chamada
        String identificador = chamada.getIdentificador().getText().toUpperCase();
        Simbolo simbolo = Simbolo.obter(identificador);
        int linha = chamada.getIdentificador().getLine();
        int coluna = chamada.getIdentificador().getPos();
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDaChamada = new TabelaDeAtributos();
        atributosDaChamada.inserir(Atributo.ID, identificador);
        atributosDaChamada.inserir(Atributo.SIMBOLO, simbolo);
        atributosDaChamada.inserir(Atributo.LINHA, linha);
        atributosDaChamada.inserir(Atributo.COLUNA, coluna);
        analisadorSemantico.gravarAtributos(chamada, atributosDaChamada);
        // Verifica se o identificador já foi declarado
        if (!analisadorSemantico.getTabelaDeSimbolos().existe(simbolo)) {
        	analisadorSemantico.lancarErroSemantico(chamada, linha, coluna, "O identificador " + identificador + " não foi declarado.");
            return;
    	}
        TabelaDeAtributos atributosDaDeclaracao = analisadorSemantico.getTabelaDeSimbolos().obter(simbolo);
        String stringDaDeclaracao = (String) atributosDaDeclaracao.obter(Atributo.STRING);
        // Verifica se o identificador corresponde a uma sub-rotina
        Tipo tipoDaDeclaracao = (Tipo) atributosDaDeclaracao.obter(Atributo.TIPO);
        if (!(tipoDaDeclaracao instanceof TipoSubrotina)) {
            analisadorSemantico.lancarErroSemantico(chamada, linha, coluna, "O identificador " + identificador + " não corresponde a uma sub-rotina");
            return;
        }
        TipoSubrotina tipoDaSubrotina = (TipoSubrotina) tipoDaDeclaracao;
        // Verifica se a quantidade de argumentos corresponde
        List<PExpressao> argumentos = chamada.getExpressao();
        int quantidadeEsperadaDeArgumentos = tipoDaSubrotina.getParametros().size();
        if (argumentos.size() != quantidadeEsperadaDeArgumentos) {
        	analisadorSemantico.lancarErroSemantico(chamada, linha, coluna, "A quantidade de argumentos fornecidos não corresponde à quantidade de parâmetros esperados pela sub-rotina " + stringDaDeclaracao);
            return;
        }
        // Verifica se os tipos dos argumentos correspondem aos tipos dos parâmetros
        String[] argumentosComoString = new String[quantidadeEsperadaDeArgumentos];
        for (int a = 0; a < quantidadeEsperadaDeArgumentos; a++) {
        	PExpressao argumento = argumentos.get(a);
        	TabelaDeAtributos atributosDoArgumento = analisadorSemantico.obterAtributos(argumento);
        	Tipo tipoDoArgumento = (Tipo) atributosDoArgumento.obter(Atributo.TIPO);
        	Simbolo simboloDoParametro = tipoDaSubrotina.getParametros().get(a);
        	TabelaDeAtributos atributosDoParametro = tipoDaSubrotina.getTabelaDeSimbolos().obter(simboloDoParametro);
        	Tipo tipoDoParametro = (Tipo) atributosDoParametro.obter(Atributo.TIPO);
        	if (!tipoDoArgumento.ehCompativel(tipoDoParametro)) {
        		analisadorSemantico.lancarErroSemantico(chamada, linha, coluna, "Os tipos dos argumentos fornecidos não correspondem aos tipos dos parâmetros esperados pela sub-rotina " + stringDaDeclaracao);
                return;
        	}
        	argumentosComoString[a] = (String) atributosDoArgumento.obter(Atributo.STRING);
        }
        Tipo tipo = new Tipo(TipoPrimitivo.DETERMINADO_EM_TEMPO_DE_EXECUCAO);
        atributosDaChamada.inserir(Atributo.TIPO, tipo);
        String string = identificador + TipoSubrotina.parametrosParaString(argumentosComoString);
        atributosDaChamada.inserir(Atributo.STRING, string);
        analisadorSemantico.logar("Chamada à sub-rotina " + stringDaDeclaracao + " analisada\n");
    }
    
    @Override
    public void outAInteiroValor(AInteiroValor valor) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do valor
        int linha = valor.getNumeroInteiro().getLine();
        int coluna = valor.getNumeroInteiro().getPos();
        Tipo tipo = new Tipo(TipoPrimitivo.NUMERICO);
        String string = valor.getNumeroInteiro().getText();
        int valorNumerico = 0;
        try {
        	valorNumerico = Integer.parseInt(string);
        } catch (NumberFormatException excecao) {
        	analisadorSemantico.lancarErroSemantico(valor, linha, coluna, "Valor numérico inválido");
        }
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoValor = new TabelaDeAtributos();
        atributosDoValor.inserir(Atributo.LINHA, linha);
        atributosDoValor.inserir(Atributo.COLUNA, coluna);
        atributosDoValor.inserir(Atributo.TIPO, tipo);
        atributosDoValor.inserir(Atributo.VALOR, valorNumerico);
        atributosDoValor.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(valor, atributosDoValor);
    }
    
    @Override
    public void outARealValor(ARealValor valor) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do valor
        int linha = valor.getNumeroReal().getLine();
        int coluna = valor.getNumeroReal().getPos();
        Tipo tipo = new Tipo(TipoPrimitivo.NUMERICO);
        String string = valor.getNumeroReal().getText();
        double valorNumerico = 0;
        try {
        	valorNumerico = Double.parseDouble(valor.getNumeroReal().getText());
        } catch (NumberFormatException excecao) {
        	analisadorSemantico.lancarErroSemantico(valor, linha, coluna, "Valor numérico inválido");
        }
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoValor = new TabelaDeAtributos();
        atributosDoValor.inserir(Atributo.LINHA, linha);
        atributosDoValor.inserir(Atributo.COLUNA, coluna);
        atributosDoValor.inserir(Atributo.TIPO, tipo);
        atributosDoValor.inserir(Atributo.VALOR, valorNumerico);
        atributosDoValor.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(valor, atributosDoValor);
    }
    
    @Override
    public void outALogicoValor(ALogicoValor valor) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do valor
        int linha = valor.getValorLogico().getLine();
        int coluna = valor.getValorLogico().getPos();
        Tipo tipo = new Tipo(TipoPrimitivo.LOGICO);
        String string = valor.getValorLogico().getText().toUpperCase();
        boolean valorLogico = string.equals("VERDADEIRO");
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoValor = new TabelaDeAtributos();
        atributosDoValor.inserir(Atributo.LINHA, linha);
        atributosDoValor.inserir(Atributo.COLUNA, coluna);
        atributosDoValor.inserir(Atributo.TIPO, tipo);
        atributosDoValor.inserir(Atributo.VALOR, valorLogico);
        atributosDoValor.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(valor, atributosDoValor);
    }
    
    @Override
    public void outALiteralValor(ALiteralValor valor) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Determina os atributos do valor
        int linha = valor.getCadeiaDeCaracteres().getLine();
        int coluna = valor.getCadeiaDeCaracteres().getPos();
        String string = valor.getCadeiaDeCaracteres().getText();
        Tipo tipo = new Tipo(TipoPrimitivo.LITERAL);
        String valorDaString = string.replaceAll("\"", "");
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoValor = new TabelaDeAtributos();
        atributosDoValor.inserir(Atributo.LINHA, linha);
        atributosDoValor.inserir(Atributo.COLUNA, coluna);
        atributosDoValor.inserir(Atributo.TIPO, tipo);
        atributosDoValor.inserir(Atributo.VALOR, valorDaString);
        atributosDoValor.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(valor, atributosDoValor);
    }
    
}
