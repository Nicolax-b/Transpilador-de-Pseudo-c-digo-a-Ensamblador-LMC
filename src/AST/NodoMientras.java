package AST;

import java.util.List;

/**
 * Representa una estructura de bucle del pseudocodigo (MIENTRAS ...
 * FIN_MIENTRAS). Contiene la condicion de permanencia y el bloque de
 * sentencias que se ejecuta iterativamente mientras la condicion sea
 * verdadera.
 */
public class NodoMientras extends NodoArbol {
    private final NodoArbol condicion;
    private final List<NodoArbol> bloqueIteracion;

    /**
     * Construye un nodo de bucle.
     *
     * @param condicion       Subarbol que representa la expresion relacional
     *                        que controla la iteracion (tipicamente un
     *                        {@link NodoComparacion}).
     * @param bloqueIteracion Lista de nodos con las sentencias que se
     *                        ejecutan en cada iteracion del bucle.
     */
    public NodoMientras(NodoArbol condicion, List<NodoArbol> bloqueIteracion) {
        this.condicion = condicion;
        this.bloqueIteracion = bloqueIteracion;
    }

    /**
     * Retorna el subarbol de la condicion de iteracion del bucle.
     *
     * @return Nodo raiz de la expresion de condicion.
     */
    public NodoArbol getCondicion() {
        return condicion;
    }

    /**
     * Retorna la lista de sentencias que conforman el cuerpo del bucle.
     * La lista retornada es inmutable; cualquier intento de modificarla
     * lanzara {@link UnsupportedOperationException}.
     *
     * @return Lista inmutable de nodos con las sentencias del bloque de iteracion.
     */
    public List<NodoArbol> getBloqueIteracion() {
        return List.copyOf(bloqueIteracion);
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
