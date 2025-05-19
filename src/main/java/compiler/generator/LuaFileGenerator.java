package compiler.generator;
import java.io.FileWriter;
import java.io.IOException;

public class LuaFileGenerator {

    public static void generateLuaFile(String code, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(code);
            System.out.println("Arquivo Lua gerado: " + fileName);
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo Lua: " + e.getMessage());
        }
    }

}
