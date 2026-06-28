package AST;

/**
 * Representa una instruccion de escritura o salida de datos (ESCRIBIR) del
 * pseudocodigo. Encapsula la expresion cuyo valor se mostrara al usuario
 * durante la ejecucion del programa, que puede ser un identificador,
 * un literal numerico o una expresion aritmetica.
 */
public class NodoEscribir extends NodoArbol {
    private final NodoArbol expresion;

    /**
     * Construye un nodo de escritura.
     *
     * @param expresion Nodo del AST que representa el valor a imprimir
     *                  (puede ser un NodoIdentificador, NodoLiteral o
     *                  NodoOperacionBinaria).
     */
    public NodoEscribir(NodoArbol expresion) {
        this.expresion = expresion;
    }

    /**
     * Retorna la expresion cuyo valor se imprimira como salida.
     *
     * @return Nodo del AST que representa el valor a escribir.
     */
    public NodoArbol getExpresion() {
        return expresion;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
