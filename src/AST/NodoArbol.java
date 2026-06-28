package AST;

public abstract class NodoArbol {
    public abstract <T> T aceptar(VisitorAST<T> visitor);
}
