package br.com.vinyanalista.portugol.interpretador.simbolo;

public enum Atributo {
    SIMBOLO,    // Simbolo
    ID,         // String
    LINHA,      // Integer
    COLUNA,     // Integer
    TIPO,       // Tipo ou TipoArray (que herda de Tipo)
    CAPACIDADE, // Integer
    POSICAO, // Integer
    VALOR, // Integer, String ou Float (?)
    SOMENTE_LEITURA,    // Boolean
    STRING,  // String (apenas para fins de teste)
    INICIO, // Integer
    PASSO,  // Integer
    FIM,    // Integer
    REFERENCIA; // Referencia
}