import java.util.List;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Token> tokens = null;
        
        String data = "_variavel _123 _abc se altrimenti intero _abc123 _teste {} 123123123 se (_num > 5){  _return _true }";
        Lexer lexer = new Lexer(data);
        tokens = lexer.getTokens(); 
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
