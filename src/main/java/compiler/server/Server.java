package compiler.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private final CompilerService compilerService;
    private boolean running;

    public Server(int port) {
        this.port = port;
        this.compilerService = new CompilerService();
        this.running = false;
    }

    public void start() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado na porta " + port);
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                    System.out.println("Porta do cliente: " + clientSocket.getPort());
                    
                    

                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String sourceCode = in.readLine();
                if (sourceCode != null) {
                    System.out.println("Código recebido do cliente: " + sourceCode);
                    String result = compilerService.compile(sourceCode);
                    out.println(result);
                }
            } catch (IOException e) {
                System.err.println("Erro ao processar cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar conexão do cliente: " + e.getMessage());
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    public static void main(String[] args) {
        int port = 8080; // Porta padrão
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida. Usando porta padrão 8080.");
            }
        }
        
        Server server = new Server(port);
        server.start();
    }
} 