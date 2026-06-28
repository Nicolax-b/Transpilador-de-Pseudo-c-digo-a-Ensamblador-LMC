package AST;

public class NodoAsignacion extends NodoArbol {
    private final String identificador;
    private final NodoArbol operacion;

    public NodoAsignacion(String identificador, NodoArbol operacion) {
        this.identificador = identificador;
        this.operacion = operacion;
    }

    public String getIdentificador() {
        return identificador;
    }

    public NodoArbol getOperacion() {
        return operacion;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
