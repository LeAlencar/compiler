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
    if(ifelse()){
      if (token.getTipo().equals("EOF")){
        System.out.println("\nSintaticamente correta");
        return;
      }
      else{
        erro();
      }
    }
    erro();
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

  private boolean ifelse() {
    if (matchT("IF") && condicao() && bloco() && matchT("ELSE") && bloco()){
      return true;
    }
    return false;
  }

  private boolean bloco() {
    if (matchT("LBRACE")) {
      if (id() && operadorAtribuicao() && num() && matchT("RBRACE")) {
        return true;
      }
      return false;
    }
    return id() && operadorAtribuicao() && num();
  }

  private boolean operadorAtribuicao() {
    if(matchT("ASSIGN")){
      return true;
    }
    return false;
  }

  private boolean condicao() {
    if (matchT("LPAREN")) {
      if (id() && operador() && num() && matchT("RPAREN")) {
        return true;
      }
      return false;
    }
    return id() && operador() && num();
  }

  private boolean operador() {
    if (matchT("GTR") || matchT("LSS") || matchT("EQL") || 
        matchT("GEQ") || matchT("LEQ") || matchT("NEQ")) {
      return true;
    }
    return false;
  }

  private boolean id() {
    if (matchT("ID")){
      return true;
    }
    return false;
  }

  private boolean num() {
    if(matchT("NUM")){
      return true;
    }
    return false;
  }

  private boolean matchL(String palavra){
    if (token.getLexema().equals(palavra)){
      token = getNextToken();
      return true;
    }
    return false;
  }

  private boolean matchT(String palavra){
    if (token.getTipo().equals(palavra)){
      token = getNextToken();
      return true;
    }
    return false;
  }
}
