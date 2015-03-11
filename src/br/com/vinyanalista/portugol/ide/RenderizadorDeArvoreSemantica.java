package br.com.vinyanalista.portugol.ide;

import br.com.vinyanalista.portugol.base.analysis.DepthFirstAdapter;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.Interpretador;

import javax.swing.tree.DefaultMutableTreeNode;

public class RenderizadorDeArvoreSemantica extends DepthFirstAdapter {
    private Interpretador interpretador;

    public RenderizadorDeArvoreSemantica(Interpretador interpretador) {
        this.interpretador = interpretador;
    }
    
    public DefaultMutableTreeNode renderizar() {
        interpretador.getArvoreSintatica().apply(this);
        
        return (DefaultMutableTreeNode) getOut(interpretador.getArvoreSintatica().getPAlgoritmo());
    }
    
    @Override
    public void outAAlgoritmo(AAlgoritmo algoritmo) {
    	NoComTabelaDeAtributos noAlgoritmo = new NoComTabelaDeAtributos("Algoritmo");
    	if (algoritmo.getDeclaracao().size() > 0) {
	        NoComTabelaDeAtributos noDeclaracoes = new NoComTabelaDeAtributos("Declarações");
	        for (PDeclaracao declaracao : algoritmo.getDeclaracao()) {
	            noDeclaracoes.add((NoComTabelaDeAtributos) getOut(declaracao));
	        }
	        noAlgoritmo.add(noDeclaracoes);
    	}
    	if (algoritmo.getComando().size() > 0) {
	    	NoComTabelaDeAtributos noComandos = new NoComTabelaDeAtributos("Comandos");
	        for (PComando comando : algoritmo.getComando()) {
	            noComandos.add((NoComTabelaDeAtributos) getOut(comando));
	        }
	        noAlgoritmo.add(noComandos);
    	}
    	if (algoritmo.getSubRotina().size() > 0) {
	        NoComTabelaDeAtributos noSubrotinas = new NoComTabelaDeAtributos("Sub-rotinas");
	        for (PSubRotina subrotina : algoritmo.getSubRotina()) {
	        	noSubrotinas.add((NoComTabelaDeAtributos) getOut(subrotina));
	        }
	        noAlgoritmo.add(noSubrotinas);
    	}
        noAlgoritmo.setTabelaDeAtributos(interpretador.getAtributos(algoritmo));
        setOut(algoritmo, noAlgoritmo);
    }
    
    @Override
    public void outADeclaracao(ADeclaracao declaracao) {
    	NoComTabelaDeAtributos noDeclaracao = new NoComTabelaDeAtributos("Declaração");
        NoComTabelaDeAtributos noVariaveis = new NoComTabelaDeAtributos("Variáveis");
        for (PVariavel variavel : declaracao.getVariavel()) {
            noVariaveis.add((NoComTabelaDeAtributos) getOut(variavel));
        }
        noDeclaracao.add(noVariaveis);
        noDeclaracao.add((NoComTabelaDeAtributos) getOut(declaracao.getTipo()));
        noDeclaracao.setTabelaDeAtributos(interpretador.getAtributos(declaracao));
        setOut(declaracao, noDeclaracao);
    }
    
    @Override
    public void outASimplesVariavel(ASimplesVariavel variavel) {
    	NoComTabelaDeAtributos noVariavel = new NoComTabelaDeAtributos("Variável");
        noVariavel.setTabelaDeAtributos(interpretador.getAtributos(variavel));
        setOut(variavel, noVariavel);
    }
    
    @Override
    public void outAVetorOuMatrizVariavel(AVetorOuMatrizVariavel vetorOuMatriz) {
    	String string;
    	if (vetorOuMatriz.getExpressao().size() == 1) {
			string = "Vetor";
		} else {
			string = "Matriz";
		}
    	NoComTabelaDeAtributos noVetorOuMatriz = new NoComTabelaDeAtributos(string);
    	noVetorOuMatriz.setTabelaDeAtributos(interpretador.getAtributos(vetorOuMatriz));
        setOut(vetorOuMatriz, noVetorOuMatriz);
    }
    
    @Override
    public void outANumericoTipo(ANumericoTipo tipo) {
    	NoComTabelaDeAtributos noTipo = new NoComTabelaDeAtributos("Tipo numérico");
        noTipo.setTabelaDeAtributos(interpretador.getAtributos(tipo));
        setOut(tipo, noTipo);
    }
    
    @Override
    public void outALiteralTipo(ALiteralTipo tipo) {
    	NoComTabelaDeAtributos noTipo = new NoComTabelaDeAtributos("Tipo literal");
        noTipo.setTabelaDeAtributos(interpretador.getAtributos(tipo));
        setOut(tipo, noTipo);
    }
    
