package compiler.lexer;

import java.text.CharacterIterator;

public class ReservedToken extends AFD {
    
    @Override
    public Token evaluate(CharacterIterator code) {
        char current = code.current();
        
        switch (current) {
            case '(':
                code.next();
                return new Token("LPAREN", "(");
            case ')':
                code.next();
                return new Token("RPAREN", ")");
            case '[':
                code.next();
                return new Token("LBRACK", "[");
            case ']':
                code.next();
                return new Token("RBRACK", "]");
            case '{':
                code.next();
                return new Token("LBRACE", "{");
            case '}':
                code.next();
                return new Token("RBRACE", "}");
            case ',':
                code.next();
                return new Token("COMMA", ",");
            case '.':
                code.next();
                return new Token("PERIOD", ".");
            case ';':
                code.next();
                return new Token("SEMICOLON", ";");
            case ':':
                code.next();
                return new Token("COLON", ":");
                
            case '=':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("EQL", "==");
                }
                return new Token("ASSIGN", "=");
                
            case '<':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("LEQ", "<=");
                }
                return new Token("LSS", "<");
                
            case '>':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("GEQ", ">=");
                }
                return new Token("GTR", ">");
                
            case '!':
                code.next();
                if (code.current() == '=') {
                    code.next();
                    return new Token("NEQ", "!=");
                }
                return new Token("NOT", "!");

           
            case CharacterIterator.DONE:
                return new Token("EOF", "");
                
            default:
                break;
        }
        
        if (current == 's' && checkWord(code, "se")) {
            return new Token("IF", "se");
        } else if (current == 'a' && checkWord(code, "altrimenti")) {
            return new Token("ELSE", "altrimenti");
        } else if (current == 'p' && checkWord(code, "per")) {
            return new Token("FOR", "per");
        } else if (current == 'm' && checkWord(code, "mentre")) {
            return new Token("WHILE", "mentre");
        } else if (current == 'f' && checkWord(code, "fare")) {
            return new Token("DO", "fare");
        }
        else if (current == 'i' && checkWord(code, "intero")) {
            return new Token("TYPE_INT", "intero");
        } else if (current == 's' && checkWord(code, "stringa")) {
            return new Token("TYPE_STRING", "stringa");
        } else if (current == 'b' && checkWord(code, "booleano")) {
            return new Token("TYPE_BOOL", "booleano");
        }
      
        return null;
    }
    
    private boolean checkWord(CharacterIterator it, String word) {
        int startPos = it.getIndex();
        boolean match = true;
        
        for (int i = 0; i < word.length(); i++) {
            if (it.current() != word.charAt(i)) {
                match = false;
                break;
            }
            it.next();
        }
        
        if (match && !isTokenSeparator(it)) {
            match = false;
        }
        
        if (!match) {
            it.setIndex(startPos);
        }
        
        return match;
    }
}
