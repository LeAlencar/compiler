package compiler.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class GoFileGenerator {
  private final StringBuilder goCode;
  private final String outputPath;

  public GoFileGenerator() {
    this.goCode = new StringBuilder();
    // Get the project root directory
    this.outputPath = Paths.get("").toAbsolutePath().toString() + "/main.go";
  }

  public void appendLine(String line) {
    goCode.append(line).append("\n");
  }

  public void generateFile() throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
      writer.print(goCode.toString());
      System.out.println("\nArquivo Go gerado com sucesso: " + outputPath);
    } catch (IOException e) {
      throw new IOException("Erro ao gerar arquivo Go: " + e.getMessage());
    }
  }

  public void clear() {
    goCode.setLength(0);
  }
}