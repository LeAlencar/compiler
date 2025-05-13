package compiler.parser;

import compiler.lexer.Token;

import java.util.List;

public class Parser {

  private List<Token> tokens;
  private Token token;
  private int currentTokenIndex;
  private String lastToken = "";
  private boolean isPrintln = false;
  private boolean isScanln = false;
  private boolean isFare = false;
  private boolean isMentre = false;
  private boolean isPer = false;
  private int semicolons = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    this.currentTokenIndex = 0;
  }

  public void main() {
    token = getNextToken();
    Node root = new Node("main");
    Tree tree = new Tree(root);

    System.out.println("package main");
    System.out.println("import (");
    System.out.println("\"fmt\"");
    System.out.println(")");
    System.out.println("func main() {");

    loop(root, tree);
  }

  public void loop(Node root, Tree tree) {
    if (bloco(root)) {
      if (token.getTipo().equals("EOF")) {
        System.out.println("}");

        // tree.preOrder();
        // tree.printCode();
        // tree.printTree();
        return;
      } else {
        loop(root, tree);
      }
    } else {
      erro();
    }
  }

  private Token getNextToken() {
    if (currentTokenIndex < tokens.size()) {
      return tokens.get(currentTokenIndex++);
    }
    return null;
  }

  private void erro() {
    System.out.println("\ntoken inválido: " + token.getLexema());
  }

  // BLOCO
  // --------------------------------------------------------------------------------------------------------------------------------------------
  private boolean bloco(Node node) {
    Node bloco = new Node("bloco");
    if (linha(bloco)) {
      if (token != null && !token.getTipo().equals("RBRACE")) {
        bloco(bloco);
      }
      node.addNode(bloco);
      return true;
    }
    return false;
  }

  // LINHA
  // --------------------------------------------------------------------------------------------------------------------------------------------
  private boolean linha(Node node) {
    Node linha = new Node("linha");
    if (escrever(linha) || ler(linha) || (declaracao(linha) && matchL(";", token.getLexema(), linha)) ||
        sealtrimenti(linha) || mentrefare(linha) || per(linha) ||
        farementre(linha) || (atribuicao(linha) && matchL(";", token.getLexema(), linha))) {
      node.addNode(linha);
      return true;
    }
    return false;
  }

  // LER
  // --------------------------------------------------------------------------------------------------------------------------------------------
  private boolean ler(Node node) {
    Node leitura = new Node("leitura");
    if (matchL("leggere", token.getLexema(), leitura) &&
        matchL("xD", token.getLexema(), leitura) &&
        matchT("TEXTO", token.getLexema(), leitura)) {

      if (token != null && token.getTipo().equals("ID")) {
        matchT("ID", token.getLexema(), leitura);
      }

      if (matchL(";", token.getLexema(), leitura)) {
        node.addNode(leitura);
        return true;
      }
    }
    return false;
  }

  // ESCREVER
  // --------------------------------------------------------------------------------------------------------------------------------------------
  private boolean escrever(Node node) {
    Node escrever = new Node("escrever");
    if (matchL("carattere", token.getLexema(), escrever) &&
        matchL("<<", token.getLexema(), escrever)) {

      if (token.getTipo().equals("TEXTO")) {
        matchT("TEXTO", token.getLexema(), escrever);
      } else {
        matchL("$", token.getLexema(), escrever);
        matchT("ID", token.getLexema(), escrever);
      }

      while (token != null && token.getTipo().equals("PERIOD")) {
        matchL(".", token.getLexema(), escrever);
        if (token.getTipo().equals("$")) {
          matchL("$", token.getLexema(), escrever);
          matchT("ID", token.getLexema(), escrever);
        } else {
          matchT("TEXTO", token.getLexema(), escrever);
        }
      }

      if (matchL(";", token.getLexema(), escrever)) {
        node.addNode(escrever);
        return true;
      }
    }
    return false;
  }

  // DECLARAÇÃO
  // --------------------------------------------------------------------------------------------------------------------------------------------
  private boolean declaracao(Node node) {
    Node declaracao = new Node("declaracao");
    if ((matchL("intero", token.getLexema(), declaracao) ||
        matchL("stringa", token.getLexema(), declaracao) ||
        matchL("booleano", token.getLexema(), declaracao)) &&
        matchT("ID", token.getLexema(), declaracao) &&
        operadorAtribuicao(declaracao)) {

      if (expressao(declaracao)) {
        node.addNode(declaracao);
        return true;
      }
    }
    return false;
  }

  // EXPRESSÃO
  // --------------------------------------------------------------------------------------------------------------------------------------------
  private boolean expressao(Node node) {
    Node expressao = new Node("expressao");
    if (fator(expressao)) {
      if (opMat(expressao)) {
        if (fator(expressao)) {
          node.addNode(expressao);
          return true;
        }
      } else {
        node.addNode(expressao);
        return true;
      }
    }
    return false;
  }

  private boolean fator(Node node) {
    Node fator = new Node("fator");
    if (matchT("ID", token.getLexema(), fator) ||
        matchT("NUM", token.getLexema(), fator) ||
        matchT("TEXTO", token.getLexema(), fator) ||
        (matchL("(", token.getLexema(), fator) && expressao(fator) && matchL(")", token.getLexema(), fator))) {
      node.addNode(fator);
      return true;
    } else {
      return false;
    }
  }

  // SE
  // --------------------------------------------------------------------------------------------------------------------------------------------
  private boolean sealtrimenti(Node node) {
    Node sealtrimenti = new Node("sealtrimenti");
    if (matchL("se", token.getLexema(), sealtrimenti) &&
        condicao(sealtrimenti) &&
        matchL("{", token.getLexema(), sealtrimenti) &&
        bloco(sealtrimenti) &&
        matchL("}", token.getLexema(), sealtrimenti)) {

      if (token != null && token.getTipo().equals("ELSE")) {
        matchL("altrimenti", token.getLexema(), sealtrimenti);
        matchL("{", token.getLexema(), sealtrimenti);
        bloco(sealtrimenti);
        matchL("}", token.getLexema(), sealtrimenti);
      }

      node.addNode(sealtrimenti);
      return true;
    }
    return false;
  }

  private boolean mentrefare(Node node) {
    Node mentrefare = new Node("mentrefare");
    if (matchL("mentre", token.getLexema(), mentrefare) &&
        condicao(mentrefare) &&
        matchL("{", token.getLexema(), mentrefare) &&
        bloco(mentrefare) &&
        matchL("}", token.getLexema(), mentrefare)) {
      node.addNode(mentrefare);
      return true;
    }
    return false;
  }

  private boolean farementre(Node node) {
    Node farementre = new Node("farementre");
    if (matchL("fare", token.getLexema(), farementre) &&
        matchL("{", token.getLexema(), farementre) &&
        bloco(farementre) &&
        matchL("}", token.getLexema(), farementre) &&
        matchL("mentre", token.getLexema(), farementre) &&
        condicao(farementre) &&
        matchL(";", token.getLexema(), farementre)) {
      node.addNode(farementre);
      return true;
    }
    return false;
  }

  private boolean per(Node node) {
    Node per = new Node("per");
    if (matchL("per", token.getLexema(), per) &&
        matchL("(", token.getLexema(), per) &&
        declaracao(per) &&
        matchL(";", token.getLexema(), per) &&
        condicao(per) &&
        matchL(";", token.getLexema(), per) &&
        atribuicao(per) &&
        matchL(";", token.getLexema(), per) &&
        matchL(")", token.getLexema(), per) &&
        matchL("{", token.getLexema(), per) &&
        bloco(per) &&
        matchL("}", token.getLexema(), per)) {
      node.addNode(per);
      return true;
    }
    return false;
  }

  private boolean atribuicao(Node node) {
    Node atribuicao = new Node("atribuicao");
    if (matchT("ID", token.getLexema(), atribuicao) &&
        operadorAtribuicao(atribuicao)) {

      if (expressao(atribuicao)) {
        node.addNode(atribuicao);
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  private boolean operadorAtribuicao(Node node) {
    Node operador = new Node("operador");
    if (matchL("=", token.getLexema(), operador) ||
        matchL("+=", token.getLexema(), operador) ||
        matchL("-=", token.getLexema(), operador) ||
        matchL("*=", token.getLexema(), operador) ||
        matchL("/=", token.getLexema(), operador) ||
        matchL("%=", token.getLexema(), operador)) {
      node.addNode(operador);
      return true;
    }
    return false;
  }

  private boolean condicao(Node node) {
    Node condicao = new Node("condicao");
    boolean hasParentheses = false;

    if (token.getTipo().equals("LPAREN")) {
      matchL("(", token.getLexema(), condicao);
      hasParentheses = true;
    }

    if ((matchT("ID", token.getLexema(), condicao) ||
        matchT("NUM", token.getLexema(), condicao)) &&
        opRelacional(condicao) &&
        (matchT("ID", token.getLexema(), condicao) ||
            matchT("NUM", token.getLexema(), condicao))) {

      if (token.getTipo().equals("OR") || token.getTipo().equals("AND")) {
        matchL(token.getTipo().equals("OR") ? "o" : "e", token.getLexema(), condicao);
        condicao(condicao);
      }

      if (hasParentheses) {
        matchL(")", token.getLexema(), condicao);
      }

      node.addNode(condicao);
      return true;
    }
    return false;
  }

  private boolean opMat(Node node) {
    Node opMat = new Node("opMat");
    if (matchL("+", token.getLexema(), opMat) ||
        matchL("-", token.getLexema(), opMat) ||
        matchL("*", token.getLexema(), opMat) ||
        matchL("/", token.getLexema(), opMat) ||
        matchL("%", token.getLexema(), opMat)) {
      node.addNode(opMat);
      return true;
    }
    return false;
  }

  private boolean opRelacional(Node node) {
    Node operadorRelacional = new Node("operadorRelacional");
    if (matchL(">", token.getLexema(), operadorRelacional) ||
        matchL("<", token.getLexema(), operadorRelacional) ||
        matchL("==", token.getLexema(), operadorRelacional) ||
        matchL("!=", token.getLexema(), operadorRelacional) ||
        matchL(">=", token.getLexema(), operadorRelacional) ||
        matchL("<=", token.getLexema(), operadorRelacional)) {
      node.addNode(operadorRelacional);
      return true;
    }
    return false;
  }

  private boolean matchL(String palavra, String newcode, Node node) {
    if (token.getLexema().equals(palavra)) {
      traduz(newcode);
      node.addNode(newcode);
      token = getNextToken();
      return true;
    }
    return false;
  }

  private boolean matchT(String palavra, String newcode, Node node) {
    if (token.getTipo().equals(palavra)) {
      traduz(newcode);
      node.addNode(newcode);
      token = getNextToken();
      return true;
    }
    return false;
  }

  private void traduz(String code) {
    if (code.equals("se")) {
      System.out.print("if ");
      lastToken = "se";
    } else if (code.equals("altrimenti")) {
      System.out.print("else ");
      lastToken = "altrimenti";
    } else if (code.equals("intero")) {
      lastToken = "intero";
    } else if (code.equals("galleggiante")) {
      System.out.print("float64 ");
      lastToken = "galleggiante";
    } else if (code.equals("stringa")) {
      lastToken = "stringa";
    } else if (code.equals("booleano")) {
      System.out.print("bool ");
      lastToken = "booleano";
    } else if (code.equals("mentre")) {
      if (isFare) {
        System.out.print("if ");
        isFare = false;
        isMentre = true;
      } else {
        System.out.print("for ");
        lastToken = "mentre";
      }
    } else if (code.equals("fare")) {
      System.out.print("for ");
      lastToken = "fare";
      isFare = true;
    } else if (code.equals("per")) {
      System.out.print("for ");
      isPer = true;
      lastToken = "per";
    } else if (code.equals("funzione")) {
      System.out.print("func ");
      lastToken = "funzione";
    } else if (code.equals("fermare")) {
      System.out.print("return ");
      lastToken = "fermare";
    } else if (code.equals("carattere")) {
      System.out.print("fmt.Println");
      lastToken = "carattere";
      isPrintln = true;
    } else if (code.equals("leggere")) {
      System.out.print("fmt.Print");
      lastToken = "leggere";
      isScanln = true;
    } else if (code.equals(";")) {
      if (isScanln) {
        if (lastToken.endsWith("\"")) {
          System.out.println(")");
          System.out.print("fmt.Scanln()");
        } else if (lastToken.startsWith("_")) {
          System.out.println(")");
          isScanln = false;
        } else {
          System.out.println(")");
          isScanln = false;
        }
        isPrintln = false;
      } else if (isPrintln) {
        System.out.println(")");
        isPrintln = false;
      } else if (isMentre) {
        System.out.println("{");
        System.out.println("break");
        System.out.println("}");
        System.out.println("}");
        isMentre = false;
      } else if (isPer) {
        if (semicolons < 2) {
          System.out.print("; ");
          semicolons++;
        }
      } else {
        System.out.println();
      }
      lastToken = ";";
    } else if (code.equals("{")) {
      System.out.println(" {");
      lastToken = "{";
    } else if (code.equals("}")) {
      if (!isFare) {
        System.out.println("}");
      }
      lastToken = "}";
    } else if (code.equals("(")) {
      if (!isPer) {
        System.out.print("(");
      }
      lastToken = "(";
    } else if (code.equals(")")) {
      if (!isPer) {
        System.out.print(")");
      } else {
        semicolons = 0;
        isPer = false;
      }
      lastToken = ")";
    } else if (code.equals("<<")) {
      System.out.print("(");
      lastToken = "<<";
    } else if (code.equals(".")) {
      System.out.print(", ");
      lastToken = ".";
    } else if (code.equals("xD")) {

    } else if (code.equals("o")) {
      System.out.print(" || ");
      lastToken = "o";
    } else if (code.equals("e")) {
      System.out.print(" && ");
      lastToken = "e";
    } else if (code.equals("=")) {
      System.out.print(" := ");
      lastToken = "=";
    } else if (code.equals("$")) {
      System.out.print("");
      lastToken = "$";
    } else if (code.equals("+=")) {
      System.out.print(" += ");
      lastToken = "+=";
    } else if (code.equals("-=")) {
      System.out.print(" -= ");
      lastToken = "-=";
    } else if (code.equals("*=")) {
      System.out.print(" *= ");
      lastToken = "*=";
    } else if (code.equals("/=")) {
      System.out.print(" /= ");
      lastToken = "/=";
    } else if (code.equals("%=")) {
      System.out.print(" %= ");
      lastToken = "%=";
    } else if (code.equals(">")) {
      if (isFare) {
        System.out.print(" <= ");
      } else {
        System.out.print(" > ");
      }
      lastToken = ">";
    } else if (code.equals("<")) {
      if (isFare) {
        System.out.print(" >= ");
      } else {
        System.out.print(" < ");
      }
      lastToken = "<";
    } else if (code.equals("==")) {
      if (isFare) {
        System.out.print(" != ");
      } else {
        System.out.print(" == ");
      }
      lastToken = "==";
    } else if (code.equals("!=")) {
      if (isFare) {
        System.out.print(" == ");
      } else {
        System.out.print(" != ");
      }
      lastToken = "!=";
    } else if (code.equals(">=")) {
      if (isFare) {
        System.out.print(" < ");
      } else {
        System.out.print(" >= ");
      }
      lastToken = ">=";
    } else if (code.equals("<=")) {
      if (isFare) {
        System.out.print(" > ");
      } else {
        System.out.print(" <= ");
      }
      lastToken = "<=";
    } else if (code.equals("+")) {
      System.out.print(" + ");
      lastToken = "+";
    } else if (code.equals("-")) {
      System.out.print(" - ");
      lastToken = "-";
    } else if (code.equals("*")) {
      System.out.print(" * ");
      lastToken = "*";
    } else if (code.equals("/")) {
      System.out.print(" / ");
      lastToken = "/";
    } else if (code.equals("%")) {
      System.out.print(" % ");
      lastToken = "%";
    } else {
      if (code.startsWith("_")) {
        if (isScanln) {
          System.out.print("&" + code.substring(1));
        } else {
          System.out.print(code.substring(1));
        }
      } else if (code.endsWith("\"") && lastToken.equals("leggere")) {
        System.out.print("(" + code + ")");
        System.out.println();
        System.out.print("fmt.Scanln(");
      } else {
        System.out.print(code);
      }
      lastToken = code;
    }
  }
}
