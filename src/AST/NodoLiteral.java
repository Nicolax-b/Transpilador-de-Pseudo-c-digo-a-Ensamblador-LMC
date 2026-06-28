package AST;

public class NodoLiteral extends NodoArbol {
    private final String valor;

    public NodoLiteral(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
