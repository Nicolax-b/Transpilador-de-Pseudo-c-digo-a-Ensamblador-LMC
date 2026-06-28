package Parser;

import AST.*;
import Lexer.Token;
import Lexer.TokenType;
import java.util.ArrayList;
import java.util.List;


/**
 * Consume la lista de tokens generada por el Lexer y valida que su secuencia
 * respete la gramática del pseudocódigo, sirviendo como el motor principal para
 * la construcción posterior del Árbol Sintáctico Abstracto (AST).
 */
public class Parser {

    private List<Token> tokens;
    private int posicionActual;
    private Token tokenActual;

    /**
     * Prepara el analizador para empezar a leer el código, cargando la lista de
     * tokens y posicionando la "cabeza lectora" en el primer token disponible.
     *
     * @param tokens La secuencia de tokens validados y extraídos previamente
     * por el Lexer.
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.posicionActual = 0;
        // Inicializa el puntero de prebusqueda (lookahead) con el primer token
        this.tokenActual = tokens.isEmpty() ? null : tokens.get(0);
    }

    /**
     * Metodo privado avanzar(). Desplaza el puntero de prebusqueda (lookahead)
     * una posicion hacia adelante y actualiza tokenActual con el siguiente
     * token de la lista, si existe.
     */
    private void avanzar() {
        posicionActual++;
        // Actualiza el puntero de prebusqueda al siguiente token si esta disponible
        if (posicionActual < tokens.size()) {
            tokenActual = tokens.get(posicionActual);
        }
    }

    /**
     * Metodo privado consumir(TokenType tipoEsperado). Realiza una validacion
     * estricta de la gramatica siguiendo el patron Fail-Fast: verifica que el
     * tokenActual coincida con el tipo esperado. Si coincide, avanza al
     * siguiente token. Si no coincide, lanza una excepcion con informacion
     * detallada del error, incluyendo el tipo esperado, el tipo encontrado y la
     * linea donde ocurrio.
     *
     * @param tipoEsperado El tipo de token que se espera segun la gramatica.
     * @throws RuntimeException Si el tokenActual no coincide con el tipo
     * esperado.
     */
    private void consumir(TokenType tipoEsperado) {
        // Validacion estricta de la gramatica: se verifica que el token actual sea del tipo esperado
        if (tokenActual.getTipo() == tipoEsperado) {
            avanzar();
        } else {
            // Patron Fail-Fast: se lanza una excepcion inmediatamente al detectar un error sintactico
            throw new RuntimeException(
                    "Error sintactico: Se esperaba " + tipoEsperado
                    + " pero se encontro " + tokenActual.getTipo()
                    + " en la linea " + tokenActual.getLinea()
            );
        }
    }

    /**
     * Punto de entrada principal para la construcción del AST; recorre todas las sentencias 
     * del programa, las procesa jerárquicamente y las agrupa bajo un nodo raíz.
     *
     * @return Un objeto NodoPrograma que actúa como la raíz del Árbol Sintáctico Abstracto, 
     *         conteniendo la lista completa de sentencias del pseudocódigo.
     */
    public NodoPrograma crearArbol() {
        List<NodoArbol> sentencias = new ArrayList<>();
        while (tokenActual.getTipo() != TokenType.EOF) {
            sentencias.add(evaluarSentencia());
        }
        return new NodoPrograma(sentencias);
    }

    /**
     * Actúa como un enrutador o multiplexor: inspecciona el token actual y delega 
     * el trabajo al método específico que sabe cómo construir el nodo correspondiente.
     *
     * @return El nodo del AST (NodoArbol) que representa la sentencia procesada, 
     *         listo para ser añadido a la lista de sentencias o a un bloque interno.
     */
    private NodoArbol evaluarSentencia() {
        switch (tokenActual.getTipo()) {
            case KEYWORD_READ:
                return procesarLeer();
            case KEYWORD_WRITE:
                return procesarEscribir();
            case IDENTIFIER:
                return procesarAsignacion();
            case KEYWORD_IF:
                return procesarSi();
            case KEYWORD_WHILE:
                return procesarMientras();
            default:
                throw new RuntimeException("[X] Error sintactico: Sentencia invalida o estructura no reconocida. Inicial (" + tokenActual.getTipo() + ") en la linea " + tokenActual.getLinea());
        }
    }

    /**
     * Valida la estructura de una instrucción de lectura, asegurando que la palabra 
     * clave "LEER" sea seguida estrictamente por el nombre de una variable, y construye su nodo.
     *
     * @return Un objeto NodoLeer que encapsula el nombre de la variable a leer.
     */
    private NodoLeer procesarLeer() {
        consumir(TokenType.KEYWORD_READ);
        String nombreVariable = tokenActual.getValor();
        consumir(TokenType.IDENTIFIER);
        return new NodoLeer(nombreVariable);
    }

    /**
     * Valida la estructura de una instrucción de escritura, exigiendo que "ESCRIBIR" 
     * vaya seguido de un identificador válido, y construye su nodo correspondiente.
     *
     * @return Un objeto NodoEscribir que encapsula el nombre de la variable a imprimir.
     */
    private NodoEscribir procesarEscribir() {
        consumir(TokenType.KEYWORD_WRITE);
        String nombreVariable = tokenActual.getValor();
        consumir(TokenType.IDENTIFIER);
        return new NodoEscribir(nombreVariable);
    }

