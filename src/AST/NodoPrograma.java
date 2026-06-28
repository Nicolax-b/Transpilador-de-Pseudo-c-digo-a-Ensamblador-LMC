package AST;

import java.util.List;

/**
 * Nodo raiz del Arbol Sintactico Abstracto (AST). Encapsula la secuencia
 * completa de sentencias que conforman el programa en pseudocodigo.
 * Actua como contenedor de la lista de nodos hijos, cada uno representando
 * una instruccion del codigo fuente.
 */
public class NodoPrograma extends NodoArbol {
    private final List<NodoArbol> sentencias;

    /**
     * Construye un nodo programa con la lista de sentencias del codigo.
     *
     * @param sentencias Lista de nodos que representan cada sentencia del
     *                   programa en el orden en que fueron declaradas.
     */
    public NodoPrograma(List<NodoArbol> sentencias) {
        this.sentencias = sentencias;
    }

    /**
     * Retorna la lista completa de sentencias del programa.
     * La lista retornada es inmutable; cualquier intento de modificarla
     * lanzara {@link UnsupportedOperationException}.
     *
     * @return Lista inmutable de nodos {@link NodoArbol}, uno por cada
     *         instruccion del codigo fuente.
     */
    public List<NodoArbol> getSentencias() {
        return List.copyOf(sentencias);
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
