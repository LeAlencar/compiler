package compiler.lexer;

import java.text.CharacterIterator;

public class NOME extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (Character.isLetter(code.current()) && code.current() != '_') {
            StringBuilder nome = new StringBuilder();
            while (Character.isLetter(code.current()) && code.current() != '_') {
                nome.append(code.current());
                code.next();
            }
            return new Token("NOME", nome.toString());
        }
        return null;
    }
} 