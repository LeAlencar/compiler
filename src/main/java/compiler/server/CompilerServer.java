package compiler.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.semantic.SemanticAnalyzer;

public class CompilerServer {
    private final int port;
    private HttpServer server;

    public CompilerServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/compile", this::handleCompileRequest);
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Servidor iniciado na porta " + port);
    }

    private void handleCompileRequest(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        // Adiciona headers CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // Responde a requisições OPTIONS (preflight)
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        if (!exchange.getRequestMethod().equals("POST")) {
            sendResponse(exchange, 405, "Método não permitido");
            return;
        }

        try {
            // Lê o corpo da requisição
            String code = new String(exchange.getRequestBody().readAllBytes());
            
            // Processa o código através do compilador
            StringBuilder result = new StringBuilder();
            
            // Captura a saída do System.out
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            try {
                // Análise Léxica
                Lexer lexer = new Lexer(code);
                List<Token> tokens = lexer.getTokens();
                
                // Limpa qualquer saída anterior e adiciona nossa saída estruturada
                outputStream.reset();
                result.append("Tokens encontrados:\n");
                for (Token token : tokens) {
                    result.append(token).append("\n");
                }

                // Análise Sintática
                result.append("\nIniciando análise sintática...\n");
                Parser parser = new Parser(tokens);
                parser.main();
                // Obtém a saída do parser (AST e outros detalhes)
                result.append(outputStream.toString());
                result.append("\nSintaticamente correta\n");

                // Análise Semântica
                result.append("\nIniciando análise semântica...\n");
                outputStream.reset();
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(tokens);
                String resultado = semanticAnalyzer.analyze();
                // Obtém a saída da análise semântica
                result.append(resultado);
                result.append(outputStream.toString());
            } finally {
                // Restaura o System.out original
                System.setOut(originalOut);
            }

            // Envia resposta de sucesso
            try {
                Thread.sleep(1500); // Delay de 1.5 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sendResponse(exchange, 200, result.toString());

        } catch (Exception e) {
            // Envia resposta de erro
            try {
                Thread.sleep(1500); // Delay de 1.5 segundos
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            sendResponse(exchange, 400, "Erro na compilação: " + e.getMessage());
        }
    }

    private void sendResponse(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Servidor parado");
        }
    }

    public static void main(String[] args) {
        try {
            CompilerServer server = new CompilerServer(8080);
            server.start();
            
            // Mantém o servidor rodando
            System.out.println("Pressione Ctrl+C para parar o servidor");
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}