package compiler.main;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.generator.GoFileGenerator;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Token> tokens = null;

        // Exemplo de entrada com sintaxe em italiano
        // String data = "intero _x = 10; intero _y = 5; se (_x > _y) {_x = _x + _y;}
        // altrimenti {_y = _y + _x;} per (intero _i = 0; _i < 3; _i += 1) {_x = _x *
        // 2;}";
        // String data = "intero _x = 10; intero _y = 5; intero _z = _x * _y;";

        String data = "stringa _s = \"Hello, World!\"; carattere << _s;";
        /*
         * "carattere << $_s;intero _x = 10;" +
         * "se _x == 10 {leggere xD \"Escreva seu nome\" _leitura;_x=1;}" +
         * "fare {_x = _x + 1;} mentre _x < 15;" +
         * "per (intero _i = 0; _i < 10; _i += 1;) {_x = _x * 2;}";
         */

        /*
         * String data = "intero _x = 10; " +
         * "intero _y = 5; " +
         * "se (_x > _y) { " +
         * "    _x = _x + _y; " +
         * "    carattere << _x; " +
         * "} altrimenti { " +
         * "    _y = _y + _x; " +
         * "    carattere << _y; " +
         * "} " +
         * "per (intero _i = 0; _i < 3; _i += 1) { " +
         * "    _x = _x * 2; " +
         * "    carattere << _x; " +
         * "}";
         */

        try {
            // Análise léxica
            Lexer lexer = new Lexer(data);
            tokens = lexer.getTokens();

            // Imprime os tokens encontrados
            System.out.println("Tokens encontrados:");
            for (Token token : tokens) {
                System.out.println(token);
            }

            // Análise sintática e semântica
            System.out.println("\nIniciando análise sintática e semântica:");
            Parser parser = new Parser(tokens);
            String goCode = parser.parse();

            System.out.println(goCode);
            // Se a compilação foi bem sucedida, gera o arquivo
            if (goCode != null) {
                GoFileGenerator generator = new GoFileGenerator();
                generator.appendLine(goCode);
                generator.generateFile();
            }

        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo Go: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }
}
