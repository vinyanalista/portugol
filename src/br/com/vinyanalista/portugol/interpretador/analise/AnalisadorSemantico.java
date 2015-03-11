package br.com.vinyanalista.portugol.interpretador.analise;

import br.com.vinyanalista.portugol.base.analysis.*;
import br.com.vinyanalista.portugol.base.lexer.LexerException;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.base.parser.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.subrotina.SubrotinaPreDefinida;
import br.com.vinyanalista.portugol.interpretador.tipo.TipoSubrotina;

import java.io.IOException;
import java.util.*;

public class AnalisadorSemantico extends DepthFirstAdapter {
    
	private final AnalisadorDeComandos analisadorDeComandos;
    private final AnalisadorDeDeclaracoes analisadorDeDeclaracoes;
    private final AnalisadorDeExpressoes analisadorDeExpressoes;
    private Start arvoreSintaticaAbstrata;
    private ErroSemantico erroSemantico;
    private final StringBuilder log;
    private final Parser parser;
    private TabelaDeSimbolos tabelaDeSimbolos;
    private final HashMap<Node, TabelaDeAtributos> tabelasDeAtributos;

    public AnalisadorSemantico(Parser parser) {
        analisadorDeComandos = new AnalisadorDeComandos(this);
        analisadorDeDeclaracoes = new AnalisadorDeDeclaracoes(this);
        analisadorDeExpressoes = new AnalisadorDeExpressoes(this);
        this.arvoreSintaticaAbstrata = null;
        erroSemantico = null;
        log = new StringBuilder();
        this.parser = parser;
        tabelaDeSimbolos = new TabelaDeSimbolos();
        tabelasDeAtributos = new HashMap<Node, TabelaDeAtributos>();
        adicionarSubrotinasPreDefinidas();
    }
    
    public AnalisadorSemantico(Start arvoreSintaticaAbstrata) {
        analisadorDeComandos = new AnalisadorDeComandos(this);
        analisadorDeDeclaracoes = new AnalisadorDeDeclaracoes(this);
        analisadorDeExpressoes = new AnalisadorDeExpressoes(this);
        this.arvoreSintaticaAbstrata = arvoreSintaticaAbstrata;
        erroSemantico = null;
        log = new StringBuilder();
        this.parser = null;
        tabelaDeSimbolos = new TabelaDeSimbolos();
        tabelasDeAtributos = new HashMap<Node, TabelaDeAtributos>();
        adicionarSubrotinasPreDefinidas();
    }
    
	private void adicionarSubrotinasPreDefinidas() {
		for (SubrotinaPreDefinida subrotina : SubrotinaPreDefinida.values()) {
			tabelaDeSimbolos.inserir(subrotina.getSimbolo(), subrotina
					.getTabelaDeAtributos());
		}
	}
    
    public void analisar() throws IOException, LexerException, ParserException, ErroSemantico {
    	if (log.length() != 0) {
    		return;
    	}
    	if (arvoreSintaticaAbstrata == null) {
    		if (parser != null) {
    			arvoreSintaticaAbstrata = parser.parse();
    		} else {
    			return;
    		}
    	}
        arvoreSintaticaAbstrata.apply(this);
        if (erroSemantico != null) {
            throw erroSemantico;
        }
        logar("Tudo OK");
    }
    
    void analisarComando(Node comando) {
    	if (comando instanceof ABlocoComando) {
    		comando.apply(this);
    	} else {
    		comando.apply(analisadorDeComandos);
    	}
    }
    
    void analisarDeclaracao(Node declaracao) {
        declaracao.apply(analisadorDeDeclaracoes);
    }
    
    void analisarExpressao(Node expressao) {
        expressao.apply(analisadorDeExpressoes);
    }

    public Start getArvoreSintaticaAbstrata() {
        return arvoreSintaticaAbstrata;
    }
    
    public String getLog() {
		return log.toString();
	}
    
    public TabelaDeSimbolos getTabelaDeSimbolos() {
        return tabelaDeSimbolos;
    }

    public HashMap<Node, TabelaDeAtributos> getTabelasDeAtributos() {
        return tabelasDeAtributos;
    }
    
    public void gravarAtributos(Node no, TabelaDeAtributos atributos) {
        tabelasDeAtributos.put(no, atributos);
    }
    
    public boolean haErroSemantico() {
        return (erroSemantico != null);
    }
    
    void lancarErroSemantico(Node no, int linha, int coluna, String mensagem) {
    	if (erroSemantico == null) {
    		erroSemantico = new ErroSemantico(no, "[" + linha + "," + coluna + "] " + mensagem, linha, coluna);
    	}
    }
    
    public void logar(String mensagem) {
    	log.append(mensagem);
    }
    
    public TabelaDeAtributos obterAtributos(Node no) {
        return tabelasDeAtributos.get(no);
    }
    
