import Lexer.Lexer;
import Lexer.Token;
import Lexer.TokenType;
import Parser.Parser;
import AST.*;

import java.util.List;

public class TestParser {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DEL PARSER ===\n");

        probarLecturaSinIdentificadorLanzaError();
        probarSiSinEntoncesLanzaError();
        probarSiSinFinSiLanzaError();
        probarLeerCorrecto();
        probarEscribirCorrecto();
        probarAsignacionAritmetica();
        probarSiCompleto();
        probarMientrasCompleto();

        System.out.println("\n=== RESULTADOS ===");
        System.out.println("Pasaron: " + pasaron);
        System.out.println("Fallaron: " + fallaron);
        if (fallaron > 0) {
            System.exit(1);
        }
    }

    private static void probarLecturaSinIdentificadorLanzaError() {
        try {
            parsear("LEER");
            fallar("Se esperaba RuntimeException por falta de identificador tras LEER");
        } catch (RuntimeException e) {
            pasar("lecturaSinIdentificadorLanzaError: " + e.getMessage());
        }
    }

    private static void probarSiSinEntoncesLanzaError() {
        try {
            parsear("SI A > 0");
            fallar("Se esperaba RuntimeException por falta de ENTONCES tras SI");
        } catch (RuntimeException e) {
            pasar("siSinEntoncesLanzaError: " + e.getMessage());
        }
    }

    private static void probarSiSinFinSiLanzaError() {
        try {
            parsear("SI A > 0 ENTONCES LEER B");
            fallar("Se esperaba RuntimeException por falta de FIN_SI");
        } catch (RuntimeException e) {
            pasar("siSinFinSiLanzaError: " + e.getMessage());
        }
    }

    private static void probarLeerCorrecto() {
        NodoPrograma programa = parsear("LEER X");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoLeer)) {
            fallar("leerCorrecto: Se esperaba NodoLeer, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        String id = ((NodoLeer) primera).getIdentificador();
        if (!"X".equals(id)) {
            fallar("leerCorrecto: identificador esperado 'X', pero se obtuvo '" + id + "'");
            return;
        }
        pasar("leerCorrecto: NodoLeer(\"" + id + "\")");
    }

    private static void probarEscribirCorrecto() {
        NodoPrograma programa = parsear("ESCRIBIR Y");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoEscribir)) {
            fallar("escribirCorrecto: Se esperaba NodoEscribir, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        String id = ((NodoEscribir) primera).getIdentificador();
        if (!"Y".equals(id)) {
            fallar("escribirCorrecto: identificador esperado 'Y', pero se obtuvo '" + id + "'");
            return;
        }
        pasar("escribirCorrecto: NodoEscribir(\"" + id + "\")");
    }

    private static void probarAsignacionAritmetica() {
        NodoPrograma programa = parsear("A = B + 1");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoAsignacion)) {
            fallar("asignacionAritmetica: Se esperaba NodoAsignacion, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        NodoAsignacion asign = (NodoAsignacion) primera;
        if (!"A".equals(asign.getIdentificador())) {
            fallar("asignacionAritmetica: L-Value esperado 'A', pero se obtuvo '" + asign.getIdentificador() + "'");
            return;
        }
        NodoArbol expr = asign.getOperacion();
        if (!(expr instanceof NodoExpresion)) {
            fallar("asignacionAritmetica: R-Value esperado NodoExpresion, pero se obtuvo " + expr.getClass().getSimpleName());
            return;
        }
        NodoExpresion exp = (NodoExpresion) expr;
        if (exp.getOperador() != Operador.PLUS) {
            fallar("asignacionAritmetica: Operador esperado PLUS, pero se obtuvo " + exp.getOperador());
            return;
        }
        if (!(exp.getIzquierdo() instanceof NodoIdentificador)) {
            fallar("asignacionAritmetica: izquierdo esperado NodoIdentificador, pero se obtuvo " + exp.getIzquierdo().getClass().getSimpleName());
            return;
        }
        if (!(exp.getDerecho() instanceof NodoLiteral)) {
            fallar("asignacionAritmetica: derecho esperado NodoLiteral, pero se obtuvo " + exp.getDerecho().getClass().getSimpleName());
            return;
        }
        pasar("asignacionAritmetica: A = B + 1 => NodoAsignacion{id='A', expr=NodoExpresion{izq=B, op=PLUS, der=1}}");
    }

    private static void probarSiCompleto() {
        NodoPrograma programa = parsear("SI A > 0 ENTONCES LEER B FIN_SI");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoSi)) {
            fallar("siCompleto: Se esperaba NodoSi, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        NodoSi si = (NodoSi) primera;

        NodoArbol cond = si.getCondicion();
        if (!(cond instanceof NodoExpresion)) {
            fallar("siCompleto: condicion esperada NodoExpresion, pero se obtuvo " + cond.getClass().getSimpleName());
            return;
        }
        NodoExpresion condExp = (NodoExpresion) cond;
        if (condExp.getOperador() != Operador.GREATER_THAN) {
            fallar("siCompleto: Operador de condicion esperado GREATER_THAN, pero se obtuvo " + condExp.getOperador());
            return;
        }

        List<NodoArbol> bloque = si.getBloqueVerdad();
        if (bloque.size() != 1) {
            fallar("siCompleto: bloqueVerdad esperado tamanio 1, pero se obtuvo " + bloque.size());
            return;
        }
        if (!(bloque.get(0) instanceof NodoLeer)) {
            fallar("siCompleto: sentencia dentro del SI esperada NodoLeer, pero se obtuvo " + bloque.get(0).getClass().getSimpleName());
            return;
        }
        pasar("siCompleto: NodoSi{cond=NodoExpresion{op=GREATER_THAN}, bloque=[NodoLeer]}");
    }

    private static void probarMientrasCompleto() {
        NodoPrograma programa = parsear("MIENTRAS A > 0 LEER B FIN_MIENTRAS");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoMientras)) {
            fallar("mientrasCompleto: Se esperaba NodoMientras, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        NodoMientras m = (NodoMientras) primera;

        NodoArbol cond = m.getCondicion();
        if (!(cond instanceof NodoExpresion)) {
            fallar("mientrasCompleto: condicion esperada NodoExpresion, pero se obtuvo " + cond.getClass().getSimpleName());
            return;
        }

        List<NodoArbol> bloque = m.getBloqueIteracion();
        if (bloque.size() != 1) {
            fallar("mientrasCompleto: bloqueIteracion esperado tamanio 1, pero se obtuvo " + bloque.size());
            return;
        }
        if (!(bloque.get(0) instanceof NodoLeer)) {
            fallar("mientrasCompleto: sentencia dentro del MIENTRAS esperada NodoLeer, pero se obtuvo " + bloque.get(0).getClass().getSimpleName());
            return;
        }
        pasar("mientrasCompleto: NodoMientras{cond=NodoExpresion{}, bloque=[NodoLeer]}");
    }

    private static NodoPrograma parsear(String codigo) {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.extractTokens(codigo);
        Parser parser = new Parser(tokens);
        return parser.crearArbol();
    }

    private static void pasar(String mensaje) {
        System.out.println("[PASO] " + mensaje);
        pasaron++;
    }

    private static void fallar(String mensaje) {
        System.out.println("[FALLO] " + mensaje);
        fallaron++;
    }
}
