package AST;

import java.util.List;

/**
 * Representa una estructura condicional del pseudocodigo (SI ... ENTONCES
 * ... FIN_SI). Contiene la condicion a evaluar (expresion relacional) y
 * el bloque de sentencias que se ejecuta cuando la condicion es verdadera.
 */
public class NodoSi extends NodoArbol {
    private final NodoArbol condicion;
    private final List<NodoArbol> bloqueVerdad;

    /**
     * Construye un nodo condicional.
     *
     * @param condicion    Subarbol que representa la expresion relacional
     *                     a evaluar (tipicamente un {@link NodoComparacion}).
     * @param bloqueVerdad Lista de nodos con las sentencias que se ejecutan
     *                     si la condicion se cumple (rama verdadera).
     */
    public NodoSi(NodoArbol condicion, List<NodoArbol> bloqueVerdad) {
        this.condicion = condicion;
        this.bloqueVerdad = bloqueVerdad;
    }

    /**
     * Retorna el subarbol de la condicion relacional del condicional.
     *
     * @return Nodo raiz de la expresion de condicion.
     */
    public NodoArbol getCondicion() {
        return condicion;
    }

    /**
     * Retorna la lista de sentencias que conforman el bloque verdadero
     * (ejecutado cuando la condicion es verdadera).
     * La lista retornada es inmutable; cualquier intento de modificarla
     * lanzara {@link UnsupportedOperationException}.
     *
     * @return Lista inmutable de nodos con las sentencias de la rama verdadera.
     */
    public List<NodoArbol> getBloqueVerdad() {
        return List.copyOf(bloqueVerdad);
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
