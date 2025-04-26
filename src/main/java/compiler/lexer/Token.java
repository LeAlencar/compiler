package compiler.lexer;

public class Token {
    private String lexema;
    private String tipo;
    
    public Token(String tipo, String lexema) {
        this.lexema = lexema;
        this.tipo = tipo;
    }
    
    public String getLexema() {
        return lexema;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    @Override
    public String toString() {
        return "<" + tipo + ", " + lexema + ">";
    }
}
