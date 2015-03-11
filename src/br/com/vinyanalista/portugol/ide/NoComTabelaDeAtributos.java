package br.com.vinyanalista.portugol.ide;


import br.com.vinyanalista.portugol.interpretador.simbolo.TabelaDeAtributos;

// import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

public class NoComTabelaDeAtributos extends DefaultMutableTreeNode {
    /* public static final ImageIcon ICONE_NO = new ImageIcon(ItemDaArvoreSemantica.class.getResource("../icons/user-online.png"));
    public static final ImageIcon ICONE_GRUPO = new ImageIcon(ItemDaArvoreSemantica.class.getResource("../icons/folder.png")); */
	private static final long serialVersionUID = 1L;
	
	private TabelaDeAtributos tabelaDeAtributos;
    
    public NoComTabelaDeAtributos(String nome) {
    	super(nome);
    }
    
    public TabelaDeAtributos getTabelaDeAtributos() {
        return tabelaDeAtributos;
    }
    
    /* public final void setIcon(QIcon qicon) {
        super.setIcon(0, qicon);
    } */
    
    public void setTabelaDeAtributos(TabelaDeAtributos tabelaDeAtributos) {
        this.tabelaDeAtributos = tabelaDeAtributos;
    }

}