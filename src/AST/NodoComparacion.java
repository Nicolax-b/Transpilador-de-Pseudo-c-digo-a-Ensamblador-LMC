package AST;

/**
 * Representa una comparacion relacional dentro de una expresion
 * (ej. {@code A > 0}, {@code X < Y}). Contiene los dos operandos
 * (subarboles izquierdo y derecho) y el operador de comparacion que los
 * relaciona, definido mediante {@link OperadorComparacion}.
 */
public class NodoComparacion extends NodoArbol {
    private final NodoArbol izquierdo;
    private final NodoArbol derecho;
    private final OperadorComparacion operador;

    /**
     * Construye un nodo de comparacion relacional.
     *
     * @param izquierdo Subarbol del operando izquierdo (p. ej. un
     *                  {@link NodoIdentificador}).
     * @param derecho   Subarbol del operando derecho (p. ej. un
     *                  {@link NodoLiteral}).
     * @param operador  Operador de comparacion que se aplica a los operandos
     *                  (GREATER_THAN, LESS_THAN, EQUALS).
     */
    public NodoComparacion(NodoArbol izquierdo, NodoArbol derecho, OperadorComparacion operador) {
        this.izquierdo = izquierdo;
        this.derecho = derecho;
        this.operador = operador;
    }

    /**
     * Retorna el subarbol del operando izquierdo de la comparacion.
     *
     * @return Nodo raiz del operando izquierdo.
     */
    public NodoArbol getIzquierdo() {
        return izquierdo;
    }

    /**
     * Retorna el subarbol del operando derecho de la comparacion.
     *
     * @return Nodo raiz del operando derecho.
     */
    public NodoArbol getDerecho() {
        return derecho;
    }

    /**
     * Retorna el operador de comparacion.
     *
     * @return Operador de comparacion (GREATER_THAN, LESS_THAN, EQUALS).
     */
    public OperadorComparacion getOperador() {
        return operador;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
