package AST;

import java.util.List;

public class NodoSi extends NodoArbol {
    private final NodoArbol condicion;
    private final List<NodoArbol> bloqueVerdad;

    public NodoSi(NodoArbol condicion, List<NodoArbol> bloqueVerdad) {
        this.condicion = condicion;
        this.bloqueVerdad = bloqueVerdad;
    }

    public NodoArbol getCondicion() {
        return condicion;
    }

    public List<NodoArbol> getBloqueVerdad() {
        return bloqueVerdad;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
