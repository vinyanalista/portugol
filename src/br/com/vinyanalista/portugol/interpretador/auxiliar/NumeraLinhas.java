package br.com.vinyanalista.portugol.interpretador.auxiliar;

import java.util.StringTokenizer;

public class NumeraLinhas {

    public static String numerar(String texto) {
        StringTokenizer quebraString = new StringTokenizer(texto.replace("\n", " \n"), "\n");
        int qtdDeAlgarismos = String.valueOf(quebraString.countTokens()).length();
        String linha = null;
        int qtdDeLinhas = 0;
        StringBuilder constroiString = new StringBuilder();
        while (quebraString.hasMoreTokens()) {
            linha = quebraString.nextToken();
            qtdDeLinhas++;
            String numDaLinha = String.valueOf(qtdDeLinhas);
            for (int i = String.valueOf(qtdDeLinhas).length(); i < qtdDeAlgarismos; i++) {
                constroiString.append("0");
            }
            constroiString.append(numDaLinha).append(": ").append(linha);
            if (quebraString.hasMoreTokens()) {
                constroiString.append("\n");
            }
        }
        return constroiString.toString();
    }
}
