package compiler.generator;

import java.io.FileWriter;
import java.io.IOException;

public class GoFileGenerator {
    public static void generateGoFile(String code, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(code);
            System.out.println("Arquivo Go gerado: " + fileName);
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo Go: " + e.getMessage());
        }
    }
} 