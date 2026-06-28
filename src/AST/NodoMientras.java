package AST;

import java.util.List;

public class NodoMientras extends NodoArbol {
    private final NodoArbol condicion;
    private final List<NodoArbol> bloqueIteracion;

    public NodoMientras(NodoArbol condicion, List<NodoArbol> bloqueIteracion) {
        this.condicion = condicion;
        this.bloqueIteracion = bloqueIteracion;
    }

    public NodoArbol getCondicion() {
        return condicion;
    }

    public List<NodoArbol> getBloqueIteracion() {
        return bloqueIteracion;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
