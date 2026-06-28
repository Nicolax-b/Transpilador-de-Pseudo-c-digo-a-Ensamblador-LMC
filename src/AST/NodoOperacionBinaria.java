package AST;

/**
 * Representa una operacion aritmetica binaria dentro de una expresion
 * (ej. {@code B + C}, {@code X - 1}). Contiene los dos operandos
 * (subarboles izquierdo y derecho) y el operador aritmetico que los
 * relaciona, definido mediante {@link OperadorAritmetico}.
 */
public class NodoOperacionBinaria extends NodoArbol {
    private final NodoArbol izquierdo;
    private final NodoArbol derecho;
    private final OperadorAritmetico operador;

    /**
     * Construye un nodo de operacion aritmetica binaria.
     *
     * @param izquierdo Subarbol del operando izquierdo (p. ej. un
     *                  {@link NodoIdentificador} o {@link NodoLiteral}).
     * @param derecho   Subarbol del operando derecho.
     * @param operador  Operador aritmetico que se aplica a los operandos
     *                  (PLUS, MINUS, MULTIPLY, DIVIDE).
     */
    public NodoOperacionBinaria(NodoArbol izquierdo, NodoArbol derecho, OperadorAritmetico operador) {
        this.izquierdo = izquierdo;
        this.derecho = derecho;
        this.operador = operador;
    }

    /**
     * Retorna el subarbol del operando izquierdo de la operacion.
     *
     * @return Nodo raiz del operando izquierdo.
     */
    public NodoArbol getIzquierdo() {
        return izquierdo;
    }

    /**
     * Retorna el subarbol del operando derecho de la operacion.
     *
     * @return Nodo raiz del operando derecho.
     */
    public NodoArbol getDerecho() {
        return derecho;
    }

    /**
     * Retorna el operador aritmetico de la operacion binaria.
     *
     * @return Operador aritmetico (PLUS, MINUS, MULTIPLY o DIVIDE).
     */
    public OperadorAritmetico getOperador() {
        return operador;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
