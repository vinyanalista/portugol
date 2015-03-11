package br.com.vinyanalista.portugol.ide;

import java.io.IOException;
import java.util.*;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import br.com.vinyanalista.portugol.base.lexer.*;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.Interpretador;

public class PortugolTokenMaker extends AbstractTokenMaker {
	private static final List<String> PALAVRAS_RESERVADAS;
	
	static {
		PALAVRAS_RESERVADAS = new ArrayList<String>();
		
		// 3.1 Estrutura sequencial em algoritmos
		PALAVRAS_RESERVADAS.add("algoritmo");
		PALAVRAS_RESERVADAS.add("declare");
		PALAVRAS_RESERVADAS.add("fim_algoritmo");
		// 3.1.1 Declaração de variáveis em algoritmos
		PALAVRAS_RESERVADAS.add("numerico");
		PALAVRAS_RESERVADAS.add("literal");
		PALAVRAS_RESERVADAS.add("logico");
		// 3.1.3 Comando de entrada em algoritmos
		PALAVRAS_RESERVADAS.add("leia");
		// 3.1.4 Comando de saída em algoritmos
		PALAVRAS_RESERVADAS.add("escreva");
		// 4.1.1 Estrutura condicional simples
		PALAVRAS_RESERVADAS.add("se");
		PALAVRAS_RESERVADAS.add("entao");
		PALAVRAS_RESERVADAS.add("inicio");
		PALAVRAS_RESERVADAS.add("fim");
		// 4.1.2 Estrutura condicional composta
		PALAVRAS_RESERVADAS.add("senao");
	    // 5.1.1 Estrutura de repetição para número definido de repetições (estrutura para)
	    PALAVRAS_RESERVADAS.add("para");
	    PALAVRAS_RESERVADAS.add("ate");
	    PALAVRAS_RESERVADAS.add("faca");
	    PALAVRAS_RESERVADAS.add("passo");
	    // 5.1.2 Estrutura de repetição para número indefinido de repetições e teste no início (estrutura enquanto)
	    PALAVRAS_RESERVADAS.add("enquanto");
	    // 5.1.3 Estrutura de repetição para número indefinido de repetições e teste no final (estrutura repita)
	    PALAVRAS_RESERVADAS.add("repita");
	    // 8.1 Sub-rotinas (programação modularizada)
	    PALAVRAS_RESERVADAS.add("sub-rotina");
	    PALAVRAS_RESERVADAS.add("retorne");
	    PALAVRAS_RESERVADAS.add("fim_sub_rotina");
	    // 10.2 Declaração de registros em algoritmos
	    PALAVRAS_RESERVADAS.add("registro");
	    // 1.6.2 Lógico
	    PALAVRAS_RESERVADAS.add("verdadeiro");
	    PALAVRAS_RESERVADAS.add("falso");
	}
	
	private Lexer lexer;
	private Token proximoToken;
	private StringBuilder tokenEmFormacao;
	
	private void determinarProximoToken() {
		try {
			proximoToken = lexer.next();
		} catch (LexerException e) {
			proximoToken = null;
		} catch (IOException e) {
			proximoToken = null;
		}
	}
	
	public static List<String> getPalavrasReservadas() {
		return PALAVRAS_RESERVADAS;
	}
	
	@Override
	public TokenMap getWordsToHighlight() {
		TokenMap tokenMap = new TokenMap();
		for (String palavraReservada : PALAVRAS_RESERVADAS) {
			tokenMap.put(palavraReservada, org.fife.ui.rsyntaxtextarea.Token.RESERVED_WORD);
		}
		return tokenMap;
	}

	@Override
	public org.fife.ui.rsyntaxtextarea.Token getTokenList(Segment text, int startTokenType, int startOffset) {
		resetTokenList();
		
		char[] array = text.array;
	    int offset = text.offset;
	    int count = text.count;
	    int end = offset + count;
	    
	    String linha = new String(array, offset, count);
		lexer = new CustomLexer(Interpretador.paraPushbackReader(linha));
		tokenEmFormacao = new StringBuilder("");
		determinarProximoToken();
		
		// Token starting offsets are always of the form:
	    // 'startOffset + (currentTokenStart-offset)', but since startOffset and
	    // offset are constant, tokens' starting positions become:
	    // 'newStartOffset+currentTokenStart'.
		int newStartOffset = startOffset - offset;
		
		int currentTokenStart = offset;
	    // int currentTokenType = startTokenType;
	    
	    for (int i = offset; i < end; i++) {
	    	char c = array[i];
	    	tokenEmFormacao.append(c);
	    	int tipoDoToken;
	    	if (proximoToken != null) {
	    		if (tokenEmFormacao.toString().equals(proximoToken.getText())) {
					String classeDoToken = proximoToken.getClass().getSimpleName();
					// Palavras reservadas
					if (classeDoToken.startsWith("TPalavraReservada")) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.RESERVED_WORD;
					// Operadores lógicos
					} else if (proximoToken instanceof TOperadorE || proximoToken instanceof TOperadorOu || proximoToken instanceof TOperadorNao) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.RESERVED_WORD;
					// Demais operadores
					} else if (classeDoToken.startsWith("TOperador")) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.OPERATOR;
					// Identificadores
					} else if (proximoToken instanceof TIdentificador) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.IDENTIFIER;
					// Valores
					} else if (proximoToken instanceof TNumeroInteiro) {	
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.LITERAL_NUMBER_DECIMAL_INT;
					} else if (proximoToken instanceof TNumeroReal) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.LITERAL_NUMBER_FLOAT;
					} else if (proximoToken instanceof TValorLogico) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.LITERAL_BOOLEAN;
					} else if (proximoToken instanceof TCadeiaDeCaracteres) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.LITERAL_STRING_DOUBLE_QUOTE;
					// Espaços em branco
					} else if (proximoToken instanceof TEspacoEmBranco) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.WHITESPACE;
					// Comentários
					} else if (proximoToken instanceof TComentario) {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.COMMENT_EOL;
					// Separadores de símbolos
					} else {
						tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.SEPARATOR;
					}
					addToken(text, currentTokenStart, i, tipoDoToken, newStartOffset + currentTokenStart);
					currentTokenStart = i + 1;
					tokenEmFormacao = new StringBuilder("");
					determinarProximoToken();
	    		}
	    	} else {
				// Interrompe a formatação ao encontrar um erro léxico,
				// retornando todo o resto da linha como um identificador (sem
				// nenhuma formatação especial)
	    		tipoDoToken = org.fife.ui.rsyntaxtextarea.Token.IDENTIFIER;
	    		addToken(text, currentTokenStart, end - 1, tipoDoToken, newStartOffset + currentTokenStart);
	    		break;
	    	}
	    }

		addNullToken();
		
		return firstToken;
	}

}
