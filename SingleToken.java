
import java.text.CharacterIterator;

public class ReservedToken extends AFD {
    
    @Override
    public Token evaluate(CharacterIterator code) {
        switch (code.current()) {
            case '(':
                code.next();
                return new Token("LPAREN", "(");
            case ')':
                code.next();
                return new Token("RPAREN", ")");
            case CharacterIterator.DONE:
                return new Token("EOF", "");
            default:
                return null;
        }
    }
}
