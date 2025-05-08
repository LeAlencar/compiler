package compiler.server.tcpIp;

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

            try {
                // Análise sintática
                Parser parser = new Parser(tokens);
                parser.main();
                
                // Restaurar a saída padrão
                System.out.flush();
                System.setOut(oldOut);
                
                String output = outputStream.toString();
                System.out.println(output);
                
                // Verifica se há mensagens de erro na saída
                if (output.contains("token inválido") || output.contains("Erro")) {
                    return "ERRO: " + output;
                }
                
                return "SUCCESSO";
            } catch (Exception e) {
                // Restaurar a saída padrão em caso de erro
                System.out.flush();
                System.setOut(oldOut);
                return "ERRO: " + e.getMessage();
            }
        } catch (Exception e) {
            return "ERRO: " + e.getMessage();
        }
    }
} 