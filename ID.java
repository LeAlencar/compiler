
import java.text.CharacterIterator;

public class ID extends AFD {
    
    @Override
    public Token evaluate(CharacterIterator code) {
        if (Character.isLetter(code.current()) || code.current() == '_') {
            StringBuilder identifier = new StringBuilder();
            
            identifier.append(code.current());
            code.next();
            
            while (Character.isLetterOrDigit(code.current()) || code.current() == '_') {
                identifier.append(code.current());
                code.next();
            }
            
            if (isTokenSeparator(code)) {
                return new Token("ID", identifier.toString());
            }
        }
        return null;
    }
}
