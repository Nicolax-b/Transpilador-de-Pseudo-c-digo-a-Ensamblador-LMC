package AST;

public class NodoExpresion extends NodoArbol {
    private final NodoArbol izquierdo;
    private final NodoArbol derecho;
    private final Operador operador;

    public NodoExpresion(NodoArbol izquierdo, NodoArbol derecho, Operador operador) {
        this.izquierdo = izquierdo;
        this.derecho = derecho;
        this.operador = operador;
    }

    public NodoArbol getIzquierdo() {
        return izquierdo;
    }

    public NodoArbol getDerecho() {
        return derecho;
    }

    public Operador getOperador() {
        return operador;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
