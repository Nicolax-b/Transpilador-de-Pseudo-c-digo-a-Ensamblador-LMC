package AST;

/**
 * Enumeracion de los operadores relacionales o de comparacion soportados
 * por el lenguaje de pseudocodigo. Se utiliza en {@link NodoComparacion}
 * para identificar la relacion a evaluar entre dos operandos.
 */
public enum OperadorComparacion {
    /** Mayor que (>) */
    GREATER_THAN,
    /** Menor que (<) */
    LESS_THAN,
    /** Igual a (=) */
    EQUALS
}
