package compiler.server;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.generator.GoFileGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
@RestController
@RequestMapping("/api/compiler")
public class CompilerServer {

    public static void main(String[] args) {
        SpringApplication.run(CompilerServer.class, args);
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Server Rodando");
        return response;
    }

    @PostMapping("/compile")
    public Map<String, Object> compileCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String code = request.get("code");

        if (code == null || code.trim().isEmpty()) {
            response.put("Compilado com Sucesso", false);
            response.put("erro", "Código fonte não pode estar vazio");
            return response;
        }

        try {
            // Análise léxica
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.getTokens();

            // Análise sintática e geração de código Go
            Parser parser = new Parser(tokens);
            String goCode = parser.parse();

            if (goCode != null) {
                // Gera o arquivo Go
                GoFileGenerator generator = new GoFileGenerator();
                generator.appendLine(goCode);
                generator.generateFile();

                response.put("Compilado com Sucesso", true);
                response.put("Código Go Gerado", goCode);
                response.put("tokens", tokens);
            } else {
                response.put("Compilado com Sucesso", false);
                response.put("erro", "Falha na análise sintática");
                response.put("Código Inválido", code);
            }
        } catch (Exception e) {
            response.put("Compilado com Sucesso", false);
            response.put("erro", e.getMessage());
            response.put("Código Inválido", code);
        }

        return response;
    }
}