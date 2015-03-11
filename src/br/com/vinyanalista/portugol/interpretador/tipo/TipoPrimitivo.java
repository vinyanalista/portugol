package br.com.vinyanalista.portugol.interpretador.tipo;

public enum TipoPrimitivo {
    NUMERICO {

        @Override
        public String toString() {
            return "NUMERICO";
        }
        
    }, LITERAL {

        @Override
        public String toString() {
            return "LITERAL";
        }
        
    }, LOGICO {

        @Override
        public String toString() {
            return "LOGICO";
        }
    }, DETERMINADO_EM_TEMPO_DE_EXECUCAO {

        @Override
        public String toString() {
            return "determinado em tempo de execução";
        }
    };
}
