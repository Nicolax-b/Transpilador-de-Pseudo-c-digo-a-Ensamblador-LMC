package Lexer;

/**
 * Excepcion lanzada cuando se detecta un error durante el analisis lexico.
 * Representa problemas como caracteres no reconocidos, formatos numericos
 * invalidos o cualquier violacion de las reglas de tokenizacion del lenguaje.
 */
public class LexicalException extends RuntimeException {
    public LexicalException(String mensaje) {
        super(mensaje);
    }
}
