package AST;

/**
 * Nodo hoja del AST que representa un valor numerico literal dentro de una
 * expresion (ej. el {@code 1} en {@code B + 1}, o el {@code 0} en
 * {@code A > 0}). Contiene el valor como cadena de texto para preservar
 * su representacion original del codigo fuente.
 */
public class NodoLiteral extends NodoArbol {
    private final String valor;

    /**
     * Construye un nodo literal numerico.
     *
     * @param valor La representacion en texto del numero literal.
     */
    public NodoLiteral(String valor) {
        this.valor = valor;
    }

    /**
     * Retorna el valor del literal numerico.
     *
     * @return Cadena con el valor numerico en su representacion original.
     */
    public String getValor() {
        return valor;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
