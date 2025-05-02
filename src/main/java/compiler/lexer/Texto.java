package compiler.lexer;

import java.text.CharacterIterator;

public class Texto extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == '"') {
            StringBuilder identifier = new StringBuilder();

            identifier.append(code.current());
            code.next();

            while (code.current() != '"' && code.current() != CharacterIterator.DONE) {
                identifier.append(code.current());
                code.next();
            }

            if (code.current() == '"') {
                identifier.append(code.current());
                code.next();
                return new Token("TEXTO", identifier.toString());
            }
        }
        return null;
    }
}
