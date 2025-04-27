package compiler.main;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;

import java.io.IOException;
import java.util.List;


public class Main
{
    public static void main(String[] args) throws IOException {
        List<Token> tokens = null;

        // Exemplo de entrada com sintaxe em italiano
        String data = "se (_num > 5) { _x = 10 } altrimenti { _x = 20 }";

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
