package br.com.vinyanalista.portugol.base.lexer;

//import br.com.vinyanalista.portugol.base.node.*;

import java.io.*;

public class CustomLexer extends Lexer {

	/*private int contadorDeComentarios;
	private int linhaDoComentario;
	private int posicaoDoComentario;
	private StringBuilder textoDoComentario;*/

	public CustomLexer(PushbackReader in) {
		super(in);
		/*contadorDeComentarios = 0;
		linhaDoComentario = 0;
		posicaoDoComentario = 0;
		textoDoComentario = null;*/
	}

	@Override
	protected void filter() throws LexerException, IOException {
		// Tratamento de comentários aninhados
		// Verifica se está no estado comentário
		/*if (state.equals(State.COMENTARIO)) {
			// Verifica se acabou de entrar no estado comentário
			if (textoDoComentario == null) {
				// O token reconhecido deve ser um comentário
				// A referência ao token é mantida e ao contador é atribuído o
				// valor 1
				contadorDeComentarios = 1;
				linhaDoComentario = token.getLine();
				posicaoDoComentario = token.getPos();
				textoDoComentario = new StringBuilder(token.getText());
				token = null; // Continuar a ler da entrada
			} else {
				// Já estava no estado comentário
				textoDoComentario.append(token.getText()); // Acumula o texto
				if (token instanceof TComentarioA) {
					contadorDeComentarios++;
				} else if (token instanceof TComentarioF) {
					contadorDeComentarios--;
					// Verifica se o fim da entrada foi atingido
				} else if (token instanceof EOF) {
					// Se sim, o comentário não tem fim, então lança uma exceção
					throw new LexerException(new InvalidToken(textoDoComentario
							.toString(), token.getLine(), token.getPos()), "["
							+ (token.getLine()) + "," + (token.getPos())
							+ "] Fim do comentário não encontrado");
				}
				if (contadorDeComentarios != 0) {
					token = null; // Continuar a ler da entrada
				} else {
					token = new TComentarioBloco(textoDoComentario.toString(),
							linhaDoComentario, posicaoDoComentario); // Retorna
																		// o
																		// comentário
					state = State.NORMAL; // Volta ao estado normal
					textoDoComentario = null; // Libera a referência ao
												// comentário
				}
			}
		}*/
	}

}