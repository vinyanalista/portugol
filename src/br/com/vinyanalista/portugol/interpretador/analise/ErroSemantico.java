package br.com.vinyanalista.portugol.interpretador.analise;

import br.com.vinyanalista.portugol.base.node.Node;

public class ErroSemantico extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final int coluna;
    private final int linha;
    private final Node no;

    public ErroSemantico(Node no, String mensagem, int linha, int coluna) {
        super(mensagem);
        this.coluna = coluna;
        this.linha = linha;
        this.no = no;
    }

    public int getColuna() {
        return coluna;
    }
    
    public int getLinha() {
        return linha;
    }

    public Node getNo() {
        return no;
    }

}