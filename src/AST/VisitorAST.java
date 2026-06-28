package AST;

public interface VisitorAST<T> {
    T visitarNodo(NodoPrograma nodo);
    T visitarNodo(NodoLeer nodo);
    T visitarNodo(NodoEscribir nodo);
    T visitarNodo(NodoAsignacion nodo);
    T visitarNodo(NodoSi nodo);
    T visitarNodo(NodoMientras nodo);
    T visitarNodo(NodoExpresion nodo);
    T visitarNodo(NodoIdentificador nodo);
    T visitarNodo(NodoLiteral nodo);
}