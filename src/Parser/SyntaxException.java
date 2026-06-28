package Parser;

/**
 * Excepcion lanzada cuando se detecta un error durante el analisis sintactico.
 * Representa problemas como tokens inesperados, estructuras gramaticales no
 * cumplidas, o cualquier violacion de la gramatica del pseudocodigo.
 */
public class SyntaxException extends RuntimeException {
    public SyntaxException(String mensaje) {
        super(mensaje);
    }
}
