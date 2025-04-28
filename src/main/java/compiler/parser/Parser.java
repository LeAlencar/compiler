package compiler.parser;

import compiler.lexer.Token;

import java.util.List;

public class Parser {

  List<Token> tokens;
  Token token;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public void main() {
    token = getNextToken();
    if(ifelse() || declaracao()){
      if (token.getTipo().equals("EOF")){
        System.out.println("\nSintaticamente correta");
        return;
      }
      else{
        main();
      }
    }
    else{
      erro();
    }
  }

  public Token getNextToken() {
    if (tokens.size() > 0) {
      return tokens.remove(0);
    } else
      return null;
  }

  private void erro() {
    System.out.println("token inválido: " + token.getLexema());
  }

  private boolean declaracao() {
    if ((matchT("TYPE_INT", token.getLexema()) || matchT("TYPE_STRING", token.getLexema()) || 
    matchT("TYPE_BOOL", token.getLexema())) && id() && matchT("ASSIGN", token.getLexema()) && (num() || id())){
      return true;
    }
    return false;
  }

  private boolean ifelse() {
    if (matchT("IF", token.getLexema()) && condicao() && bloco() && matchT("ELSE", token.getLexema()) && bloco()){
      return true;
    }
    return false;
  }

  private boolean bloco() {
    if (matchT("LBRACE", token.getLexema())) {
      if (id() && operadorAtribuicao() && num() && matchT("RBRACE", token.getLexema())) {
        return true;
      }
      return false;
    }
    return id() && operadorAtribuicao() && num();
  }

  private boolean operadorAtribuicao() {
    if(matchT("ASSIGN", token.getLexema())){
      return true;
    }
    return false;
  }

  private boolean condicao() {
    if (matchT("LPAREN", token.getLexema())) {
      if (id() && operador() && num() && matchT("RPAREN", token.getLexema())) {
        return true;
      }
      return false;
    }
    return id() && operador() && num();
  }

  private boolean operador() {
    if (matchT("GTR", token.getLexema()) || matchT("LSS", token.getLexema()) || matchT("EQL", token.getLexema()) || 
        matchT("GEQ", token.getLexema()) || matchT("LEQ", token.getLexema()) || matchT("NEQ", token.getLexema())) {
      return true;
    }
    return false;
  }

  private boolean id() {
    if (matchT("ID", token.getLexema())){
      return true;
    }
    return false;
  }

  private boolean num() {
    if(matchT("NUM", token.getLexema())){
      return true;
    }
    return false;
  }

  private boolean matchL(String palavra, String newcode){
    if (token.getLexema().equals(palavra)){
      traduz(newcode);
      token = getNextToken();
      return true;
    }
    return false;
  }

  private boolean matchT(String palavra, String newcode){
    if (token.getTipo().equals(palavra)){
      traduz(newcode);
      token = getNextToken();
      return true;
    }
    return false;
  }

  private void traduz(String code) {
    if (code.equals("se")){
      System.out.print("if");
    }
    else if (code.equals("altrimenti")){
      System.out.print("else");
    }
    else if (code.equals("intero")){
      System.out.print("int");
    }
    else if (code.equals("stringa")){
      System.out.print("string ");
    }
    else if (code.equals("booleano")){
      System.out.print("boolean ");
    }
    else if (code.equals(";")){
      System.out.println(";");
    }
    else {
      System.out.print(" " + code);
    }    
  }
}
