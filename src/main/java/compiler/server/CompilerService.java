package compiler.server;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class CompilerService {
    
    public String compile(String sourceCode) {
        try {
            // Análise léxica
            Lexer lexer = new Lexer(sourceCode);
            List<Token> tokens = lexer.getTokens();

            // Capturar a saída do Parser
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            PrintStream oldOut = System.out;
            System.setOut(printStream);

            // Análise sintática
            Parser parser = new Parser(tokens);
            parser.main();
            
            // Restaurar a saída padrão
            System.out.flush();
            System.setOut(oldOut);
            
            return "SUCCESS:" + outputStream.toString();
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }
} 