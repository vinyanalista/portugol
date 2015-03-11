package br.com.vinyanalista.portugol.interpretador.analise;

import br.com.vinyanalista.portugol.base.analysis.*;
import br.com.vinyanalista.portugol.base.node.*;
import br.com.vinyanalista.portugol.interpretador.simbolo.*;
import br.com.vinyanalista.portugol.interpretador.tipo.*;

public class AnalisadorDeComandos extends DepthFirstAdapter {

    private final AnalisadorSemantico analisadorSemantico;

    public AnalisadorDeComandos(AnalisadorSemantico analisadorSemantico) {
        this.analisadorSemantico = analisadorSemantico;
    }
    
    @Override
    public void caseAAtribuicaoComando(AAtribuicaoComando comando) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos dos operandos
        TabelaDeAtributos atributosDaPosicaoDeMemoria = analisadorSemantico.obterAtributos(comando.getPosicaoDeMemoria());
        TabelaDeAtributos atributosDaExpressao = analisadorSemantico.obterAtributos(comando.getExpressao());
        // Determina os atributos do comando
        int linha = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.LINHA);
        int coluna = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.COLUNA);
        String string = atributosDaPosicaoDeMemoria.obter(Atributo.STRING) + " <- " + atributosDaExpressao.obter(Atributo.STRING);
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        atributosDoComando.inserir(Atributo.LINHA, linha);
        atributosDoComando.inserir(Atributo.COLUNA, coluna);
        atributosDoComando.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(comando, atributosDoComando);
        // Obtém os tipos dos operandos
        Tipo tipoDaPosicaoDeMemoria = (Tipo) atributosDaPosicaoDeMemoria.obter(Atributo.TIPO);
        Tipo tipoDaExpressao = (Tipo) atributosDaExpressao.obter(Atributo.TIPO);
        // Verifica se os tipos são compatíveis
        String stringDaPosicaoDeMemoria = (String) atributosDaPosicaoDeMemoria.obter(Atributo.STRING);
    	String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
        if (!tipoDaPosicaoDeMemoria.ehCompativel(tipoDaExpressao)) {
            // Se não são compatíveis, lança um erro semântico
            analisadorSemantico.lancarErroSemantico(comando, linha, 0, "Não é possível atribuir " + stringDaExpressao + " (um " + tipoDaExpressao + ") a " + stringDaPosicaoDeMemoria + " (um " + tipoDaPosicaoDeMemoria + ")");
            return;
        }        
        analisadorSemantico.logar("Comando de atribuição analisado, os tipos da posição de memória " + stringDaPosicaoDeMemoria + " e da expressão " + stringDaExpressao + " são compatíveis\n");
    }

    @Override
    public void caseAEntradaComando(AEntradaComando comando) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos de uma das posições de memória
        // TabelaDeAtributos atributosDaPrimeiraPosicaoDeMemoria = analisadorSemantico.obterAtributos(comando.getPosicaoDeMemoria().getFirst());
        // Determina os atributos do comando de entrada
        // int linha = (Integer) atributosDaPrimeiraPosicaoDeMemoria.obter(Atributo.LINHA);
        // int coluna = (Integer) atributosDaPrimeiraPosicaoDeMemoria.obter(Atributo.COLUNA);
        String string = "LEIA [...]";
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        // atributosDoComando.inserir(Atributo.LINHA, linha);
        // atributosDoComando.inserir(Atributo.COLUNA, coluna);
        atributosDoComando.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(comando, atributosDoComando);
        // Verifica se as posições de memória podem de fato receber entrada do usuário
        for (PPosicaoDeMemoria posicaoDeMemoria : comando.getPosicaoDeMemoria()) {
            TabelaDeAtributos atributosDaPosicaoDeMemoria = analisadorSemantico.obterAtributos(posicaoDeMemoria);
            Tipo tipoDaPosicaoDeMemoria = (Tipo) atributosDaPosicaoDeMemoria.obter(Atributo.TIPO);
            if (!tipoDaPosicaoDeMemoria.podeReceberEntradaDoUsuario()) {
                int linhaDoUso = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.LINHA);
                int colunaDoUso = (Integer) atributosDaPosicaoDeMemoria.obter(Atributo.COLUNA);
                analisadorSemantico.lancarErroSemantico(comando, linhaDoUso, colunaDoUso, "Não é possível atribuir um valor a " + atributosDaPosicaoDeMemoria.obter(Atributo.STRING) + " do tipo " + tipoDaPosicaoDeMemoria + ".");
                return;
            }
        }
        analisadorSemantico.logar("Comando de entrada analisado\n");
    }

    @Override
    public void caseASaidaComando(ASaidaComando comando) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos de uma das expressões
        // TabelaDeAtributos atributosDaPrimeiraExpressao = analisadorSemantico.obterAtributos(comando.getExpressao().getFirst());
        // Determina os atributos do comando
        // int linha = (Integer) atributosDaPrimeiraExpressao.obter(Atributo.LINHA);
        // int coluna = (Integer) atributosDaPrimeiraExpressao.obter(Atributo.COLUNA);
        String string = "ESCREVA [...]";
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        // atributosDoComando.inserir(Atributo.LINHA, linha);
        // atributosDoComando.inserir(Atributo.COLUNA, coluna);
        atributosDoComando.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(comando, atributosDoComando);
        analisadorSemantico.logar("Comando de saída analisado\n");
    }

    @Override
    public void caseACondicionalComando(ACondicionalComando comando) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos da expressão
        TabelaDeAtributos atributosDaExpressao = analisadorSemantico.obterAtributos(comando.getExpressao());
        // Obtém o tipo da expressão
        Tipo tipoDaExpressao = (Tipo) atributosDaExpressao.obter(Atributo.TIPO);
        // Determina os atributos do comando
        // int linha = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
        // int coluna = (Integer) atributosDaExpressao.obter(Atributo.COLUNA);
        String string = "SE " + atributosDaExpressao.obter(Atributo.STRING) + " ENTAO [...]" + (comando.getSenao() != null ? " SENAO [...]" : "");
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        // atributosDoComando.inserir(Atributo.LINHA, linha);
        // atributosDoComando.inserir(Atributo.COLUNA, coluna);
        atributosDoComando.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(comando, atributosDoComando);
        // Verifica se o tipo da expressão é lógico
        if (!(tipoDaExpressao.ehLogico())){
            // Se não é lógico, lança um erro semântico
        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
            int colunaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.COLUNA);
            analisadorSemantico.lancarErroSemantico(comando, linhaDaExpressao, colunaDaExpressao, "A estrutura condicional requer uma expressão lógica após a palavra reservada SE");
            return;
        }
        analisadorSemantico.logar("Estrutura condicional analisada\n");
    }
    
    @Override
    public void caseARepeticaoParaComando(ARepeticaoParaComando comandoDeRepeticao) {
        // A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos da variável e das expressões
        TabelaDeAtributos atributosDaVariavel = analisadorSemantico.obterAtributos(comandoDeRepeticao.getVariavel());
        TabelaDeAtributos atributosDaExpressaoInicio = analisadorSemantico.obterAtributos(comandoDeRepeticao.getInicio());
        TabelaDeAtributos atributosDaExpressaoFim = analisadorSemantico.obterAtributos(comandoDeRepeticao.getFim());
        // Determina os atributos do comando
        // int linha = (Integer) atributosDaVariavel.obter(Atributo.LINHA);
        // int coluna = (Integer) atributosDaVariavel.obter(Atributo.COLUNA);
        Integer passo = null;
        if (comandoDeRepeticao.getPasso() != null) {
        	passo = Integer.parseInt(comandoDeRepeticao.getPasso().getText());
        }
        String stringDaVariavel = (String) atributosDaVariavel.obter(Atributo.STRING);
        String stringDaExpressaoInicio = (String) atributosDaExpressaoInicio.obter(Atributo.STRING);
        String stringDaExpressaoFim = (String) atributosDaExpressaoFim.obter(Atributo.STRING);
        String string = "PARA " + stringDaVariavel + " <- " + stringDaExpressaoInicio + " ATE " + stringDaExpressaoFim + " FACA" + (passo != null ? " PASSO " + passo : "") + " [...]";
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        // atributosDoComando.inserir(Atributo.LINHA, linha);
        // atributosDoComando.inserir(Atributo.COLUNA, coluna);
        atributosDoComando.inserir(Atributo.STRING, string);
        if (passo != null) {
        	atributosDoComando.inserir(Atributo.PASSO, passo);
        }
        analisadorSemantico.gravarAtributos(comandoDeRepeticao, atributosDoComando);
        // Verifica se a variável é do tipo numérico
        Tipo tipoDaVariavel = (Tipo) atributosDaVariavel.obter(Atributo.TIPO);
        if (!tipoDaVariavel.ehNumerico()) {
            // Se não é um tipo numérico, lança um erro semântico
        	int linhaDaVariavel = (Integer) atributosDaVariavel.obter(Atributo.LINHA);
            int colunaDaVariavel = (Integer) atributosDaVariavel.obter(Atributo.COLUNA);
            analisadorSemantico.lancarErroSemantico(comandoDeRepeticao, linhaDaVariavel, colunaDaVariavel, "A variável que controla a repetição deve ser do tipo NUMERICO, " + stringDaVariavel + " é do tipo " + tipoDaVariavel);
            return;
        }        
        analisadorSemantico.logar("Estrutura de repetição PARA analisada\n");
    }
    
    @Override
    public void caseARepeticaoEnquantoComando(ARepeticaoEnquantoComando comandoDeRepeticao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos da expressão
        TabelaDeAtributos atributosDaExpressao = analisadorSemantico.obterAtributos(comandoDeRepeticao.getExpressao());
        // Obtém o tipo da expressão
        Tipo tipoDaExpressao = (Tipo) atributosDaExpressao.obter(Atributo.TIPO);
        // Determina os atributos do comando
        // int linha = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
        // int coluna = (Integer) atributosDaExpressao.obter(Atributo.COLUNA);
        String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
        String string = "ENQUANTO " + stringDaExpressao + " FACA [...]";
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        // atributosDoComando.inserir(Atributo.LINHA, linha);
        // atributosDoComando.inserir(Atributo.COLUNA, coluna);
        atributosDoComando.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(comandoDeRepeticao, atributosDoComando);
        // Verifica se o tipo da expressão é lógico
        if (!(tipoDaExpressao.ehLogico())) {
            // Se não é lógico, lança um erro semântico
        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
            int colunaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.COLUNA);
            analisadorSemantico.lancarErroSemantico(comandoDeRepeticao, linhaDaExpressao, colunaDaExpressao, "A estrutura de repetição ENQUANTO requer uma expressão lógica após a palavra reservada ENQUANTO");
            return;
        }
        analisadorSemantico.logar("Estrutura de repetição ENQUANTO analisada\n");
    }
    
    @Override
    public void caseARepeticaoRepitaComando(ARepeticaoRepitaComando comandoDeRepeticao) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
        // Obtém os atributos da expressão
        TabelaDeAtributos atributosDaExpressao = analisadorSemantico.obterAtributos(comandoDeRepeticao.getExpressao());
        // Obtém o tipo da expressão
        Tipo tipoDaExpressao = (Tipo) atributosDaExpressao.obter(Atributo.TIPO);
        // Determina os atributos do comando
        // int linha = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
        // int coluna = (Integer) atributosDaExpressao.obter(Atributo.COLUNA);
        String stringDaExpressao = (String) atributosDaExpressao.obter(Atributo.STRING);
        String string = "REPITA [...] ATE " + stringDaExpressao;
        // Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        // atributosDoComando.inserir(Atributo.LINHA, linha);
        // atributosDoComando.inserir(Atributo.COLUNA, coluna);
        atributosDoComando.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(comandoDeRepeticao, atributosDoComando);
        // Verifica se o tipo da expressão é lógico
        if (!(tipoDaExpressao.ehLogico())) {
            // Se não é lógico, lança um erro semântico
        	int linhaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.LINHA);
            int colunaDaExpressao = (Integer) atributosDaExpressao.obter(Atributo.COLUNA);
            analisadorSemantico.lancarErroSemantico(comandoDeRepeticao, linhaDaExpressao, colunaDaExpressao, "A estrutura de repetição REPITA requer uma expressão lógica após a palavra reservada ATE");
            return;
        }
        analisadorSemantico.logar("Estrutura de repetição REPITA analisada\n");
    }

    @Override
    public void caseAChamadaASubRotinaComando(AChamadaASubRotinaComando comando) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
		// Como a chamada à sub-rotina é o próprio comando, os atributos da
		// chamada são também os atributos do comando
		TabelaDeAtributos atributosDoComando = analisadorSemantico
				.obterAtributos(comando.getChamadaASubRotina());
        analisadorSemantico.gravarAtributos(comando, atributosDoComando);
    }
    
    @Override
    public void caseARetorneComando(ARetorneComando comando) {
    	// A análise semântica é suspensa quando um erro semântico é encontrado
        if (analisadorSemantico.haErroSemantico()) {
            return;
        }
    	// Determina os atributos do comando
    	String string = "RETORNE [...]";
    	// Armazena os atributos na tabela de atributos
        TabelaDeAtributos atributosDoComando = new TabelaDeAtributos();
        atributosDoComando.inserir(Atributo.STRING, string);
        analisadorSemantico.gravarAtributos(comando, atributosDoComando);
    	analisadorSemantico.logar("Comando de retorno analisado\n");
    }

}