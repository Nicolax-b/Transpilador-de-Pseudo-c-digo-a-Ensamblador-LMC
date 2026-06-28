package Lexer;

public class Token {
    private final TokenType tipo;
    private final String valor;
    private final int linea;

    

    public Token(int linea, TokenType tipo, String valor) {
        this.linea = linea;
        this.tipo = tipo;
        this.valor = valor;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public int getLinea() {
        return linea;
    }

    @Override
    public String toString(){
        return String.format("Token [%s, '%s' , Linea: %d]", tipo, valor, linea);
    }
}
