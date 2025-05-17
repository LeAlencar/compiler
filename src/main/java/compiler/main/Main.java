package compiler.main;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.semantic.SemanticAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// Exemplo de entrada com sintaxe em italiano
// String data = "intero _x = 10; intero _y = 5; se (_x > _y) {_x = _x + _y;}
// altrimenti {_y = _y + _x;} per (intero _i = 0; _i < 3; _i += 1) {_x = _x *
// 2;}";
// String data = "intero _x = 10; intero _y = 5; intero _z = _x * _y;";

//String data = "stringa _s = \"Hello, World!\";" +
//   "carattere << $_s;intero _x = 10;stringa _leitura = \"\";" +
//   "se _x == 10 {leggere xD \"Escreva seu nome\" _leitura;_x=1;}" +
//   "fare {_x = _x + 1;_x = _x * 2;} mentre _x < 15;" +
//   "per (intero _i = 0; _i < 10; _i += 1;) {_x = _x * 2;}teste();funzione teste()intero _j =10;intero _k = _j + 1;fermare funzione testee()fermare";

// String data = "intero _x = 10; intero _y = 5; intero _z = _x * _y;funzione teste()intero _j =10;intero _k = _j + 1;fermare";
public class Main {
    public static void main(String[] args) throws IOException {
        List<Token> tokens = null;

        // Read from codigo.txt file
        String data = new String(Files.readAllBytes(Paths.get("src/main/java/compiler/main/codigo.txt")));
        //System.out.println(data);
        // Análise léxica
        Lexer lexer = new Lexer(data);
        tokens = lexer.getTokens();

        // Imprime os tokens encontrados
        System.out.println("Tokens encontrados:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // Análise sintática
        System.out.println("\nIniciando análise sintática...");
        Parser parser = new Parser(tokens);
        parser.main();
        System.out.println("\nSintaticamente correta\n");

        // Análise semântica
        System.out.println("\nIniciando análise semântica...");
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(tokens);
        semanticAnalyzer.analyze();
    }
}
