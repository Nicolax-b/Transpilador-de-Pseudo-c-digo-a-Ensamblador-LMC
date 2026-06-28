package Parser;

import Lexer.Lexer;
import Lexer.Token;
import Lexer.TokenType;
import AST.*;

import java.util.List;

public class ParserTest {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DEL PARSER ===\n");

        probarLecturaSinIdentificadorLanzaError();
        probarSiSinEntoncesLanzaError();
        probarSiSinFinSiLanzaError();
        probarMientrasSinFinMientrasLanzaError();
        probarLeerCorrecto();
        probarEscribirCorrecto();
        probarAsignacionAritmetica();
        probarSiCompleto();
        probarMientrasCompleto();
        probarAsignacionConOperadoresEncadenados();
        probarSiAnidadoDentroDeMientras();
        probarMientrasAnidadoDentroDeSi();

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
            fallar("Se esperaba SyntaxException por falta de identificador tras LEER");
        } catch (SyntaxException e) {
            pasar("lecturaSinIdentificadorLanzaError: " + e.getMessage());
        }
    }

    private static void probarSiSinEntoncesLanzaError() {
        try {
            parsear("SI A > 0");
            fallar("Se esperaba SyntaxException por falta de ENTONCES tras SI");
        } catch (SyntaxException e) {
            pasar("siSinEntoncesLanzaError: " + e.getMessage());
        }
    }

    private static void probarSiSinFinSiLanzaError() {
        try {
            parsear("SI A > 0 ENTONCES LEER B");
            fallar("Se esperaba SyntaxException por falta de FIN_SI");
        } catch (SyntaxException e) {
            pasar("siSinFinSiLanzaError: " + e.getMessage());
        }
    }

    private static void probarMientrasSinFinMientrasLanzaError() {
        try {
            parsear("MIENTRAS A > 0 LEER B");
            fallar("Se esperaba SyntaxException por falta de FIN_MIENTRAS");
        } catch (SyntaxException e) {
            pasar("mientrasSinFinMientrasLanzaError: " + e.getMessage());
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
        NodoArbol expr = ((NodoEscribir) primera).getExpresion();
        if (!(expr instanceof NodoIdentificador)) {
            fallar("escribirCorrecto: getExpresion() esperaba NodoIdentificador, pero se obtuvo " + expr.getClass().getSimpleName());
            return;
        }
        String nombre = ((NodoIdentificador) expr).getNombre();
        if (!"Y".equals(nombre)) {
            fallar("escribirCorrecto: nombre de variable esperado 'Y', pero se obtuvo '" + nombre + "'");
            return;
        }
        pasar("escribirCorrecto: NodoEscribir{expresion=NodoIdentificador(\"" + nombre + "\")}");
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
        if (!(expr instanceof NodoOperacionBinaria)) {
            fallar("asignacionAritmetica: R-Value esperado NodoOperacionBinaria, pero se obtuvo " + expr.getClass().getSimpleName());
            return;
        }
        NodoOperacionBinaria exp = (NodoOperacionBinaria) expr;
        if (exp.getOperador() != OperadorAritmetico.PLUS) {
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
        pasar("asignacionAritmetica: A = B + 1 => NodoAsignacion{id='A', expr=NodoOperacionBinaria{izq=B, op=PLUS, der=1}}");
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
        if (!(cond instanceof NodoComparacion)) {
            fallar("siCompleto: condicion esperada NodoComparacion, pero se obtuvo " + cond.getClass().getSimpleName());
            return;
        }
        NodoComparacion condExp = (NodoComparacion) cond;
        if (condExp.getOperador() != OperadorComparacion.GREATER_THAN) {
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
        pasar("siCompleto: NodoSi{cond=NodoComparacion{op=GREATER_THAN}, bloque=[NodoLeer]}");
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
        if (!(cond instanceof NodoComparacion)) {
            fallar("mientrasCompleto: condicion esperada NodoComparacion, pero se obtuvo " + cond.getClass().getSimpleName());
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
        pasar("mientrasCompleto: NodoMientras{cond=NodoComparacion{}, bloque=[NodoLeer]}");
    }

    private static void probarAsignacionConOperadoresEncadenados() {
        NodoPrograma programa = parsear("A = B + C + D");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoAsignacion)) {
            fallar("operadoresEncadenados: Se esperaba NodoAsignacion, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        NodoAsignacion asign = (NodoAsignacion) primera;
        if (!"A".equals(asign.getIdentificador())) {
            fallar("operadoresEncadenados: L-Value esperado 'A', pero se obtuvo '" + asign.getIdentificador() + "'");
            return;
        }

        NodoArbol expr = asign.getOperacion();
        if (!(expr instanceof NodoOperacionBinaria)) {
            fallar("operadoresEncadenados: R-Value esperado NodoOperacionBinaria, pero se obtuvo " + expr.getClass().getSimpleName());
            return;
        }

        NodoOperacionBinaria top = (NodoOperacionBinaria) expr;
        if (top.getOperador() != OperadorAritmetico.PLUS) {
            fallar("operadoresEncadenados: Operador del nivel superior esperado PLUS, pero se obtuvo " + top.getOperador());
            return;
        }
        if (!(top.getDerecho() instanceof NodoIdentificador) || !"D".equals(((NodoIdentificador) top.getDerecho()).getNombre())) {
            fallar("operadoresEncadenados: Lado derecho del nivel superior esperado NodoIdentificador(\"D\"), pero se obtuvo " + top.getDerecho().getClass().getSimpleName());
            return;
        }

        if (!(top.getIzquierdo() instanceof NodoOperacionBinaria)) {
            fallar("operadoresEncadenados: Lado izquierdo del nivel superior esperado NodoOperacionBinaria, pero se obtuvo " + top.getIzquierdo().getClass().getSimpleName());
            return;
        }
        NodoOperacionBinaria inner = (NodoOperacionBinaria) top.getIzquierdo();
        if (inner.getOperador() != OperadorAritmetico.PLUS) {
            fallar("operadoresEncadenados: Operador del nivel interno esperado PLUS, pero se obtuvo " + inner.getOperador());
            return;
        }
        if (!(inner.getIzquierdo() instanceof NodoIdentificador) || !"B".equals(((NodoIdentificador) inner.getIzquierdo()).getNombre())) {
            fallar("operadoresEncadenados: Lado izquierdo del nivel interno esperado NodoIdentificador(\"B\"), pero se obtuvo " + inner.getIzquierdo().getClass().getSimpleName());
            return;
        }
        if (!(inner.getDerecho() instanceof NodoIdentificador) || !"C".equals(((NodoIdentificador) inner.getDerecho()).getNombre())) {
            fallar("operadoresEncadenados: Lado derecho del nivel interno esperado NodoIdentificador(\"C\"), pero se obtuvo " + inner.getDerecho().getClass().getSimpleName());
            return;
        }

        pasar("operadoresEncadenados: A = B + C + D => ((B + C) + D)");
    }

    private static void probarSiAnidadoDentroDeMientras() {
        NodoPrograma programa = parsear("MIENTRAS A > 0 SI A > 10 ENTONCES ESCRIBIR A FIN_SI FIN_MIENTRAS");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoMientras)) {
            fallar("siAnidadoDentroDeMientras: Se esperaba NodoMientras en la raiz, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        NodoMientras m = (NodoMientras) primera;

        if (!(m.getCondicion() instanceof NodoComparacion)) {
            fallar("siAnidadoDentroDeMientras: condicion del MIENTRAS esperada NodoComparacion, pero se obtuvo " + m.getCondicion().getClass().getSimpleName());
            return;
        }

        List<NodoArbol> bloqueIter = m.getBloqueIteracion();
        if (bloqueIter.size() != 1) {
            fallar("siAnidadoDentroDeMientras: bloqueIteracion esperado tamanio 1, pero se obtuvo " + bloqueIter.size());
            return;
        }
        if (!(bloqueIter.get(0) instanceof NodoSi)) {
            fallar("siAnidadoDentroDeMientras: sentencia dentro del MIENTRAS esperada NodoSi, pero se obtuvo " + bloqueIter.get(0).getClass().getSimpleName());
            return;
        }
        NodoSi siInterno = (NodoSi) bloqueIter.get(0);

        if (!(siInterno.getCondicion() instanceof NodoComparacion)) {
            fallar("siAnidadoDentroDeMientras: condicion del SI interno esperada NodoComparacion, pero se obtuvo " + siInterno.getCondicion().getClass().getSimpleName());
            return;
        }

        List<NodoArbol> bloqueVerdad = siInterno.getBloqueVerdad();
        if (bloqueVerdad.size() != 1) {
            fallar("siAnidadoDentroDeMientras: bloqueVerdad del SI interno esperado tamanio 1, pero se obtuvo " + bloqueVerdad.size());
            return;
        }
        if (!(bloqueVerdad.get(0) instanceof NodoEscribir)) {
            fallar("siAnidadoDentroDeMientras: sentencia dentro del SI interno esperada NodoEscribir, pero se obtuvo " + bloqueVerdad.get(0).getClass().getSimpleName());
            return;
        }
        NodoEscribir escrib = (NodoEscribir) bloqueVerdad.get(0);
        NodoArbol exprEscrib = escrib.getExpresion();
        if (!(exprEscrib instanceof NodoIdentificador)) {
            fallar("siAnidadoDentroDeMientras: getExpresion() esperaba NodoIdentificador, pero se obtuvo " + exprEscrib.getClass().getSimpleName());
            return;
        }
        String nombreVar = ((NodoIdentificador) exprEscrib).getNombre();
        if (!"A".equals(nombreVar)) {
            fallar("siAnidadoDentroDeMientras: NodoEscribir esperado con variable 'A', pero se obtuvo '" + nombreVar + "'");
            return;
        }

        pasar("siAnidadoDentroDeMientras: MIENTRAS{ SI{ ESCRIBIR A } } estructura correcta");
    }

    private static void probarMientrasAnidadoDentroDeSi() {
        NodoPrograma programa = parsear("SI A > 0 ENTONCES MIENTRAS A > 10 LEER X FIN_MIENTRAS FIN_SI");
        NodoArbol primera = programa.getSentencias().get(0);

        if (!(primera instanceof NodoSi)) {
            fallar("mientrasAnidadoDentroDeSi: Se esperaba NodoSi en la raiz, pero se obtuvo " + primera.getClass().getSimpleName());
            return;
        }
        NodoSi si = (NodoSi) primera;

        if (!(si.getCondicion() instanceof NodoComparacion)) {
            fallar("mientrasAnidadoDentroDeSi: condicion del SI esperada NodoComparacion, pero se obtuvo " + si.getCondicion().getClass().getSimpleName());
            return;
        }

        List<NodoArbol> bloqueVerdad = si.getBloqueVerdad();
        if (bloqueVerdad.size() != 1) {
            fallar("mientrasAnidadoDentroDeSi: bloqueVerdad esperado tamanio 1, pero se obtuvo " + bloqueVerdad.size());
            return;
        }
        if (!(bloqueVerdad.get(0) instanceof NodoMientras)) {
            fallar("mientrasAnidadoDentroDeSi: sentencia dentro del SI esperada NodoMientras, pero se obtuvo " + bloqueVerdad.get(0).getClass().getSimpleName());
            return;
        }
        NodoMientras mInterno = (NodoMientras) bloqueVerdad.get(0);

        if (!(mInterno.getCondicion() instanceof NodoComparacion)) {
            fallar("mientrasAnidadoDentroDeSi: condicion del MIENTRAS interno esperada NodoComparacion, pero se obtuvo " + mInterno.getCondicion().getClass().getSimpleName());
            return;
        }

        List<NodoArbol> bloqueIter = mInterno.getBloqueIteracion();
        if (bloqueIter.size() != 1) {
            fallar("mientrasAnidadoDentroDeSi: bloqueIteracion del MIENTRAS interno esperado tamanio 1, pero se obtuvo " + bloqueIter.size());
            return;
        }
        if (!(bloqueIter.get(0) instanceof NodoLeer)) {
            fallar("mientrasAnidadoDentroDeSi: sentencia dentro del MIENTRAS interno esperada NodoLeer, pero se obtuvo " + bloqueIter.get(0).getClass().getSimpleName());
            return;
        }
        NodoLeer leer = (NodoLeer) bloqueIter.get(0);
        if (!"X".equals(leer.getIdentificador())) {
            fallar("mientrasAnidadoDentroDeSi: NodoLeer esperado con identificador 'X', pero se obtuvo '" + leer.getIdentificador() + "'");
            return;
        }

        pasar("mientrasAnidadoDentroDeSi: SI{ MIENTRAS{ LEER X } } estructura correcta");
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
