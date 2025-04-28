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
    if (statements()) {
      if (token.getTipo().equals("EOF")) {
        System.out.println("\nSintaticamente correta");
        return;
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

  private boolean statements() {
    while (statement()) {
      // Continue processing statements
      if (token.getTipo().equals("EOF")) {
        return true;
      }
    }
    return false;
  }

  private boolean statement() {
    if (declaracao()) {
      System.out.println();
      return true;
    } else if (ifelse()) {
      System.out.println();
      return true;
    } else if (whileLoop()) {
      System.out.println();
      return true;
    } else if (doWhileLoop()) {
      System.out.println();
      return true;
    } else if (forLoop()) {
      System.out.println();
      return true;
    } else if (expressao() && matchT("SEMICOLON", ";")) {
      System.out.println();
      return true;
    }
    return false;
  }

  private boolean declaracao() {
    if ((matchT("TYPE_INT", token.getLexema()) || matchT("TYPE_STRING", token.getLexema()) ||
        matchT("TYPE_BOOL", token.getLexema())) && id() && matchT("ASSIGN", token.getLexema()) && (num() || id())
        && matchT("SEMICOLON", ";")) {
      return true;
    }
    return false;
  }

  private boolean ifelse() {
    if (matchT("IF", token.getLexema()) && condicao() && bloco() && matchT("ELSE", token.getLexema()) && bloco()) {
      return true;
    }
    return false;
  }

  private boolean bloco() {
    if (matchT("LBRACE", token.getLexema())) {
      while (statement() && !token.getTipo().equals("RBRACE")) {
        // Continue processing statements
      }
      return matchT("RBRACE", token.getLexema());
    }
    return statement();
  }

  private boolean operadorAtribuicao() {
    return matchT("ASSIGN", token.getLexema()) ||
        matchT("ADD_ASSIGN", token.getLexema()) ||
        matchT("SUB_ASSIGN", token.getLexema()) ||
        matchT("MUL_ASSIGN", token.getLexema()) ||
        matchT("DIV_ASSIGN", token.getLexema()) ||
        matchT("MOD_ASSIGN", token.getLexema());
  }

  private boolean condicao() {
    if (matchT("LPAREN", token.getLexema())) {
      if (expressaoComparativa()) {
        if (matchT("RPAREN", token.getLexema())) {
          return true;
        }
      }
      return false;
    }
    return expressaoComparativa();
  }

  private boolean expressaoComparativa() {
    if (termo()) {
      if (operador()) {
        if (termo()) {
          return true;
        }
        return false;
      }
      return true;
    }
    return false;
  }

  private boolean operador() {
    if (token != null && (token.getTipo().equals("GTR") ||
        token.getTipo().equals("LSS") ||
        token.getTipo().equals("EQL") ||
        token.getTipo().equals("NEQ") ||
        token.getTipo().equals("GEQ") ||
        token.getTipo().equals("LEQ"))) {
      traduz(token.getLexema());
      token = getNextToken();
      return true;
    }
    return false;
  }

  private boolean id() {
    if (matchT("ID", token.getLexema())) {
      return true;
    }
    return false;
  }

  private boolean num() {
    if (matchT("NUM", token.getLexema())) {
      return true;
    }
    return false;
  }

  private boolean whileLoop() {
    if (matchT("WHILE", token.getLexema()) &&
        matchT("LPAREN", token.getLexema())) {
      // Special handling for condition with > operator
      if (termo()) {
        if (token != null && token.getTipo().equals("GTR")) {
          traduz(token.getLexema());
          token = getNextToken();
          if (termo() && matchT("RPAREN", token.getLexema()) && bloco()) {
            return true;
          }
        } else {
          // Regular condition processing
          if (operador() && termo() && matchT("RPAREN", token.getLexema()) && bloco()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean doWhileLoop() {
    if (matchT("DO", token.getLexema()) &&
        bloco() &&
        matchT("WHILE", token.getLexema()) &&
        matchT("LPAREN", token.getLexema())) {

      // Handle condition with comparison operator
      if (termo()) {
        if (token != null && (token.getTipo().equals("GTR") ||
            token.getTipo().equals("LSS") ||
            token.getTipo().equals("EQL") ||
            token.getTipo().equals("NEQ") ||
            token.getTipo().equals("GEQ") ||
            token.getTipo().equals("LEQ"))) {
          traduz(token.getLexema());
          token = getNextToken();
          if (termo() &&
              matchT("RPAREN", token.getLexema()) &&
              matchT("SEMICOLON", ";")) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean forLoop() {
    if (matchT("FOR", token.getLexema()) &&
        matchT("LPAREN", token.getLexema()) &&
        (declaracao() || (expressao() && matchT("SEMICOLON", ";")))) {

      // Handle condition with comparison operator
      if (termo()) {
        if (token != null && (token.getTipo().equals("GTR") ||
            token.getTipo().equals("LSS") ||
            token.getTipo().equals("EQL") ||
            token.getTipo().equals("NEQ") ||
            token.getTipo().equals("GEQ") ||
            token.getTipo().equals("LEQ"))) {
          traduz(token.getLexema());
          token = getNextToken();
          if (termo() &&
              matchT("SEMICOLON", ";") &&
              expressao() &&
              matchT("RPAREN", token.getLexema()) &&
              bloco()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean expressao() {
    if (atribuicao()) {
      return true;
    } else if (expressaoComparativa()) {
      return true;
    } else if (expressaoMatematica()) {
      return true;
    }
    return false;
  }

  private boolean atribuicao() {
    if (id()) {
      if (operadorAtribuicao()) {
        if (expressaoMatematica()) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean expressaoMatematica() {
    if (termo()) {
      if (token != null && (token.getTipo().equals("ADD") ||
          token.getTipo().equals("SUB") ||
          token.getTipo().equals("MUL") ||
          token.getTipo().equals("DIV") ||
          token.getTipo().equals("MOD"))) {
        traduz(token.getLexema());
        token = getNextToken();
        return termo();
      }
      return true;
    }
    return false;
  }

  private boolean termo() {
    return id() || num();
  }

  private boolean operadorMatematico() {
    return matchT("ADD", token.getLexema()) ||
        matchT("SUB", token.getLexema()) ||
        matchT("MUL", token.getLexema()) ||
        matchT("DIV", token.getLexema()) ||
        matchT("MOD", token.getLexema());
  }

  private boolean matchL(String palavra, String newcode) {
    if (token.getLexema().equals(palavra)) {
      traduz(newcode);
      token = getNextToken();
      return true;
    }
    return false;
  }

  private boolean matchT(String tipo, String lexema) {
    if (token.getTipo().equals(tipo)) {
      traduz(lexema);
      token = getNextToken();
      return true;
    }
    return false;
  }

  private void traduz(String code) {
    if (code.equals("se")) {
      System.out.print("if");
    } else if (code.equals("altrimenti")) {
      System.out.print("else");
    } else if (code.equals("intero")) {
      System.out.print("int");
    } else if (code.equals("stringa")) {
      System.out.print("string");
    } else if (code.equals("booleano")) {
      System.out.print("boolean");
    } else if (code.equals("mentre")) {
      System.out.print("while");
    } else if (code.equals("fare")) {
      System.out.print("do");
    } else if (code.equals("per")) {
      System.out.print("for");
    } else if (code.equals(";")) {
      System.out.println(";");
    } else if (code.equals("+=")) {
      System.out.print(" += ");
    } else if (code.equals("-=")) {
      System.out.print(" -= ");
    } else if (code.equals("*=")) {
      System.out.print(" *= ");
    } else if (code.equals("/=")) {
      System.out.print(" /= ");
    } else if (code.equals("%=")) {
      System.out.print(" %= ");
    } else if (code.equals("+")) {
      System.out.print(" + ");
    } else if (code.equals("-")) {
      System.out.print(" - ");
    } else if (code.equals("*")) {
      System.out.print(" * ");
    } else if (code.equals("/")) {
      System.out.print(" / ");
    } else if (code.equals("%")) {
      System.out.print(" % ");
    } else if (code.equals("==")) {
      System.out.print(" == ");
    } else if (code.equals("!=")) {
      System.out.print(" != ");
    } else if (code.equals(">=")) {
      System.out.print(" >= ");
    } else if (code.equals("<=")) {
      System.out.print(" <= ");
    } else if (code.equals(">")) {
      System.out.print(" > ");
    } else if (code.equals("<")) {
      System.out.print(" < ");
    } else if (code.equals("=")) {
      System.out.print(" = ");
    } else if (code.equals("(") || code.equals(")") ||
        code.equals("{") || code.equals("}")) {
      System.out.print(" " + code + " ");
    } else {
      // For identifiers starting with underscore, remove it
      if (code.startsWith("_")) {
        System.out.print(" " + code.substring(1) + " ");
      } else {
        // For other tokens (like numbers), keep them as is
        System.out.print(" " + code + " ");
      }
    }
  }
}
