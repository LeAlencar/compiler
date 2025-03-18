
import java.text.CharacterIterator;

public class MathOperator extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        
        switch (code.current()) {
            case '+':
                code.next();
                return new Token("ADD", "+");
                
            case '-':
                code.next();
                return new Token("SUB", "-");
            case '/':
                code.next();
                return new Token("QUO", "/");
            case '*':
                code.next();
                return new Token("MUL", "*");
            case '%':
                code.next();
                return new Token("REM", "%");
            
            case CharacterIterator.DONE:
                return new Token("EOF", "$");
            default:
                return null;
        }
    }
}
