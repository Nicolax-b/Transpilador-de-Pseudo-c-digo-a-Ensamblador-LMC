package AST;

public class NodoEscribir extends NodoArbol {
    private final String identificador;

    public NodoEscribir(String identificador) {
        this.identificador = identificador;
    }

    public String getIdentificador() {
        return identificador;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
