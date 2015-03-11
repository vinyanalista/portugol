package br.com.vinyanalista.portugol.interpretador.simbolo;

import java.util.Dictionary;

public class Simbolo implements Comparable<Simbolo> {

    private String representacao;

    private Simbolo(String representacao) {
        this.representacao = representacao;
    }

    @Override
	public int compareTo(Simbolo outroSimbolo) {
		return representacao.compareTo(outroSimbolo.representacao);
	}
    
    @Override
    public String toString() {
        return representacao;
    }

    private static Dictionary simbolosConhecidos = new java.util.Hashtable();

    public static Simbolo obter(String id) {
        // Garante que cada ID seja atribuído a um único símbolo
        String representacao = id.intern();
        Simbolo simbolo = (Simbolo) simbolosConhecidos.get(representacao);
        if (simbolo == null) {
            simbolo = new Simbolo(representacao);
            simbolosConhecidos.put(representacao, simbolo);
        }
        return simbolo;
    }
	
}