    @Override
    public void caseAAlgoritmo(AAlgoritmo algoritmo) {
    	for (PDeclaracao declaracao : algoritmo.getDeclaracao()) {
    		declaracao.apply(this);
    	}
    	for (PSubRotina subrotina : algoritmo.getSubRotina()) {
    		TabelaDeSimbolos tabelaDeSimbolosDoAlgoritmo = tabelaDeSimbolos;
			tabelaDeSimbolos = new TabelaDeSimbolos();
    		for (PDeclaracao parametro : ((ASubRotina) subrotina).getParametros()) {
    			analisarDeclaracao(parametro);
    		}
    		tabelaDeSimbolos = tabelaDeSimbolosDoAlgoritmo;
    		analisarDeclaracao(subrotina);
    	}
    	for (PComando comando : algoritmo.getComando()) {
    		comando.apply(this);
    	}
    	logar("Algoritmo analisado\n");
    	for (PSubRotina subrotina : algoritmo.getSubRotina()) {
    		subrotina.apply(this);
    	}
    }
    
    @Override
    public void caseADeclaracao(ADeclaracao declaracao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        analisarDeclaracao(declaracao);
    }
    
    @Override
    public void caseAAtribuicaoComando(AAtribuicaoComando comando) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        analisarExpressao(comando.getPosicaoDeMemoria());
        analisarExpressao(comando.getExpressao());
        analisarComando(comando);
    }

    @Override
    public void caseAEntradaComando(AEntradaComando comando) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        for (PPosicaoDeMemoria posicaoDeMemoria : comando.getPosicaoDeMemoria()) {
            analisarExpressao(posicaoDeMemoria);
        }
        analisarComando(comando);
    }

    @Override
    public void caseASaidaComando(ASaidaComando comando) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        for (PExpressao expressao : comando.getExpressao()) {
            analisarExpressao(expressao);
        }
        analisarComando(comando);
    }
    
    @Override
    public void caseABlocoComando(ABlocoComando bloco) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        for (PComando comando : bloco.getComando()) {
        	comando.apply(this);
        }
    }

    @Override
    public void caseACondicionalComando(ACondicionalComando comandoCondicional) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        analisarExpressao(comandoCondicional.getExpressao());
        if (comandoCondicional.getEntao() != null) {
        	comandoCondicional.getEntao().apply(this);
        }
        if (comandoCondicional.getSenao() != null) {
        	comandoCondicional.getSenao().apply(this);
        }
        analisarComando(comandoCondicional);
    }
    
    @Override
    public void caseARepeticaoParaComando(ARepeticaoParaComando comandoDeRepeticao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        analisarExpressao(comandoDeRepeticao.getVariavel());
        analisarExpressao(comandoDeRepeticao.getInicio());
        analisarExpressao(comandoDeRepeticao.getFim());
        comandoDeRepeticao.getComando().apply(this);
        analisarComando(comandoDeRepeticao);
    }
    
    @Override
    public void caseARepeticaoEnquantoComando(ARepeticaoEnquantoComando comandoDeRepeticao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        analisarExpressao(comandoDeRepeticao.getExpressao());
        comandoDeRepeticao.getComando().apply(this);
        analisarComando(comandoDeRepeticao);
    }
    
    @Override
    public void caseARepeticaoRepitaComando(ARepeticaoRepitaComando comandoDeRepeticao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        for (PComando comando : comandoDeRepeticao.getComando()) {
        	comando.apply(this);
        }
        analisarExpressao(comandoDeRepeticao.getExpressao());
        analisarComando(comandoDeRepeticao);
    }
    
    @Override
    public void caseAChamadaASubRotinaComando(AChamadaASubRotinaComando comando) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
    	analisarExpressao(comando.getChamadaASubRotina());
    	analisarComando(comando);
    }
    
    @Override
    public void caseARetorneComando(ARetorneComando comando) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
        analisarExpressao(comando.getExpressao());
    	analisarComando(comando);
    }
    
    @Override
    public void caseASubRotina(ASubRotina subrotina) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (haErroSemantico()) {
            return;
        }
    	TabelaDeSimbolos tabelaDeSimbolosDoAlgoritmo = tabelaDeSimbolos;
    	TabelaDeAtributos atributosDaSubrotina = obterAtributos(subrotina);
    	String stringDaSubrotina = (String) atributosDaSubrotina.obter(Atributo.STRING);
    	TipoSubrotina tipoDaSubrotina = (TipoSubrotina) atributosDaSubrotina.obter(Atributo.TIPO);
    	tabelaDeSimbolos = tipoDaSubrotina.getTabelaDeSimbolos();
    	for (PDeclaracao declaracao : subrotina.getVariaveisLocais()) {
    		analisarDeclaracao(declaracao);
    	}
    	for (PComando comando : subrotina.getComando()) {
    		comando.apply(this);
    	}
    	tabelaDeSimbolos = tabelaDeSimbolosDoAlgoritmo;
    	logar("Implementação da sub-rotina " + stringDaSubrotina + " analisada\n");
    }
}