    @Override
    public void outALogicoTipo(ALogicoTipo tipo) {
    	NoComTabelaDeAtributos noTipo = new NoComTabelaDeAtributos("Tipo lógico");
        noTipo.setTabelaDeAtributos(interpretador.getAtributos(tipo));
        setOut(tipo, noTipo);
    }
    
    @Override
    public void outARegistroTipo(ARegistroTipo tipo) {
    	NoComTabelaDeAtributos noTipo = new NoComTabelaDeAtributos("Tipo registro");
        for (PDeclaracao declaracao : tipo.getDeclaracao()) {
    		for (PVariavel campo : ((ADeclaracao) declaracao).getVariavel()) {
    			noTipo.add((NoComTabelaDeAtributos) getOut(campo));
    		}
        }
        noTipo.setTabelaDeAtributos(interpretador.getAtributos(tipo));
        setOut(tipo, noTipo);
    }
    
    @Override
    public void outAAtribuicaoComando(AAtribuicaoComando comando) {
    	NoComTabelaDeAtributos noComando = new NoComTabelaDeAtributos("Comando de atribuição");
        noComando.add((NoComTabelaDeAtributos) getOut(comando.getPosicaoDeMemoria()));
        noComando.add((NoComTabelaDeAtributos) getOut(comando.getExpressao()));
        noComando.setTabelaDeAtributos(interpretador.getAtributos(comando));
        setOut(comando, noComando);
    }
    
    @Override
    public void outAEntradaComando(AEntradaComando comando) {
        NoComTabelaDeAtributos noComando = new NoComTabelaDeAtributos("Comando de entrada");
        for (PPosicaoDeMemoria posicaoDeMemoria : comando.getPosicaoDeMemoria()) {
        	noComando.add((NoComTabelaDeAtributos) getOut(posicaoDeMemoria));
        }
        noComando.setTabelaDeAtributos(interpretador.getAtributos(comando));
        setOut(comando, noComando);
    }

    @Override
    public void outASaidaComando(ASaidaComando comando) {
        NoComTabelaDeAtributos noComando = new NoComTabelaDeAtributos("Comando de saída");
        for (PExpressao expressao : comando.getExpressao()) {
        	noComando.add((NoComTabelaDeAtributos) getOut(expressao));
        }
        noComando.setTabelaDeAtributos(interpretador.getAtributos(comando));
        setOut(comando, noComando);
    }
    
    @Override
    public void outABlocoComando(ABlocoComando bloco) {
    	NoComTabelaDeAtributos noBloco = new NoComTabelaDeAtributos("Bloco de comandos");
    	for (PComando comando : bloco.getComando()) {
    		noBloco.add((NoComTabelaDeAtributos) getOut(comando));
    	}
    	setOut(bloco, noBloco);
    }
    
    @Override
    public void outACondicionalComando(ACondicionalComando comandoCondicional) {
        NoComTabelaDeAtributos noComandoCondicional = new NoComTabelaDeAtributos("Estrutura condicional");
        NoComTabelaDeAtributos noExpressaoLogica = new NoComTabelaDeAtributos("Expressão lógica");
        noExpressaoLogica.add((NoComTabelaDeAtributos) getOut(comandoCondicional.getExpressao()));
        noComandoCondicional.add(noExpressaoLogica);
        if (comandoCondicional.getEntao() != null) {
        	NoComTabelaDeAtributos noEntao = new NoComTabelaDeAtributos("Então");
        	noEntao.add((NoComTabelaDeAtributos) getOut(comandoCondicional.getEntao()));
        	noComandoCondicional.add(noEntao);
        }
        if (comandoCondicional.getSenao() != null) {
        	NoComTabelaDeAtributos noSenao = new NoComTabelaDeAtributos("Senão");
        	noSenao.add((NoComTabelaDeAtributos) getOut(comandoCondicional.getSenao()));
        	noComandoCondicional.add(noSenao);
        }
        noComandoCondicional.setTabelaDeAtributos(interpretador.getAtributos(comandoCondicional));
        setOut(comandoCondicional, noComandoCondicional);
    }
    
