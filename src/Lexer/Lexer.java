package Lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {
    private static final Map<String, TokenType> KEYWORD = Map.of(
        "LEER", TokenType.KEYWORD_READ,
        "ESCRIBIR", TokenType.KEYWORD_WRITE,
        "SI", TokenType.KEYWORD_IF,
        "ENTONCES", TokenType.KEYWORD_THEN,
        "FIN_SI", TokenType.KEYWORD_ENDIF,
        "MIENTRAS", TokenType.KEYWORD_WHILE,
        "FIN_MIENTRAS", TokenType.KEYWORD_ENDWHILE
    );

    /**
     * Recorre el texto fuente completo y lo divide en una lista de tokens, 
     * delegando el trabajo pesado a métodos específicos según el tipo de carácter.
     *
     * @param codigoFuente El texto crudo del programa en pseudocódigo a analizar.
     * @return Una lista de objetos Token que representan el código, terminando siempre 
     *         con un token especial de fin de archivo (EOF) para avisar al parser que terminó.
     */
    public List<Token> extractTokens(String codigoFuente) {
        List<Token> tokens = new ArrayList<>();
        int indice = 0;
        int contadorLinea = 1;

        while (indice < codigoFuente.length()) {
            char caracterActual = codigoFuente.charAt(indice);

            if (caracterActual == '\n') {
                contadorLinea++;
                indice++;
            } else if (Character.isWhitespace(caracterActual)) {
                indice++;
            } else if (Character.isLetter(caracterActual)) {
                indice = consumirIdentificadorOPalabraClave(codigoFuente, indice, contadorLinea, tokens);
            } else if (Character.isDigit(caracterActual)) {
                indice = consumirNumero(codigoFuente, indice, contadorLinea, tokens);
            } else {
                indice = consumirSimbolo(caracterActual, indice, contadorLinea, tokens);
            }
        }
        
        // Añadimos un token de fin de archivo (EOF). Es un patrón de diseño común (centinela) 
        // para que el Parser sepa cuándo detenerse sin riesgo de quedarse sin tokens (IndexOutOfBounds).
        tokens.add(new Token(contadorLinea, TokenType.EOF, ""));
        return tokens;
    }

    /**
     * Lee una secuencia continua de letras y dígitos para formar una palabra completa, 
     * y decide si es una palabra reservada del lenguaje o el nombre de una variable.
     *
     * @param fuente        El texto fuente completo.
     * @param indiceInicio  La posición donde empieza la palabra.
     * @param linea         El número de línea actual (para el token).
     * @param tokens        La lista donde se guardará el token resultante.
     * @return La nueva posición del cursor (índice) justo después del final de la palabra.
     */
    private int consumirIdentificadorOPalabraClave(String fuente, int indiceInicio, int linea, List<Token> tokens) {
        // Usamos StringBuilder en lugar de concatenar Strings (ej. palabra += caracter) 
        // porque los Strings son inmutables en Java; usar StringBuilder ahorra mucha memoria y CPU en bucles.
        StringBuilder buffer = new StringBuilder();
        int indiceActual = indiceInicio;

        while (indiceActual < fuente.length() && (Character.isLetterOrDigit(fuente.charAt(indiceActual)) || fuente.charAt(indiceActual) == '_')) {
            buffer.append(fuente.charAt(indiceActual));
            indiceActual++;
        }

        String lexema = buffer.toString(); // Lexema -> Palabra completa ya extraida

        // getOrDefault es un truco elegante: busca el lexema en el mapa. Si lo encuentra, 
        // devuelve su TokenType (ej. KEYWORD_IF); si no está, asume por defecto que es un IDENTIFIER.
        TokenType tipo = KEYWORD.getOrDefault(lexema, TokenType.IDENTIFIER);
        
        tokens.add(new Token(linea, tipo, lexema));
        return indiceActual;
    }

    /**
     * Lee una secuencia continua de dígitos para formar un número completo 
     * y lo añade a la lista de tokens como un valor numérico.
     *
     * @param fuente        El texto fuente completo.
     * @param indiceInicio  La posición donde empieza el número.
     * @param linea         El número de línea actual.
     * @param tokens        La lista donde se guardará el token resultante.
     * @return La nueva posición del cursor justo después del final del número.
     */
    private int consumirNumero(String fuente, int indiceInicio, int linea, List<Token> tokens) {
        StringBuilder buffer = new StringBuilder();
        int indiceActual = indiceInicio;

        while (indiceActual < fuente.length() && Character.isDigit(fuente.charAt(indiceActual))) {
            buffer.append(fuente.charAt(indiceActual));
            indiceActual++;
        }

        tokens.add(new Token(linea, TokenType.NUMBER, buffer.toString()));
        return indiceActual;
    }

    /**
     * Identifica y clasifica símbolos matemáticos o de asignación de un solo carácter 
     * (como '=', '+', '>'), lanzando un error si encuentra un símbolo no soportado.
     *
     * @param caracterActual El símbolo exacto a procesar.
     * @param indiceActual   La posición actual del cursor.
     * @param linea          El número de línea actual.
     * @param tokens         La lista donde se guardará el token resultante.
     * @return La nueva posición del cursor (simplemente el índice actual + 1, ya que son de 1 carácter).
     */
    private int consumirSimbolo(char caracterActual, int indiceActual, int linea, List<Token> tokens) {
        switch (caracterActual) {
            case '=':
                tokens.add(new Token(linea, TokenType.ASSIGN, "="));
                break;
            case '+':
                tokens.add(new Token(linea, TokenType.PLUS, "+"));
                break;
            case '>':
                tokens.add(new Token(linea, TokenType.GREATER_THAN, ">"));
                break;
            default:
                throw new RuntimeException("Error léxico: Carácter no reconocido '" + caracterActual + "' en la línea " + linea);
        }
        return indiceActual + 1;
    }
}