    /**
     * Procesa una asignación de variables (ej. A = B + 1), consumiendo el identificador, 
     * el signo igual y delegando la validación de la parte matemática a procesarExpresion().
     */
    private NodoAsignacion procesarAsignacion() {
        String identificador = tokenActual.getValor();
        consumir(TokenType.IDENTIFIER);
        consumir(TokenType.ASSIGN);

        NodoArbol expresion = procesarExpresion();
        return new NodoAsignacion(identificador, expresion);
    }

    /**
     * Analiza la estructura completa de un bloque condicional (SI...ENTONCES...FIN_SI), 
     * evaluando la condición inicial y procesando recursivamente todas las sentencias internas.
     *
     * @return Un objeto NodoSi que contiene la condición a evaluar y la lista de sentencias 
     *         que se ejecutarán si la condición es verdadera.
     */
    private NodoSi procesarSi() {
        consumir(TokenType.KEYWORD_IF);
        NodoArbol condicion = procesarExpresion(); // Evalúa la condición relacional
        consumir(TokenType.KEYWORD_THEN);

        List<NodoArbol> bloqueVerdad = new ArrayList<>();
        // Bucle de análisis recursivo: seguimos evaluando sentencias internas 
        // hasta que nos topemos con la palabra de cierre "FIN_SI" o el fin del archivo.
        // Esto permite anidar múltiples sentencias dentro del SI.

        while (tokenActual.getTipo() != TokenType.KEYWORD_ENDIF && tokenActual.getTipo() != TokenType.EOF) {
            bloqueVerdad.add(evaluarSentencia());
        }

        consumir(TokenType.KEYWORD_ENDIF);
        return new NodoSi(condicion, bloqueVerdad);
    }

    /**
     * Analiza la estructura completa de un bucle (MIENTRAS...FIN_MIENTRAS), 
     * evaluando la condición de permanencia y procesando recursivamente el cuerpo del ciclo.
     *
     * @return Un objeto NodoMientras que contiene la condición de iteración y la lista 
     *         de sentencias que componen el cuerpo del bucle.
     */
    private NodoMientras procesarMientras() {
        consumir(TokenType.KEYWORD_WHILE);
        NodoArbol condicion = procesarExpresion(); // Evalúa la condición de parada

        List<NodoArbol> bloqueIteracion = new ArrayList<>();

        // Análisis recursivo del cuerpo del bucle
        while (tokenActual.getTipo() != TokenType.KEYWORD_ENDWHILE && tokenActual.getTipo() != TokenType.EOF) {
            bloqueIteracion.add(evaluarSentencia());
        }

        consumir(TokenType.KEYWORD_ENDWHILE);
        return new NodoMientras(condicion, bloqueIteracion);
    }

    /**
     * Valida expresiones matemáticas o relacionales (ej. "B + C" o "A > 0"), 
     * asegurando que tengan la estructura correcta y construyendo el sub-árbol de la operación.
     *
     * @return Un NodoArbol que representa la expresión. Si hay operadores, retorna un árbol 
     *         binario (NodoExpresion con hijos); si es un valor aislado, retorna un nodo envoltorio 
     *         con hijos nulos para mantener la uniformidad de la estructura.
     */
    private NodoArbol procesarExpresion() {
        NodoArbol izquierdo = crearNodoHoja();
        avanzar();

        if (tokenActual.getTipo() == TokenType.PLUS || tokenActual.getTipo() == TokenType.GREATER_THAN) {
            Operador op = mapearOperador(tokenActual.getTipo());
            avanzar();
            NodoArbol derecho = crearNodoHoja();
            avanzar();
            return new NodoExpresion(izquierdo, derecho, op);
        }

        return new NodoExpresion(izquierdo, null, null);
    }

    /**
     * Fábrica de nodos terminales (hojas) que decide si el token actual representa 
     * una variable (identificador) o un número literal, y construye el nodo correspondiente.
     *
     * @return Un NodoIdentificador o un NodoLiteral, dependiendo del tipo de token actual.
     * @throws RuntimeException Si el token no es ni un identificador ni un número, 
     *         indicando que la expresión está malformada.
     */
    private NodoArbol crearNodoHoja() {
        if (tokenActual.getTipo() == TokenType.IDENTIFIER) {
            return new NodoIdentificador(tokenActual.getValor());
        }
        if (tokenActual.getTipo() == TokenType.NUMBER) {
            return new NodoLiteral(tokenActual.getValor());
        }
        throw new RuntimeException(
            "Error sintactico: Se esperaba un identificador o numero, pero se encontro " +
            tokenActual.getTipo() + " en la linea " + tokenActual.getLinea()
        );
    }

     /**
     * Traduce el tipo de token del operador (del Lexer) a su enumerador interno 
     * del AST, actuando como un puente entre las dos capas del sistema.
     *
     * @param tipo El TokenType del operador encontrado (PLUS o GREATER_THAN).
     * @return El valor del enumerador Operador correspondiente para ser guardado en el NodoExpresion.
     */
    private Operador mapearOperador(TokenType tipo) {
        if (tipo == TokenType.PLUS) return Operador.PLUS;
        if (tipo == TokenType.GREATER_THAN) return Operador.GREATER_THAN;
        throw new RuntimeException(
            "Error sintactico: Operador no soportado " + tipo +
            " en la linea " + tokenActual.getLinea()
        );
    }
}
