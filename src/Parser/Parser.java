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
    private int ultimaLineaValida;

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
        this.ultimaLineaValida = 1;
    }

    /**
     * Metodo privado avanzar(). Desplaza el puntero de prebusqueda (lookahead)
     * una posicion hacia adelante y actualiza tokenActual con el siguiente
     * token de la lista, si existe.
     */
    private void avanzar() {
        if (tokenActual != null && tokenActual.getTipo() != TokenType.EOF) {
            ultimaLineaValida = tokenActual.getLinea();
        }
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
     * @throws SyntaxException Si el tokenActual no coincide con el tipo
     * esperado.
     */
    private void consumir(TokenType tipoEsperado) {
        // Validacion estricta de la gramatica: se verifica que el token actual sea del tipo esperado
        if (tokenActual.getTipo() == tipoEsperado) {
            avanzar();
        } else if (tokenActual.getTipo() == TokenType.EOF) {
            throw new SyntaxException(
                    "Error sintactico: Se esperaba " + tipoEsperado
                    + " pero se llego al final del archivo (linea " + tokenActual.getLinea()
                    + ") sin encontrarlo. La estructura abierta en la linea "
                    + ultimaLineaValida + " podria estar incompleta."
            );
        } else {
            // Patron Fail-Fast: se lanza una excepcion inmediatamente al detectar un error sintactico
            throw new SyntaxException(
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
                if (tokenActual.getTipo() == TokenType.EOF) {
                    throw new SyntaxException(
                            "Error sintactico: Se llego al final del archivo (linea " + tokenActual.getLinea()
                            + ") pero se esperaba una sentencia. La estructura abierta en la linea "
                            + ultimaLineaValida + " podria estar incompleta."
                    );
                }
                throw new SyntaxException("[X] Error sintactico: Sentencia invalida o estructura no reconocida. Inicial (" + tokenActual.getTipo() + ") en la linea " + tokenActual.getLinea());
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
     * Valida la estructura de una instrucción de escritura, consumiendo la
     * palabra clave "ESCRIBIR" seguida de una expresion aditiva (identificador,
     * literal, o suma/resta). Se usa {@link #procesarExpresionAditiva()} en
     * lugar de {@link #procesarExpresion()} para permitir expresiones completas
     * (ESCRIBIR A+B) pero evitar comparaciones relacionales (ESCRIBIR A>B),
     * que semanticamente no tienen sentido en una instruccion de escritura.
     *
     * @return Un objeto NodoEscribir que encapsula la expresion a imprimir.
     */
    private NodoEscribir procesarEscribir() {
        consumir(TokenType.KEYWORD_WRITE);
        NodoArbol expr = procesarExpresionAditiva();
        return new NodoEscribir(expr);
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
     * Punto de entrada para el analisis de expresiones. Implementa la gramatica
     * de precedencia:
     * <pre>
     * expresionRelacional → expresionAditiva ( ('>'|'<'|'==') expresionAditiva )?
     * expresionAditiva     → hoja ( ('+'|'-') hoja )*
     * </pre>
     * Delega a {@link #procesarExpresionRelacional()} como punto de partida.
     *
     * @return NodoArbol que representa la expresion completa (un nodo hoja,
     *         un {@link NodoOperacionBinaria} o un {@link NodoComparacion}).
     */
    private NodoArbol procesarExpresion() {
        return procesarExpresionRelacional();
    }

    /**
     * Procesa una expresion relacional segun la gramatica:
     * {@code expresionRelacional → expresionAditiva ( ('>'|'<'|'==') expresionAditiva )?}
     * Evalua primero una expresion aditiva; si el token actual es un operador
     * de comparacion, consume el operador y la expresion aditiva derecha,
     * construyendo un {@link NodoComparacion}. En caso contrario, retorna
     * directamente el resultado de la expresion aditiva.
     *
     * @return Un nodo hoja, {@link NodoOperacionBinaria} o {@link NodoComparacion}.
     */
    private NodoArbol procesarExpresionRelacional() {
        NodoArbol izquierdo = procesarExpresionAditiva();

        if (tokenActual.getTipo() == TokenType.GREATER_THAN) {
            // Cuando el lexer tokenice '<' y '==', agregar las condiciones:
            // || tokenActual.getTipo() == TokenType.LESS_THAN
            // || tokenActual.getTipo() == TokenType.EQUALS
            OperadorComparacion op = OperadorComparacion.GREATER_THAN;
            avanzar();
            NodoArbol derecho = procesarExpresionAditiva();
            return new NodoComparacion(izquierdo, derecho, op);
        }

        return izquierdo;
    }

    /**
     * Procesa una expresion aditiva segun la gramatica:
     * {@code expresionAditiva → hoja ( ('+'|'-') hoja )*}
     * Obtiene la primera hoja y luego entra en un bucle: mientras el token
     * actual sea un operador aditivo, avanza, obtiene la siguiente hoja y
     * construye un {@link NodoOperacionBinaria} left-associative usando como
     * lado izquierdo el resultado acumulado de la iteracion anterior.
     * Esto permite encadenar operadores: {@code B + C + D} se parsea como
     * {@code ((B + C) + D)}.
     *
     * @return Un nodo hoja o un {@link NodoOperacionBinaria} left-associative.
     */
    private NodoArbol procesarExpresionAditiva() {
        NodoArbol acumulado = crearNodoHoja();
        avanzar();

        // Cuando el lexer tokenice '-', agregar:
        // || tokenActual.getTipo() == TokenType.MINUS
        while (tokenActual.getTipo() == TokenType.PLUS) {
            OperadorAritmetico op = OperadorAritmetico.PLUS;
            avanzar();
            NodoArbol derecho = crearNodoHoja();
            avanzar();
            acumulado = new NodoOperacionBinaria(acumulado, derecho, op);
        }

        return acumulado;
    }

    /**
     * Fábrica de nodos terminales (hojas) que decide si el token actual representa 
     * una variable (identificador) o un número literal, y construye el nodo correspondiente.
     *
     * @return Un NodoIdentificador o un NodoLiteral, dependiendo del tipo de token actual.
     * @throws SyntaxException Si el token no es ni un identificador ni un número, 
     *         indicando que la expresión está malformada.
     */
    private NodoArbol crearNodoHoja() {
        if (tokenActual.getTipo() == TokenType.IDENTIFIER) {
            return new NodoIdentificador(tokenActual.getValor());
        }
        if (tokenActual.getTipo() == TokenType.NUMBER) {
            return new NodoLiteral(tokenActual.getValor());
        }
        if (tokenActual.getTipo() == TokenType.EOF) {
            throw new SyntaxException(
                "Error sintactico: Se esperaba un identificador o numero pero se llego al final del archivo"
                + " (linea " + tokenActual.getLinea() + "). La expresion iniciada en la linea "
                + ultimaLineaValida + " esta incompleta."
            );
        }
        throw new SyntaxException(
            "Error sintactico: Se esperaba un identificador o numero, pero se encontro " +
            tokenActual.getTipo() + " en la linea " + tokenActual.getLinea()
        );
    }
}
