import java.util.List;
import java.io.IOException;

public class Main {
public static void main(String[] args) throws IOException {
  List<Token> tokens = null;
  String data = "+++_abc";
  Lexer lexer = new Lexer(data);
  tokens = lexer.getTokens();
  for (Token token : tokens) {
    System.out.println(token);
  }
}
}
