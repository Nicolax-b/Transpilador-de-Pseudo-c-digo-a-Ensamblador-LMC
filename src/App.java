import Lexer.Lexer;
import Lexer.Token;
import Parser.Parser;
import AST.NodoPrograma;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class App {

    private List<Token> tokens;
    private NodoPrograma ast;

    public App(String rutaArchivo) throws Exception {
        String codigoFuente = Files.readString(Paths.get(rutaArchivo));

        Lexer lexer = new Lexer();
        this.tokens = lexer.extractTokens(codigoFuente);

        Parser parser = new Parser(tokens);
        this.ast = parser.crearArbol();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public NodoPrograma getAst() {
        return ast;
    }

    public static void main(String[] args) throws Exception {
        String ruta = args.length > 0 ? args[0] : "ejemplo.psc";
        App app = new App(ruta);

        System.out.println("=== TOKENS GENERADOS ===");
        for (Token t : app.getTokens()) {
            System.out.println("  " + t);
        }

        System.out.println("\n=== AST GENERADO ===");
        System.out.println("  NodoPrograma con " + app.getAst().getSentencias().size() + " sentencias");

        System.out.println("\nPipeline completado exitosamente.");
    }
}
