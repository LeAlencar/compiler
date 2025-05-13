package compiler.server;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@SpringBootApplication
@RestController
@RequestMapping("/api/compiler")
public class CompilerServer {

    public static void main(String[] args) {
        SpringApplication.run(CompilerServer.class, args);
    }


    /*
     * @GetMapping("/health")
     * public Map<String, String> healthCheck() {
     * Map<String, String> response = new HashMap<>();
     * response.put("status", "Server Rodando");
     * return response;
     * }
     */

    @PostMapping("/compile")
    public Map<String, Object> compileCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String code = request.get("code");

        try {
            // Análise léxica
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.getTokens();

            // Análise sintática
            Parser parser = new Parser(tokens);
            // Captura a saída do System.out
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            parser.main();
           
            // Restaura o System.out original
            System.setOut(originalOut);

            String result = outputStream.toString();

            response.put("Compilado com Sucesso", true);
            response.put("Resultado", result);
            response.put("tokens", tokens);
        } catch (Exception e) {
            response.put("Compilado com Sucesso", false);
            response.put("erro", e.getMessage());
            response.put("Código Inválido", code);
        }

        return response;
    }
} 