package AST;

/**
 * Representa una sentencia de asignacion del pseudocodigo (ej. A = B + 1).
 * Vincula un L-Value (identificador de la variable destino) con un R-Value
 * (arbol de expresion que se evalua para obtener el valor a asignar).
 */
public class NodoAsignacion extends NodoArbol {
    private final String identificador;
    private final NodoArbol operacion;

    /**
     * Construye un nodo de asignacion.
     *
     * @param identificador Nombre de la variable que recibe el valor (L-Value).
     * @param operacion     Subarbol que representa la expresion del lado
     *                      derecho (R-Value), que puede ser un nodo hoja o
     *                      una operacion binaria.
     */
    public NodoAsignacion(String identificador, NodoArbol operacion) {
        this.identificador = identificador;
        this.operacion = operacion;
    }

    /**
     * Retorna el nombre de la variable destino de la asignacion.
     *
     * @return Identificador de la variable (L-Value).
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Retorna el subarbol de la expresion del lado derecho de la asignacion.
     *
     * @return Nodo raiz de la expresion (R-Value), que puede ser un
     *         {@link NodoIdentificador}, {@link NodoLiteral} o
     *         {@link NodoOperacionBinaria}.
     */
    public NodoArbol getOperacion() {
        return operacion;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
