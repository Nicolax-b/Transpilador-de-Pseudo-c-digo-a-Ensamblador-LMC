package AST;

import java.util.List;

public class NodoPrograma extends NodoArbol {
    private final List<NodoArbol> sentencias;

    public NodoPrograma(List<NodoArbol> sentencias) {
        this.sentencias = sentencias;
    }

    public List<NodoArbol> getSentencias() {
        return sentencias;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
