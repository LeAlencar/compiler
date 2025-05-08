package compiler.server;

import java.io.*;
import java.net.Socket;


public class Client {
    private final String host;
    private final int port;
    

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String compile(String data) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(data);
            return in.readLine();
        } catch (IOException e) {
            return "ERRO: Erro de conexão - " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida. Usando porta padrão 8080.");
            }
        }

        Client client = new Client(host, port);
        
        // Exemplo de código para compilar
        String data = "intero _x = 10; intero _y = 5; _x = _x - _y";
        
        String result = client.compile(data);
        System.out.println("Resultado da compilação: " + result);
    }
} 