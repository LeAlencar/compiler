package compiler.lexer;

import java.text.CharacterIterator;

public class MathOperator extends AFD {

    @Override
    public Token evaluate(CharacterIterator code) {
        
        switch (code.current()) {
            case '+':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("ADD_ASSIGN", "+=");
                }
                return new Token("ADD", "+");
                
            case '-':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("SUB_ASSIGN", "-=");
                }
                return new Token("SUB", "-");
                
            case '*':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("MUL_ASSIGN", "*=");
                }
                return new Token("MUL", "*");

            case '/':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("DIV_ASSIGN", "/=");
                }
                return new Token("DIV", "/");

            case '%':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("MOD_ASSIGN", "%=");
                }
                return new Token("MOD", "%");
                
            case CharacterIterator.DONE:
                return new Token("EOF", "$");
                
            default:
                return null;
        }
    }
}
