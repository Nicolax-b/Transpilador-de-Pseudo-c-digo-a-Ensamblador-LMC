# Proyecto B5 — Transpilador de Pseudocódigo a Ensamblador LMC

Analiza pseudocódigo con palabras clave en español (`LEER`, `ESCRIBIR`, `SI`,
`MIENTRAS`, etc.) y construye un Árbol Sintáctico Abstracto (AST) que otro
módulo consumirá para generar código ensamblador LMC (*Little Man Computer*).

## Estructura del proyecto

```
src/
├── App.java           — Pipeline principal: lee un archivo .psc, lo tokeniza,
│                        lo parsea y muestra el resultado.
├── Lexer/
│   ├── Lexer.java          — Analizador léxico (divide el código fuente en tokens).
│   ├── LexerTest.java      — Tests unitarios del Lexer (15 pruebas).
│   ├── LexicalException.java — Excepción de dominio para errores léxicos.
│   ├── Token.java           — Representación de un token (tipo, valor, línea).
│   └── TokenType.java       — Enumeración de todos los tipos de token.
├── AST/
│   ├── NodoArbol.java               — Clase abstracta base de todo nodo del AST.
│   ├── NodoPrograma.java             — Raíz del AST, contiene la lista de sentencias.
│   ├── NodoLeer.java                 — Instrucción LEER <id>.
│   ├── NodoEscribir.java             — Instrucción ESCRIBIR <expr>.
│   ├── NodoAsignacion.java           — Asignación <id> = <expr>.
│   ├── NodoSi.java                   — Condicional SI ... ENTONCES ... FIN_SI.
│   ├── NodoMientras.java             — Bucle MIENTRAS ... FIN_MIENTRAS.
│   ├── NodoOperacionBinaria.java     — Operación aritmética binaria (ej. B + C).
│   ├── NodoComparacion.java          — Comparación relacional (ej. A > 0).
│   ├── NodoIdentificador.java        — Variable o identificador.
│   ├── NodoLiteral.java              — Valor numérico literal.
│   ├── OperadorAritmetico.java       — Enum: PLUS, MINUS, MULTIPLY, DIVIDE.
│   ├── OperadorComparacion.java      — Enum: GREATER_THAN, LESS_THAN, EQUALS.
│   └── VisitorAST.java               — Interfaz Visitor con Double Dispatch.
└── Parser/
    ├── Parser.java          — Analizador sintáctico (construye el AST).
    ├── ParserTest.java      — Tests unitarios del Parser (12 pruebas).
    └── SyntaxException.java — Excepción de dominio para errores sintácticos.
```

## Cómo compilar y ejecutar

```bash
# Compilar todo el proyecto
javac -d classes -sourcepath src src/App.java

# Ejecutar el pipeline con el archivo de ejemplo
java -cp classes App ejemplo.psc

# Ejecutar tests del Lexer
java -cp classes Lexer.LexerTest

# Ejecutar tests del Parser
java -cp classes Parser.ParserTest
```

## Gramática soportada

### Lectura

```
LEER <identificador>
```

Lee un valor desde la entrada estándar y lo almacena en la variable indicada.

### Escritura

```
ESCRIBIR <expresión>
```

Escribe el valor de una expresión en la salida estándar. La expresión puede
ser una variable, un número literal o una suma encadenada (ej. `ESCRIBIR A+B`).

### Asignación

```
<identificador> = <expresión>
```

Evalúa la expresión del lado derecho y asigna el resultado a la variable.

### Condicional

```
SI <condición> ENTONCES
  <sentencias>
FIN_SI
```

Evalúa la condición relacional (ej. `A > 0`); si se cumple, ejecuta el bloque
interno.

### Bucle

```
MIENTRAS <condición>
  <sentencias>
FIN_MIENTRAS
```

Evalúa la condición antes de cada iteración; mientras sea verdadera, ejecuta
el bloque interno.

### Ejemplo completo

```
LEER X
LEER Y
A = X + Y
ESCRIBIR A
SI A > 10 ENTONCES
ESCRIBIR X
FIN_SI
MIENTRAS X > 0
X = X + 1
FIN_MIENTRAS
```

### Precedencia de operadores

El parser implementa dos niveles de precedencia:

1. **Relacional** (`>`): `A > B` se parsea como `NodoComparacion`.
2. **Aditivo** (`+`): `B + C + D` se parsea como `((B + C) + D)` (asociativo a
   la izquierda).

## Casos de prueba documentados

### LexerTest (15 pruebas)

