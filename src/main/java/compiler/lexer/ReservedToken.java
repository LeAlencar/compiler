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
            case '$':
                code.next();
                return new Token("IDWRITE", "$");
                
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
                if (code.current() == '<') {
                    code.next();
                    return new Token("WRITECHAR", "<<");
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
        } else if (current == 'i' && checkWord(code, "intero")) {
            return new Token("TYPE_INT", "intero");
        } else if (current == 's' && checkWord(code, "stringa")) {
            return new Token("TYPE_STRING", "stringa");
        } else if (current == 'b' && checkWord(code, "booleano")) {
            return new Token("TYPE_BOOL", "booleano");
        } else if (current == 'g' && checkWord(code, "galleggiante")) {
            return new Token("TYPE_FLOAT", "galleggiante");
        } else if (current == 'l' && checkWord(code, "leggere")) {
            return new Token("READ", "leggere");
        } else if (current == 'c' && checkWord(code, "carattere")) {
            return new Token("WRITE", "carattere");
        } else if (current == 'x' && checkWord(code, "xD")) {
            return new Token("READCARACTER", "xD");
        } else if (current == 'f' && checkWord(code, "funzione")) {
            return new Token("FUNCTION", "funzione");
        } else if (current == 'f' && checkWord(code, "fermare")) {
            return new Token("END_FUNCTION", "fermare");
        } else if (current == 'o' && checkWord(code, "o")) {
            return new Token("OR", "o");
        } else if (current == 'e' && checkWord(code, "e")) {
            return new Token("AND", "e");
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
        
        if (!match) {
            it.setIndex(startPos);
        }
        
        return match;
    }
}
