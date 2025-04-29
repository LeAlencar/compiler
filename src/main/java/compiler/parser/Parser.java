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
    Node root = new Node("main");
    Tree tree = new Tree(root);
    if (statements(root)) {
      if (token.getTipo().equals("EOF")) {
        System.out.println("\nSintaticamente correta");
        tree.printTree();
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

  private boolean statements(Node node) {
    Node statements = node.addNode("statements");
    while (statement(statements)) {
      // Continue processing statements
      if (token.getTipo().equals("EOF")) {
        return true;
      }
    }
    return false;
  }

  private boolean statement(Node node) {
    Node statement = node.addNode("statement");
    if (declaracao(statement)) {
      System.out.println();
      return true;
    } else if (ifelse(statement)) {
      System.out.println();
      return true;
    } else if (whileLoop(statement)) {
      System.out.println();
      return true;
    } else if (doWhileLoop(statement)) {
      System.out.println();
      return true;
    } else if (forLoop(statement)) {
      System.out.println();
      return true;
    } else if (expressao(statement) && matchT("SEMICOLON", ";", statement)) {
      System.out.println();
      return true;
    }
    return false;
  }

  private boolean declaracao(Node node) {
    Node declaracao = node.addNode("declaracao");
    if ((matchT("TYPE_INT", token.getLexema(), declaracao) || matchT("TYPE_STRING", token.getLexema(), declaracao) ||
        matchT("TYPE_BOOL", token.getLexema(), declaracao)) && id(declaracao) && 
        operadorAtribuicao(declaracao) && (num(declaracao) || id(declaracao))
        && matchT("SEMICOLON", ";", declaracao)) {
      return true;
    }
    return false;
  }

  private boolean ifelse(Node node) {
    Node ifelse = node.addNode("ifelse");
    if (matchT("IF", token.getLexema(), ifelse) && condicao(ifelse) && bloco(ifelse) && matchT("ELSE", token.getLexema(), ifelse) && bloco(ifelse)) {
      return true;
    }
    return false;
  }

  private boolean bloco(Node node) {
    Node bloco = node.addNode("bloco");
    if (matchT("LBRACE", token.getLexema(), bloco)) {
      while (statement(bloco) && !token.getTipo().equals("RBRACE")) {
        // Continue processing statements
      }
      return matchT("RBRACE", token.getLexema(), bloco);
    }
    return statement(bloco);
  }

  private boolean operadorAtribuicao(Node node) {
    Node operadorAtribuicao = node.addNode("operadorAtribuicao");
    return matchT("ASSIGN", token.getLexema(), operadorAtribuicao) ||
        matchT("ADD_ASSIGN", token.getLexema(), operadorAtribuicao) ||
        matchT("SUB_ASSIGN", token.getLexema(), operadorAtribuicao) ||
        matchT("MUL_ASSIGN", token.getLexema(), operadorAtribuicao) ||
        matchT("DIV_ASSIGN", token.getLexema(), operadorAtribuicao) ||
        matchT("MOD_ASSIGN", token.getLexema(), operadorAtribuicao);
  }

  private boolean condicao(Node node) {
    Node condicao = node.addNode("condicao");
    if (matchT("LPAREN", token.getLexema(), condicao)) {
      if (expressaoComparativa(condicao)) {
        if (matchT("RPAREN", token.getLexema(), condicao)) {
          return true;
        }
      }
      return false;
    }
    return expressaoComparativa(condicao);
  }

  private boolean expressaoComparativa(Node node) {
    Node expressaoComparativa = node.addNode("expressaoComparativa");
    if (termo(expressaoComparativa)) {
      if (operador()) {
        if (termo(expressaoComparativa)) {
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

  private boolean id(Node node) {
    Node id = node.addNode("id");
    if (matchT("ID", token.getLexema(), id)) {
      return true;
    }
    return false;
  }

  private boolean num(Node node) {
    Node num = node.addNode("num");
    if (matchT("NUM", token.getLexema(), num)) {
      return true;
    }
    return false;
  }

  private boolean whileLoop(Node node) {
    Node whileLoop = node.addNode("whileLoop");
    if (matchT("WHILE", token.getLexema(), whileLoop)) {
      // Check if we have a left parenthesis
      if (matchT("LPAREN", token.getLexema(), whileLoop)) {
        // Handle condition with comparison operator
        if (termo(whileLoop)) {
          if (token != null && (token.getTipo().equals("GTR") ||
              token.getTipo().equals("LSS") ||
              token.getTipo().equals("EQL") ||
              token.getTipo().equals("NEQ") ||
              token.getTipo().equals("GEQ") ||
              token.getTipo().equals("LEQ"))) {
            traduz(token.getLexema());
            token = getNextToken();
            if (termo(whileLoop) && matchT("RPAREN", token.getLexema(), whileLoop) && bloco(whileLoop)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private boolean doWhileLoop(Node node) {
    Node doWhileLoop = node.addNode("doWhileLoop");
    if (matchT("DO", token.getLexema(), doWhileLoop) &&
        bloco(doWhileLoop) &&
        matchT("WHILE", token.getLexema(), doWhileLoop) &&
        matchT("LPAREN", token.getLexema(), doWhileLoop)) {

      // Handle condition with comparison operator
      if (termo(doWhileLoop)) {
        if (token != null && (token.getTipo().equals("GTR") ||
            token.getTipo().equals("LSS") ||
            token.getTipo().equals("EQL") ||
            token.getTipo().equals("NEQ") ||
            token.getTipo().equals("GEQ") ||
            token.getTipo().equals("LEQ"))) {
          traduz(token.getLexema());
          token = getNextToken();
          if (termo(doWhileLoop) &&
              matchT("RPAREN", token.getLexema(), doWhileLoop) &&
              matchT("SEMICOLON", ";", doWhileLoop)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean forLoop(Node node) {
    Node forLoop = node.addNode("forLoop");
    if (matchT("FOR", token.getLexema(), forLoop) &&
        matchT("LPAREN", token.getLexema(), forLoop) &&
        (declaracao(forLoop) || (expressao(forLoop) && matchT("SEMICOLON", ";", forLoop)))) {

      // Handle condition with comparison operator
      if (termo(forLoop)) {
        if (token != null && (token.getTipo().equals("GTR") ||
            token.getTipo().equals("LSS") ||
            token.getTipo().equals("EQL") ||
            token.getTipo().equals("NEQ") ||
            token.getTipo().equals("GEQ") ||
            token.getTipo().equals("LEQ"))) {
          traduz(token.getLexema());
          token = getNextToken();
          if (termo(forLoop) &&
              matchT("SEMICOLON", ";", forLoop) &&
              expressao(forLoop) &&
              matchT("RPAREN", token.getLexema(), forLoop) &&
              bloco(forLoop)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean expressao(Node node) {
    Node expressao = node.addNode("expressao");
    if (atribuicao(expressao)) {
      return true;
    } else if (expressaoComparativa(expressao)) {
      return true;
    } else if (expressaoMatematica(expressao)) {
      return true;
    }
    return false;
  }

  private boolean atribuicao(Node node) {
    Node atribuicao = node.addNode("atribuicao");
    if (id(atribuicao)) {
      if (operadorAtribuicao(atribuicao)) {
        if (expressaoMatematica(atribuicao)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean expressaoMatematica(Node node) {
    Node expressaoMatematica = node.addNode("expressaoMatematica");
    if (termo(expressaoMatematica)) {
      if (token != null && (token.getTipo().equals("ADD") ||
          token.getTipo().equals("SUB") ||
          token.getTipo().equals("MUL") ||
          token.getTipo().equals("DIV") ||
          token.getTipo().equals("MOD"))) {
        traduz(token.getLexema());
        token = getNextToken();
        return termo(expressaoMatematica);
      }
      return true;
    }
    return false;
  }

  private boolean termo(Node node) {
    Node termo = node.addNode("termo");
    return id(termo) || num(termo);
  }

  private boolean matchT(String tipo, String lexema, Node node) {
    if (token.getTipo().equals(tipo)) {
      traduz(lexema);
      node.addNode(lexema);
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
