package compiler.lexer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    
    private List<Token> tokens;
    private List<AFD> afds;
    private CharacterIterator code;
    
    public Lexer(String code) {
        tokens = new ArrayList<>();
        afds = new ArrayList<>();
        this.code = new StringCharacterIterator(code);
        afds.add(new ReservedToken());
        afds.add(new Texto());
        afds.add(new DecimalNumber());
        afds.add(new Number());
        afds.add(new MathOperator());
        afds.add(new ID());
        afds.add(new NOME());
    }
    
    public void skipWhiteSpace() {
        while (code.current() != CharacterIterator.DONE && 
               (code.current() == ' ' || code.current() == '\n' || code.current() == '\r' || code.current() == '\t')) {
            code.next();
        }
    }
    
    public List<Token> getTokens() {
        Token t;
        do {
            skipWhiteSpace();
            t = searchNextToken();
            if (t == null) error();
            tokens.add(t);
        }while (t.getTipo() != "EOF");
        return tokens;
    }
    
    private Token searchNextToken() {
        int pos = code.getIndex();
        for (AFD afd : afds) {
            Token t = afd.evaluate(code);
            if (t != null) return t;
            code.setIndex(pos);
        }
        return null;
    }
    
    private void error() {
        StringBuilder context = new StringBuilder();
        int pos = code.getIndex();
        code.setIndex(Math.max(0, pos - 10));
        for (int i = 0; i < 20 && code.current() != CharacterIterator.DONE; i++) {
            context.append(code.current());
            code.next();
        }
        code.setIndex(pos);
        throw new RuntimeException("Erro: token não reconhecido '" + code.current() + "' na posição " + pos + 
                                 "\nContexto: ..." + context.toString() + "...");
    }
}

