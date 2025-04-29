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
    if(sealtrimenti(root) || mentrefare(root) || declaracao(root)){
      if (token.getTipo().equals("EOF")){
        System.out.println("\nSintaticamente correta\n");
        tree.preOrder();
        tree.printCode();
        tree.printTree();

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
    System.out.println("\ntoken inválido: " + token.getLexema());
  }

  private boolean sealtrimenti(Node node) {
    Node sealtrimenti = new Node("sealtrimenti");
    if (matchL("se", token.getLexema(), sealtrimenti) && matchL("(", token.getLexema(), sealtrimenti) && condicao(sealtrimenti) && 
    matchL(")", token.getLexema(), sealtrimenti) && matchL("{", token.getLexema(), sealtrimenti) && bloco(sealtrimenti) && 
    matchL("}", token.getLexema(), sealtrimenti) && matchL("altrimenti", token.getLexema(), sealtrimenti) && 
    matchL("{", token.getLexema(), sealtrimenti) && bloco(sealtrimenti) && matchL("}", token.getLexema(), sealtrimenti)){
      node.addNode(sealtrimenti);
      return true;
    }
      return false;
  }

  private boolean mentrefare(Node node) {
    Node mentrefare = new Node("mentrefare");
    if (matchL("mentre", token.getLexema(), mentrefare) && condicao(mentrefare) && bloco(mentrefare) && 
    matchL("fare", token.getLexema(), mentrefare) && bloco(mentrefare)){
      node.addNode(mentrefare);
      return true;
    }
    return false;
  }

  private boolean declaracao(Node node) {
    Node declaracao = new Node("declaracao");
    if ((matchL("intero", token.getLexema(), declaracao) || matchL("stringa", token.getLexema(), declaracao) || 
    matchL("booleano", token.getLexema(), declaracao)) && id(declaracao) && operadorAtribuicao(declaracao) && 
    (num(declaracao) || id(declaracao) || matchL("true", token.getLexema(), declaracao) || matchL("false", token.getLexema(), declaracao)) && 
    matchL(";", token.getLexema(), declaracao)){
      node.addNode(declaracao);
      return true;
    }
    return false;
  }

  private boolean bloco(Node node) {
    Node bloco = new Node("bloco");
    if (id(bloco) && (operadorAtribuicao(bloco) || operadorAritmetico(bloco)) && (num(bloco) || id(bloco))){
      node.addNode(bloco);
      return true;
    }

    return false;
  }

  private boolean operadorAtribuicao(Node node) {
    Node operador = new Node("operador");
    if(matchL("+=", token.getLexema(), operador) || 
    matchL("-=", token.getLexema(), operador) || matchL("*=", token.getLexema(), operador) || 
    matchL("/=", token.getLexema(), operador) || matchL("%=", token.getLexema(), operador)){
      node.addNode(operador);
      return true;
    }

    return false;
  }

  private boolean condicao(Node node) {
    Node condicao = new Node("condicao");
    if(id(condicao) && operadorRelacional(condicao) && (num(condicao) || id(condicao))){
      node.addNode(condicao);
      return true;
    }
    return false;
  }

  private boolean operadorAritmetico(Node node) {
    Node operadorAritmetico = new Node("operadorAritmetico");
    if (matchL("+", token.getLexema(), operadorAritmetico) || matchL("-", token.getLexema(), operadorAritmetico) || 
    matchL("=", token.getLexema(), operadorAritmetico) || matchT("MUL", token.getLexema(), operadorAritmetico) || 
    matchT("DIV", token.getLexema(), operadorAritmetico) || matchT("MOD", token.getLexema(), operadorAritmetico)){
      node.addNode(operadorAritmetico);
      return true;
    }
    return false;
  }

  private boolean operadorRelacional(Node node) {
    Node operadorRelacional = new Node("operadorRelacional");
    if (matchL(">", token.getLexema(), operadorRelacional) || matchL("<", token.getLexema(), operadorRelacional) || 
    matchL("==", token.getLexema(), operadorRelacional) || matchL("!=", token.getLexema(), operadorRelacional) || 
    matchL(">=", token.getLexema(), operadorRelacional) || matchL("<=", token.getLexema(), operadorRelacional)){
      node.addNode(operadorRelacional);
      return true;
    }
    return false;
  }

  private boolean id(Node node) {
    Node id = new Node("id");
    if (matchT("ID", token.getLexema(), id)){
      node.addNode(id);
      return true;
    }
    return false;
  }

  private boolean num(Node node) {
    Node num = new Node("num");
    if(matchT("NUM", token.getLexema(), num)){
      node.addNode(num);
      return true;
    }
    return false;
  }

  private boolean matchL(String palavra, String newcode, Node node){
    if (token.getLexema().equals(palavra)){
      traduz(newcode);
      node.addNode(newcode);
      token = getNextToken();
      return true;
    }
    return false;
  }

  private boolean matchT(String palavra, String newcode, Node node){
    if (token.getTipo().equals(palavra)){
      traduz(newcode);
      node.addNode(newcode);
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
