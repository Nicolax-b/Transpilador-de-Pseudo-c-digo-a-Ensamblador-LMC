package AST;

/**
 * Clase abstracta base de todo el Arbol Sintactico Abstracto (AST).
 * Cada subclase concreta representa una construccion gramatical del
 * pseudocodigo (lectura, escritura, asignacion, condicional, bucle, etc.)
 * y debe implementar {@link #aceptar(VisitorAST)} para habilitar el
 * recorrido del arbol mediante el patron Visitor.
 */
public abstract class NodoArbol {

    /**
     * Constructor por defecto para las subclases.
     */
    protected NodoArbol() {
    }

    /**
     * Metodo de enrutamiento dinamico del patron Visitor (Double Dispatch).
     * Cada subclase concreta invoca al metodo {@code visitarNodo(this)}
     * sobrecargado en el visitor, permitiendo que el algoritmo externo
     * procese el nodo sin necesidad de usar {@code instanceof}.
     *
     * @param <T>     Tipo de retorno definido por el visitor concreto.
     * @param visitor Implementacion del visitor que procesara este nodo.
     * @return El resultado de la visita, cuyo tipo depende del visitor
     *         (por ejemplo, {@code String} para pretty-printing,
     *         {@code Void} para generacion de codigo, etc.).
     */
    public abstract <T> T aceptar(VisitorAST<T> visitor);
}
