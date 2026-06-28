package Lexer;
public enum TokenType {
    //Palabras clave
    KEYWORD_READ, KEYWORD_WRITE, KEYWORD_IF, KEYWORD_THEN,
    KEYWORD_ENDIF, KEYWORD_WHILE, KEYWORD_ENDWHILE,

    // Literales e Identificadores
    IDENTIFIER, NUMBER, 
    
    // Operadores
    ASSIGN, PLUS, GREATER_THAN, 
    
    // Control de flujo
    EOF // End of file: Indica el final del documento
}
