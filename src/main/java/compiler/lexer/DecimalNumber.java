package compiler.lexer;

import java.text.CharacterIterator;

public class DecimalNumber extends AFD {
    @Override
    public Token evaluate(CharacterIterator code) {
        if (Character.isDigit(code.current())) {
            StringBuilder number = new StringBuilder();
            
            // Lê a parte inteira
            while (Character.isDigit(code.current())) {
                number.append(code.current());
                code.next();
            }
            
            // Verifica se tem ponto decimal
            if (code.current() == '.') {
                number.append('.');
                code.next();
                
                // Verifica se tem dígitos após o ponto
                if (Character.isDigit(code.current())) {
                    // Lê a parte decimal
                    while (Character.isDigit(code.current())) {
                        number.append(code.current());
                        code.next();
                    }
                    return new Token("NUMDECIMAL", number.toString());
                }
            }
        }
        return null;
    }
} 