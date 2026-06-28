package Lexer;

import java.util.List;

public class LexerTest {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DEL LEXER ===\n");

        probarPalabraClave("LEER", TokenType.KEYWORD_READ);
        probarPalabraClave("ESCRIBIR", TokenType.KEYWORD_WRITE);
        probarPalabraClave("SI", TokenType.KEYWORD_IF);
        probarPalabraClave("ENTONCES", TokenType.KEYWORD_THEN);
        probarPalabraClave("FIN_SI", TokenType.KEYWORD_ENDIF);
        probarPalabraClave("MIENTRAS", TokenType.KEYWORD_WHILE);
        probarPalabraClave("FIN_MIENTRAS", TokenType.KEYWORD_ENDWHILE);
        probarIdentificador();
        probarNumero();
        probarSimbolo('=', TokenType.ASSIGN);
        probarSimbolo('+', TokenType.PLUS);
        probarSimbolo('>', TokenType.GREATER_THAN);
        probarIgnorarEspaciosYLineas();
        probarTerminaConEof();
        probarCaracterNoReconocido();

        System.out.println("\n=== RESULTADOS ===");
        System.out.println("Pasaron: " + pasaron);
        System.out.println("Fallaron: " + fallaron);
        if (fallaron > 0) {
            System.exit(1);
        }
    }

    private static void probarPalabraClave(String lexema, TokenType esperado) {
        List<Token> tokens = new Lexer().extractTokens(lexema);
        if (tokens.size() < 2 || tokens.get(0).getTipo() != esperado) {
            fallar("palabraClave(" + lexema + "): Se esperaba " + esperado + " pero se obtuvo " + (tokens.isEmpty() ? "vacio" : tokens.get(0).getTipo()));
            return;
        }
        if (!lexema.equals(tokens.get(0).getValor())) {
            fallar("palabraClave(" + lexema + "): Valor esperado '" + lexema + "' pero se obtuvo '" + tokens.get(0).getValor() + "'");
            return;
        }
        pasar("palabraClave(" + lexema + "): " + esperado + " correcto");
    }

    private static void probarIdentificador() {
        List<Token> tokens = new Lexer().extractTokens("variableX");
        if (tokens.size() < 2 || tokens.get(0).getTipo() != TokenType.IDENTIFIER) {
            fallar("identificador: Se esperaba IDENTIFIER pero se obtuvo " + (tokens.isEmpty() ? "vacio" : tokens.get(0).getTipo()));
            return;
        }
        if (!"variableX".equals(tokens.get(0).getValor())) {
            fallar("identificador: Valor esperado 'variableX' pero se obtuvo '" + tokens.get(0).getValor() + "'");
            return;
        }
        pasar("identificador: IDENTIFIER(\"variableX\") correcto");
    }

    private static void probarNumero() {
        List<Token> tokens = new Lexer().extractTokens("123");
        if (tokens.size() < 2 || tokens.get(0).getTipo() != TokenType.NUMBER) {
            fallar("numero: Se esperaba NUMBER pero se obtuvo " + (tokens.isEmpty() ? "vacio" : tokens.get(0).getTipo()));
            return;
        }
        if (!"123".equals(tokens.get(0).getValor())) {
            fallar("numero: Valor esperado '123' pero se obtuvo '" + tokens.get(0).getValor() + "'");
            return;
        }
        pasar("numero: NUMBER(\"123\") correcto");
    }

    private static void probarSimbolo(char simbolo, TokenType esperado) {
        List<Token> tokens = new Lexer().extractTokens(String.valueOf(simbolo));
        if (tokens.size() < 2 || tokens.get(0).getTipo() != esperado) {
            fallar("simbolo(" + simbolo + "): Se esperaba " + esperado + " pero se obtuvo " + (tokens.isEmpty() ? "vacio" : tokens.get(0).getTipo()));
            return;
        }
        pasar("simbolo(" + simbolo + "): " + esperado + " correcto");
    }

    private static void probarIgnorarEspaciosYLineas() {
        List<Token> tokens = new Lexer().extractTokens("LEER   X\nESCRIBIR Y");

        if (tokens.size() < 4) {
            fallar("ignorarEspaciosYLineas: Se esperaban al menos 4 tokens, pero se obtuvo " + tokens.size());
            return;
        }

        Token primerToken = tokens.get(0);
        if (primerToken.getTipo() != TokenType.KEYWORD_READ || !"LEER".equals(primerToken.getValor()) || primerToken.getLinea() != 1) {
            fallar("ignorarEspaciosYLineas: Primer token esperado KEYWORD_READ(\"LEER\") linea 1, pero se obtuvo " + primerToken);
            return;
        }

        Token segundoToken = tokens.get(1);
        if (segundoToken.getTipo() != TokenType.IDENTIFIER || !"X".equals(segundoToken.getValor()) || segundoToken.getLinea() != 1) {
            fallar("ignorarEspaciosYLineas: Segundo token esperado IDENTIFIER(\"X\") linea 1, pero se obtuvo " + segundoToken);
            return;
        }

        Token tercerToken = tokens.get(2);
        if (tercerToken.getTipo() != TokenType.KEYWORD_WRITE || !"ESCRIBIR".equals(tercerToken.getValor()) || tercerToken.getLinea() != 2) {
            fallar("ignorarEspaciosYLineas: Tercer token esperado KEYWORD_WRITE(\"ESCRIBIR\") linea 2, pero se obtuvo " + tercerToken);
            return;
        }

        Token cuartoToken = tokens.get(3);
        if (cuartoToken.getTipo() != TokenType.IDENTIFIER || !"Y".equals(cuartoToken.getValor()) || cuartoToken.getLinea() != 2) {
            fallar("ignorarEspaciosYLineas: Cuarto token esperado IDENTIFIER(\"Y\") linea 2, pero se obtuvo " + cuartoToken);
            return;
        }

        pasar("ignorarEspaciosYLineas: tokens correctos con saltos de linea y espacios multiples");
    }

    private static void probarTerminaConEof() {
        List<Token> tokens = new Lexer().extractTokens("");
        if (tokens.size() != 1) {
            fallar("terminaConEof: Se esperaba exactamente 1 token (EOF) para entrada vacia, pero se obtuvo " + tokens.size());
            return;
        }
        if (tokens.get(0).getTipo() != TokenType.EOF) {
            fallar("terminaConEof: Se esperaba EOF pero se obtuvo " + tokens.get(0).getTipo());
            return;
        }

        tokens = new Lexer().extractTokens("X");
        Token ultimo = tokens.get(tokens.size() - 1);
        if (ultimo.getTipo() != TokenType.EOF) {
            fallar("terminaConEof: Ultimo token esperado EOF pero se obtuvo " + ultimo.getTipo());
            return;
        }
        pasar("terminaConEof: lista termina con EOF en ambos casos");
    }

    private static void probarCaracterNoReconocido() {
        try {
            new Lexer().extractTokens("@");
            fallar("caracterNoReconocido: Se esperaba LexicalException por caracter '@'");
        } catch (LexicalException e) {
            pasar("caracterNoReconocido: " + e.getMessage());
        }
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
