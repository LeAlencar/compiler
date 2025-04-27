package compiler.lexer;

import java.text.CharacterIterator;
public class Number extends AFD{

    @Override
    public Token evaluate(CharacterIterator code){
        
        if(Character.isDigit(code.current())){
            String number = readNumber(code);
            if (code.current() == '.'){
                number += '.';
                code.next();
                number+= readNumber(code);
            }
            if (isTokenSeparator(code)){
                return new Token("NUM", number);
            }
        }
        return null;
    }
    
    private String readNumber(CharacterIterator code){
        String number = "";
        while(Character.isDigit(code.current())){
            number+= code.current();
            code.next();
        }
        return number;
    }
}
