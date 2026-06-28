package AST;

/**
 * Representa una instruccion de lectura de datos (LEER) del pseudocodigo.
 * Encapsula el nombre de la variable en la cual se almacenara el valor
 * ingresado por el usuario durante la ejecucion del programa.
 */
public class NodoLeer extends NodoArbol {
    private final String identificador;

    /**
     * Construye un nodo de lectura.
     *
     * @param identificador Nombre de la variable que recibira el dato de
     *                      entrada.
     */
    public NodoLeer(String identificador) {
        this.identificador = identificador;
    }

    /**
     * Retorna el nombre de la variable asociada a la instruccion de lectura.
     *
     * @return Identificador de la variable destino.
     */
    public String getIdentificador() {
        return identificador;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
