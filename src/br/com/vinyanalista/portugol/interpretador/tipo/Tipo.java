package br.com.vinyanalista.portugol.interpretador.tipo;

public class Tipo {
    protected final TipoPrimitivo tipoPrimitivo;

    public Tipo(TipoPrimitivo tipoPrimitivo) {
        this.tipoPrimitivo = tipoPrimitivo;
    }

    public TipoPrimitivo getTipoPrimitivo() {
        return tipoPrimitivo;
    }
    
    public boolean ehCompativel(Tipo outroTipo) {
		return this.equals(outroTipo)
				|| (tipoPrimitivo != null && tipoPrimitivo.equals(TipoPrimitivo.DETERMINADO_EM_TEMPO_DE_EXECUCAO))
				|| (outroTipo.tipoPrimitivo != null && outroTipo.tipoPrimitivo.equals(TipoPrimitivo.DETERMINADO_EM_TEMPO_DE_EXECUCAO));
    }
    
    public boolean ehLiteral() {
    	return tipoPrimitivo.equals(TipoPrimitivo.LITERAL) || tipoPrimitivo.equals(TipoPrimitivo.DETERMINADO_EM_TEMPO_DE_EXECUCAO);
    }
    
    public boolean ehLogico() {
    	return tipoPrimitivo.equals(TipoPrimitivo.LOGICO) || tipoPrimitivo.equals(TipoPrimitivo.DETERMINADO_EM_TEMPO_DE_EXECUCAO);
    }
    
    public boolean ehNumerico() {
        return tipoPrimitivo.equals(TipoPrimitivo.NUMERICO) || tipoPrimitivo.equals(TipoPrimitivo.DETERMINADO_EM_TEMPO_DE_EXECUCAO);
    }

    @Override
    public boolean equals(Object objeto) {
        if (!(objeto instanceof Tipo)) {
            return false;
        }
        return tipoPrimitivo.equals(((Tipo) objeto).tipoPrimitivo);
    }
    
    public Object getValorPadrao() {
    	switch (tipoPrimitivo) {
		case LITERAL:
			return "";
		case LOGICO:
			return false;
		case NUMERICO:
			return 0;
		default:
			return null;
		}
	}
    
    public boolean podeReceberEntradaDoUsuario() {
    	return !tipoPrimitivo.equals(TipoPrimitivo.DETERMINADO_EM_TEMPO_DE_EXECUCAO); // Todos os tipos primitivos podem ser obtidos do usuário
    }
    
    @Override
    public String toString() {
        return tipoPrimitivo.toString();
    }
    
}