| Test | Qué verifica | Entrada | Resultado esperado |
|---|---|---|---|
| `palabraClave(LEER)` | Tokenización de palabra clave | `"LEER"` | `Token(KEYWORD_READ, "LEER")` |
| `palabraClave(ESCRIBIR)` | Tokenización de palabra clave | `"ESCRIBIR"` | `Token(KEYWORD_WRITE, "ESCRIBIR")` |
| `palabraClave(SI)` | Tokenización de palabra clave | `"SI"` | `Token(KEYWORD_IF, "SI")` |
| `palabraClave(ENTONCES)` | Tokenización de palabra clave | `"ENTONCES"` | `Token(KEYWORD_THEN, "ENTONCES")` |
| `palabraClave(FIN_SI)` | Tokenización de palabra clave | `"FIN_SI"` | `Token(KEYWORD_ENDIF, "FIN_SI")` |
| `palabraClave(MIENTRAS)` | Tokenización de palabra clave | `"MIENTRAS"` | `Token(KEYWORD_WHILE, "MIENTRAS")` |
| `palabraClave(FIN_MIENTRAS)` | Tokenización de palabra clave | `"FIN_MIENTRAS"` | `Token(KEYWORD_ENDWHILE, "FIN_MIENTRAS")` |
| `identificador` | Tokenización de identificador (con `_`) | `"variableX"` | `Token(IDENTIFIER, "variableX")` |
| `numero` | Tokenización de literal numérico | `"123"` | `Token(NUMBER, "123")` |
| `simbolo(=)` | Tokenización de asignación | `"="` | `Token(ASSIGN, "=")` |
| `simbolo(+)` | Tokenización de suma | `"+"` | `Token(PLUS, "+")` |
| `simbolo(>)` | Tokenización de mayor que | `">"` | `Token(GREATER_THAN, ">")` |
| `ignorarEspaciosYLineas` | Espacios múltiples y saltos de línea | `"LEER   X\nESCRIBIR Y"` | 4 tokens correctos, línea 1 y 2 |
| `terminaConEof` | La lista siempre termina con EOF | `""` y `"X"` | Último token es `EOF` |
| `caracterNoReconocido` | Caracter inválido lanza excepción | `"@"` | `LexicalException` |

### ParserTest (12 pruebas)

| Test | Qué verifica | Entrada | Resultado esperado |
|---|---|---|---|
| `lecturaSinIdentificadorLanzaError` | Error si LEER no va seguido de ID | `"LEER"` | `SyntaxException` |
| `siSinEntoncesLanzaError` | Error si SI no tiene ENTONCES | `"SI A > 0"` | `SyntaxException` |
| `siSinFinSiLanzaError` | Error si SI no se cierra con FIN_SI | `"SI A > 0 ENTONCES LEER B"` | `SyntaxException` |
| `mientrasSinFinMientrasLanzaError` | Error si MIENTRAS no se cierra | `"MIENTRAS A > 0 LEER B"` | `SyntaxException` |
| `leerCorrecto` | LEER construye NodoLeer correcto | `"LEER X"` | `NodoLeer{id="X"}` |
| `escribirCorrecto` | ESCRIBIR construye NodoEscribir | `"ESCRIBIR Y"` | `NodoEscribir{expresion=NodoIdentificador("Y")}` |
| `asignacionAritmetica` | Asignación con suma (B + 1) | `"A = B + 1"` | `NodoAsignacion{id="A", expr=NodoOperacionBinaria{PLUS}}` |
| `siCompleto` | Condicional con condición y bloque | `"SI A > 0 ENTONCES LEER B FIN_SI"` | `NodoSi{cond=NodoComparacion{GREATER_THAN}, bloque=[NodoLeer]}` |
| `mientrasCompleto` | Bucle con condición y bloque | `"MIENTRAS A > 0 LEER B FIN_MIENTRAS"` | `NodoMientras{cond=NodoComparacion{}, bloque=[NodoLeer]}` |
| `operadoresEncadenados` | Suma encadenada (B + C + D) | `"A = B + C + D"` | `((B + C) + D)`, asociativo a la izquierda |
| `siAnidadoDentroDeMientras` | SI dentro de MIENTRAS | `"MIENTRAS A > 0 SI A > 10 ENTONCES ESCRIBIR A FIN_SI FIN_MIENTRAS"` | AST anidado correcto |
| `mientrasAnidadoDentroDeSi` | MIENTRAS dentro de SI | `"SI A > 0 ENTONCES MIENTRAS A > 10 LEER X FIN_MIENTRAS FIN_SI"` | AST anidado correcto |

## Limitaciones conocidas

- **Operadores aritméticos no tokenizados**: El Lexer solo reconoce `+`. Los
  operadores `-` (resta), `*` (multiplicación) y `/` (división) existen en el
  enum `OperadorAritmetico` del AST y el Parser ya está preparado para
  procesarlos en `procesarExpresionAditiva()`, pero el Lexer aún no los
  tokeniza. Agregarlos requiere una entrada en `consumirSimbolo()` y una
  nueva entrada en `TokenType`.
- **Operadores de comparación no tokenizados**: Solo `>` está tokenizado.
  Los operadores `<` y `==` existen en el enum `OperadorComparacion` y el
  Parser (`procesarExpresionRelacional()`) ya tiene los condicionales
  comentados listos para activarse, pero el Lexer aún no los produce.
- **Comparaciones encadenadas**: La gramática relacional solo permite una
  comparación por expresión (ej. `A > B`). Una expresión como `A > B > C` no
  es válida.
- **Sin bloque ELSE**: La estructura `SI` no soporta rama falsa (`SINO` o
  `ELSE`).
- **Sin negación**: No existe operador lógico `NO` ni forma de negar una
  condición.
- **Tipado**: No hay verificación de tipos. El AST almacena todo como texto
  y la validación semántica queda para una fase posterior (generación de
  código LMC).
- **Sin entrada/salida real**: `App.java` ejecuta el pipeline léxico →
  sintáctico pero no hay un módulo de ejecución o generación de código LMC
  todavía.