    @Override
    public void outARepeticaoParaComando(ARepeticaoParaComando comandoDeRepeticao) {
        NoComTabelaDeAtributos noComandoDeRepeticao = new NoComTabelaDeAtributos("Estrutura de repetição PARA");
        NoComTabelaDeAtributos noVariavel = new NoComTabelaDeAtributos("Variável");
        noVariavel.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getVariavel()));
        noComandoDeRepeticao.add(noVariavel);
        NoComTabelaDeAtributos noInicio = new NoComTabelaDeAtributos("Valor inicial");
        noInicio.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getInicio()));
        noComandoDeRepeticao.add(noInicio);
        NoComTabelaDeAtributos noFim = new NoComTabelaDeAtributos("Valor final");
        noFim.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getFim()));
        noComandoDeRepeticao.add(noFim);
        if (comandoDeRepeticao.getComando() instanceof ABlocoComando) {
        	noComandoDeRepeticao.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getComando()));
        } else {
        	NoComTabelaDeAtributos noComando = new NoComTabelaDeAtributos("Comando");
        	noComando.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getComando()));
        	noComandoDeRepeticao.add(noComando);
        }
        noComandoDeRepeticao.setTabelaDeAtributos(interpretador.getAtributos(comandoDeRepeticao));
        setOut(comandoDeRepeticao, noComandoDeRepeticao);
    }
    
    @Override
    public void outARepeticaoEnquantoComando(ARepeticaoEnquantoComando comandoDeRepeticao) {
    	NoComTabelaDeAtributos noComandoDeRepeticao = new NoComTabelaDeAtributos("Estrutura de repetição ENQUANTO");
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Expressão lógica");
        noExpressao.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getExpressao()));
        noComandoDeRepeticao.add(noExpressao);
    	if (comandoDeRepeticao.getComando() instanceof ABlocoComando) {
        	noComandoDeRepeticao.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getComando()));
        } else {
        	NoComTabelaDeAtributos noComando = new NoComTabelaDeAtributos("Comando");
        	noComando.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getComando()));
        	noComandoDeRepeticao.add(noComando);
        }
        noComandoDeRepeticao.setTabelaDeAtributos(interpretador.getAtributos(comandoDeRepeticao));
        setOut(comandoDeRepeticao, noComandoDeRepeticao);
    }
    
    @Override
    public void outARepeticaoRepitaComando(ARepeticaoRepitaComando comandoDeRepeticao) {
    	NoComTabelaDeAtributos noComandoDeRepeticao = new NoComTabelaDeAtributos("Estrutura de repetição REPITA");
    	NoComTabelaDeAtributos noComandos = new NoComTabelaDeAtributos("Comandos");
    	for (PComando comando : comandoDeRepeticao.getComando()) {
    		noComandos.add((NoComTabelaDeAtributos) getOut(comando));
    	}
    	noComandoDeRepeticao.add(noComandos);
        NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Expressão lógica");
        noExpressao.add((NoComTabelaDeAtributos) getOut(comandoDeRepeticao.getExpressao()));
        noComandoDeRepeticao.add(noExpressao);
        noComandoDeRepeticao.setTabelaDeAtributos(interpretador.getAtributos(comandoDeRepeticao));
        setOut(comandoDeRepeticao, noComandoDeRepeticao);
    }
    
    @Override
    public void outAChamadaASubRotinaComando(AChamadaASubRotinaComando comando) {
    	setOut(comando, getOut(comando.getChamadaASubRotina()));
    }
    
    @Override
    public void outARetorneComando(ARetorneComando comando) {
    	NoComTabelaDeAtributos noComando = new NoComTabelaDeAtributos("Comando de retorno");
    	noComando.setTabelaDeAtributos(interpretador.getAtributos(comando));
        setOut(comando, noComando);
    }
    
    @Override
    public void outAVariavelPosicaoDeMemoria(AVariavelPosicaoDeMemoria posicaoDeMemoria) {
        setOut(posicaoDeMemoria, getOut(posicaoDeMemoria.getVariavel()));
    }
    
    @Override
    public void outACampoPosicaoDeMemoria(ACampoPosicaoDeMemoria posicaoDeMemoria) {
    	NoComTabelaDeAtributos noPosicaoDeMemoria = new NoComTabelaDeAtributos("Campo de registro");
    	noPosicaoDeMemoria.setTabelaDeAtributos(interpretador.getAtributos(posicaoDeMemoria));
        setOut(posicaoDeMemoria, noPosicaoDeMemoria);
    }
    
    @Override
    public void outADisjuncaoExpressao(ADisjuncaoExpressao expressao) {
        NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Disjunção");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAConjuncaoExpressao(AConjuncaoExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Conjunção");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAIgualdadeExpressao(AIgualdadeExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Igualdade");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outADiferencaExpressao(ADiferencaExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Diferença");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAMenorExpressao(AMenorExpressao expressao) {
        NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Menor");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAMenorOuIgualExpressao(AMenorOuIgualExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Menor ou igual");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAMaiorExpressao(AMaiorExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Maior");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAMaiorOuIgualExpressao(AMaiorOuIgualExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Maior ou igual");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outASomaExpressao(ASomaExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Soma");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outASubtracaoExpressao(ASubtracaoExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Subtração");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAMultiplicacaoExpressao(AMultiplicacaoExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Multiplicação");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outADivisaoExpressao(ADivisaoExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Divisão");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getEsquerda()));
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getDireita()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAPositivoExpressao(APositivoExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Positivo");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getExpressao()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outANegativoExpressao(ANegativoExpressao expressao) {
    	NoComTabelaDeAtributos noExpressao = new NoComTabelaDeAtributos("Negativo");
        noExpressao.add((NoComTabelaDeAtributos) getOut(expressao.getExpressao()));
        noExpressao.setTabelaDeAtributos(interpretador.getAtributos(expressao));
        setOut(expressao, noExpressao);
    }
    
    @Override
    public void outAPosicaoDeMemoriaExpressao(APosicaoDeMemoriaExpressao expressao) {
		setOut(expressao, getOut(expressao.getPosicaoDeMemoria()));
    }
    
    @Override
    public void outAChamadaASubRotinaExpressao(AChamadaASubRotinaExpressao expressao) {
    	setOut(expressao, getOut(expressao.getChamadaASubRotina()));
    }
    
    @Override
    public void outAValorExpressao(AValorExpressao expressao) {
    	setOut(expressao, getOut(expressao.getValor()));
    }
    
    @Override
    public void outAChamadaASubRotina(AChamadaASubRotina chamada) {
    	NoComTabelaDeAtributos noChamada = new NoComTabelaDeAtributos("Chamada a sub-rotina");
    	if (chamada.getExpressao().size() > 0) {
	    	NoComTabelaDeAtributos noArgumentos = new NoComTabelaDeAtributos("Argumentos");
			for (PExpressao expressao : chamada.getExpressao()) {
				noArgumentos.add((NoComTabelaDeAtributos) getOut(expressao));
			}
	    	noChamada.add(noArgumentos);
    	}
    	noChamada.setTabelaDeAtributos(interpretador.getAtributos(chamada));
        setOut(chamada, noChamada);
    }
    
    @Override
    public void outAInteiroValor(AInteiroValor valor) {
    	NoComTabelaDeAtributos noValor = new NoComTabelaDeAtributos("Valor inteiro");
        noValor.setTabelaDeAtributos(interpretador.getAtributos(valor));
        setOut(valor, noValor);
    }
    
    @Override
    public void outARealValor(ARealValor valor) {
    	NoComTabelaDeAtributos noValor = new NoComTabelaDeAtributos("Valor real");
        noValor.setTabelaDeAtributos(interpretador.getAtributos(valor));
        setOut(valor, noValor);
    }
    
    @Override
    public void outALogicoValor(ALogicoValor valor) {
    	NoComTabelaDeAtributos noValor = new NoComTabelaDeAtributos("Valor lógico");
        noValor.setTabelaDeAtributos(interpretador.getAtributos(valor));
        setOut(valor, noValor);
    }
    
    @Override
    public void outALiteralValor(ALiteralValor valor) {
    	NoComTabelaDeAtributos noValor = new NoComTabelaDeAtributos("Valor literal");
        noValor.setTabelaDeAtributos(interpretador.getAtributos(valor));
        setOut(valor, noValor);
    }
    
    @Override
    public void outASubRotina(ASubRotina subrotina) {
    	NoComTabelaDeAtributos noSubrotina = new NoComTabelaDeAtributos("Sub-rotina");
    	if (subrotina.getParametros().size() > 0) {
	    	NoComTabelaDeAtributos noParametros = new NoComTabelaDeAtributos("Parâmetros");
	        for (PDeclaracao declaracao : subrotina.getParametros()) {
	        	noParametros.add((NoComTabelaDeAtributos) getOut(declaracao));
	        }
	        noSubrotina.add(noParametros);
    	}
    	if (subrotina.getVariaveisLocais().size() > 0) {
	        NoComTabelaDeAtributos noVariaveisLocais = new NoComTabelaDeAtributos("Variáveis locais");
	        for (PDeclaracao declaracao : subrotina.getVariaveisLocais()) {
	        	noVariaveisLocais.add((NoComTabelaDeAtributos) getOut(declaracao));
	        }
	        noSubrotina.add(noVariaveisLocais);
    	}
    	if (subrotina.getComando().size() > 0) {
	        NoComTabelaDeAtributos noComandos = new NoComTabelaDeAtributos("Comandos");
	        for (PComando comando : subrotina.getComando()) {
	            noComandos.add((NoComTabelaDeAtributos) getOut(comando));
	        }
	        noSubrotina.add(noComandos);
    	}
        noSubrotina.setTabelaDeAtributos(interpretador.getAtributos(subrotina));
        setOut(subrotina, noSubrotina);
    }
    
}