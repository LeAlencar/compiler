package compiler.lexer;

import java.text.CharacterIterator;

public class ID extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        if (code.current() == '_') {
            StringBuilder identifier = new StringBuilder();
            identifier.append(code.current());
            code.next();

            // Verifica se o próximo caractere é uma letra
            if (Character.isLetter(code.current())) {
                // Continua lendo enquanto for letra
                while (Character.isLetter(code.current())) {
                    identifier.append(code.current());
                    code.next();
                }

                // Se o próximo caractere não for uma letra, retorna o token
                if (!Character.isLetter(code.current())) {
                    return new Token("ID", identifier.toString());
                }
            }
        }
        return null;
    }
}
