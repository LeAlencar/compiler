package compiler.main;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.parser.Tree;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Token> tokens = null;

        // Exemplo de entrada com sintaxe em italiano
        String data = "intero x = 10;mentre (x > 0) {x -= 1}per (intero i = 0; i < 10; i += 1) {x = x * 2}fare {x = x / 2}mentre (x > 100);";

        // Análise léxica
        Lexer lexer = new Lexer(data);
        tokens = lexer.getTokens();

        // Imprime os tokens encontrados
        System.out.println("Tokens encontrados:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // Análise sintática
        System.out.println("\nIniciando análise sintática:");
        Parser parser = new Parser(tokens);
        parser.main();
    }
}
