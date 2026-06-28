package AST;

/**
 * Nodo hoja del AST que representa una variable o identificador dentro de
 * una expresion (ej. la {@code B} en {@code B + 1}, o la {@code X} en una
 * asignacion {@code A = X}). Contiene unicamente el nombre del identificador
 * como cadena de texto.
 */
public class NodoIdentificador extends NodoArbol {
    private final String nombre;

    /**
     * Construye un nodo identificador.
     *
     * @param nombre El nombre del identificador o variable.
     */
    public NodoIdentificador(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna el nombre del identificador.
     *
     * @return Nombre de la variable o identificador.
     */
    public String getNombre() {
        return nombre;
    }

    @Override
    public <T> T aceptar(VisitorAST<T> visitor) {
        return visitor.visitarNodo(this);
    }
}
