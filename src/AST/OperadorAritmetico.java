package AST;

/**
 * Enumeracion de los operadores aritmeticos soportados por el lenguaje
 * de pseudocodigo. Se utiliza en {@link NodoOperacionBinaria} para
 * identificar la operacion matematica a realizar entre dos operandos.
 */
public enum OperadorAritmetico {
    /** Suma aritmetica (+) */
    PLUS,
    /** Resta o sustraccion (-) */
    MINUS,
    /** Multiplicacion (*) */
    MULTIPLY,
    /** Division (/) */
    DIVIDE
}
