package AST;

/**
 * Interfaz generica que define el contrato del patron Visitor para recorrer
 * el Arbol Sintactico Abstracto (AST). Quien la implemente debe proporcionar
 * un metodo {@code visitarNodo} por cada tipo de nodo concreto del AST,
 * permitiendo separar el algoritmo de recorrido de la estructura de los nodos.
 *
 * @param <T> Tipo de retorno de los metodos de visita. Cada implementacion
 *            concreta define su propio tipo (ej. {@code String} para
 *            pretty-printing, {@code Void} para generacion de codigo LMC).
 */
public interface VisitorAST<T> {

    /**
     * Visita un nodo {@link NodoPrograma}, raiz del AST que contiene la
     * secuencia completa de sentencias del pseudocodigo.
     *
     * @param nodo El nodo programa a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoPrograma nodo);

    /**
     * Visita un nodo {@link NodoLeer}, que representa una instruccion de
     * lectura de datos (LEER).
     *
     * @param nodo El nodo de lectura a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoLeer nodo);

    /**
     * Visita un nodo {@link NodoEscribir}, que representa una instruccion de
     * escritura o salida de datos (ESCRIBIR).
     *
     * @param nodo El nodo de escritura a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoEscribir nodo);

    /**
     * Visita un nodo {@link NodoAsignacion}, que representa una sentencia de
     * asignacion (IDENTIFICADOR = expresion).
     *
     * @param nodo El nodo de asignacion a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoAsignacion nodo);

    /**
     * Visita un nodo {@link NodoSi}, que representa una estructura condicional
     * (SI ... ENTONCES ... FIN_SI).
     *
     * @param nodo El nodo condicional a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoSi nodo);

    /**
     * Visita un nodo {@link NodoMientras}, que representa una estructura de
     * bucle (MIENTRAS ... FIN_MIENTRAS).
     *
     * @param nodo El nodo de bucle a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoMientras nodo);

    /**
     * Visita un nodo {@link NodoOperacionBinaria}, que representa una
     * operacion aritmetica binaria (ej. B + C, X - 1).
     *
     * @param nodo El nodo de operacion aritmetica a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoOperacionBinaria nodo);

    /**
     * Visita un nodo {@link NodoComparacion}, que representa una comparacion
     * relacional (ej. A > 0, X < Y).
     *
     * @param nodo El nodo de comparacion a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoComparacion nodo);

    /**
     * Visita un nodo {@link NodoIdentificador}, que representa una variable o
     * identificador dentro de una expresion (ej. la {@code B} en {@code B + 1}).
     *
     * @param nodo El nodo identificador a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoIdentificador nodo);

    /**
     * Visita un nodo {@link NodoLiteral}, que representa un valor numerico
     * literal dentro de una expresion (ej. el {@code 1} en {@code B + 1}).
     *
     * @param nodo El nodo literal a visitar.
     * @return El resultado de la visita, segun el tipo {@code T} del visitor.
     */
    T visitarNodo(NodoLiteral nodo);
}
