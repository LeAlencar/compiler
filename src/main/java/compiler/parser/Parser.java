package compiler.parser;

import compiler.lexer.Token;
import compiler.semantic.SymbolTable;
import compiler.semantic.SemanticException;
import compiler.semantic.Symbol;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

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
  private SymbolTable symbolTable;
  private Set<String> usedVariables;
  private Set<String> declaredVariables;
  private StringBuilder generatedCode;
  private boolean isElseComing = false;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    this.currentTokenIndex = 0;
    this.symbolTable = new SymbolTable();
    this.usedVariables = new HashSet<>();
    this.declaredVariables = new HashSet<>();
    this.generatedCode = new StringBuilder();
  }

  public String parse() {
    token = getNextToken();
    Node root = new Node("main");
    Tree tree = new Tree(root);

    appendCode("package main\n");
    appendCode("import (\n");
    appendCode("\t\"fmt\"\n");
    appendCode(")\n\n");
    appendCode("func main() {\n");

    // Track if we're at the start of a new line for indentation
    boolean isNewLine = true;
    boolean isFirstDeclaration = true;

    loop(root, tree);

    // Verificação semântica de variáveis não utilizadas
    try {
      Set<String> allVariables = symbolTable.getAllVariableNames();
      Set<String> unusedVars = new HashSet<>();

      for (String var : allVariables) {
        if (!usedVariables.contains(var)) {
          unusedVars.add(var);
        }
      }

      // Se houver variáveis não utilizadas, lança exceção
      if (!unusedVars.isEmpty()) {
        StringBuilder errorMsg = new StringBuilder("Erro de compilação: variáveis declaradas mas não utilizadas:\n");
        for (String var : unusedVars) {
          errorMsg.append("  - ").append(var).append("\n");
        }
        throw new SemanticException(errorMsg.toString());
      }

      // If we reach here, compilation was successful
      appendCode("}\n");
      System.out.println("\nCódigo compilado com sucesso! Análise sintática e semântica OK.");
      return generatedCode.toString();

    } catch (SemanticException e) {
      System.err.println(e.getMessage());
      System.err.println("\nCompilação falhou devido a erros semânticos.\n");
      throw new RuntimeException("Compilação interrompida devido a erros semânticos");
    }
  }

  private void appendCode(String code) {
    generatedCode.append(code);
  }

  public void loop(Node root, Tree tree) {
    if (token == null || token.getTipo().equals("EOF")) {
      return;
    }

    if (bloco(root)) {
      loop(root, tree);
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
    appendCode("\ntoken inválido: " + token.getLexema() + "\n");
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
    String type = "";
    String varName = "";

    // Captura o tipo
    if (token.getLexema().equals("intero")) {
      type = "int"; // Convertendo para o tipo Go correspondente
      if (!matchL("intero", token.getLexema(), declaracao))
        return false;
    } else if (token.getLexema().equals("stringa")) {
      type = "string"; // Convertendo para o tipo Go correspondente
      if (!matchL("stringa", token.getLexema(), declaracao))
        return false;
    } else if (token.getLexema().equals("booleano")) {
      type = "bool"; // Convertendo para o tipo Go correspondente
      if (!matchL("booleano", token.getLexema(), declaracao))
        return false;
    } else {
      return false;
    }

    // Captura o nome da variável
    if (token.getTipo().equals("ID")) {
      varName = token.getLexema().substring(1); // Remove o underscore inicial
      if (!matchT("ID", token.getLexema(), declaracao))
        return false;
    } else {
      return false;
    }

    try {
      symbolTable.insert(varName, type);

      if (!operadorAtribuicao(declaracao))
        return false;

      if (!expressao(declaracao))
        return false;

      // Marca a variável como inicializada após a expressão
      Symbol symbol = symbolTable.lookup(varName);
      symbol.setInitialized(true);

      // Se estamos dentro de um loop 'per', marca a variável como usada
      // automaticamente
      if (isPer) {
        usedVariables.add(varName);
      }

      node.addNode(declaracao);
      return true;
    } catch (SemanticException e) {
      appendCode("Erro semântico: " + e.getMessage() + "\n");
      return false;
    }
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
    try {
      if (token.getTipo().equals("ID")) {
        String varName = token.getLexema().substring(1); // Remove o underscore inicial
        Symbol symbol = symbolTable.lookup(varName);

        // Verifica se a variável foi inicializada
        if (!symbol.isInitialized()) {
          throw new SemanticException("Erro: variável '" + varName + "' está sendo usada sem ter sido inicializada");
        }

        // Marca a variável como usada
        usedVariables.add(varName);

        if (!matchT("ID", token.getLexema(), fator))
          return false;
      } else if (token.getTipo().equals("NUM")) {
        if (!matchT("NUM", token.getLexema(), fator))
          return false;
      } else if (token.getTipo().equals("TEXTO")) {
        if (!matchT("TEXTO", token.getLexema(), fator))
          return false;
      } else if (matchL("(", token.getLexema(), fator)) {
        if (!expressao(fator) || !matchL(")", token.getLexema(), fator))
          return false;
      } else {
        return false;
      }

      node.addNode(fator);
      return true;
    } catch (SemanticException e) {
      System.err.println("Erro semântico: " + e.getMessage());
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
        matchL("(", token.getLexema(), per)) {

      // Store the current token to get the loop variable name
      Token loopVar = token;
      boolean wasVar = false;

      if (token.getLexema().equals("intero")) {
        wasVar = true;
      }

      if (declaracao(per) &&
          matchL(";", token.getLexema(), per) &&
          condicao(per) &&
          matchL(";", token.getLexema(), per) &&
          atribuicao(per) &&
          matchL(")", token.getLexema(), per) &&
          matchL("{", token.getLexema(), per) &&
          bloco(per) &&
          matchL("}", token.getLexema(), per)) {

        // Mark the loop variable as used since it's part of the loop control
        if (loopVar != null && loopVar.getTipo().equals("ID")) {
          String varName = loopVar.getLexema().substring(1); // Remove o underscore inicial
          usedVariables.add(varName);
        }

        node.addNode(per);
        return true;
      }
    }
    return false;
  }

  private boolean atribuicao(Node node) {
    Node atribuicao = new Node("atribuicao");
    String varName = "";

    if (token.getTipo().equals("ID")) {
      varName = token.getLexema().substring(1); // Remove o underscore inicial
      try {
        Symbol var = symbolTable.lookup(varName);
        if (!matchT("ID", token.getLexema(), atribuicao))
          return false;

        if (!operadorAtribuicao(atribuicao))
          return false;

        if (!expressao(atribuicao))
          return false;

        var.setInitialized(true);
        node.addNode(atribuicao);
        return true;
      } catch (SemanticException e) {
        System.err.println("Erro semântico: " + e.getMessage());
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
      appendCode("\tif ");
      lastToken = "se";
    } else if (code.equals("altrimenti")) {
      appendCode(" else"); // Space before else
      lastToken = "altrimenti";
    } else if (code.equals("intero")) {
      lastToken = "intero";
    } else if (code.equals("galleggiante")) {
      lastToken = "galleggiante";
    } else if (code.equals("stringa")) {
      lastToken = "stringa";
    } else if (code.equals("booleano")) {
      lastToken = "booleano";
    } else if (code.equals("mentre")) {
      appendCode("\tfor ");
      lastToken = "mentre";
    } else if (code.equals("per")) {
      appendCode("\tfor ");
      isPer = true;
      semicolons = 0;
      lastToken = "per";
    } else if (code.equals("carattere")) {
      if (lastToken.equals(";") || lastToken.equals("{")) {
        appendCode("\t\t"); // Double tab for statements inside blocks
      }
      appendCode("fmt.Println");
      lastToken = "carattere";
      isPrintln = true;
    } else if (code.equals(";")) {
      if (isPrintln) {
        appendCode(")\n");
        isPrintln = false;
      } else if (isPer && semicolons < 2) {
        appendCode("; "); // Semicolon and space for for-loop separators
        semicolons++;
      } else {
        appendCode("\n"); // Just a newline for other cases
      }
      lastToken = ";";
    } else if (code.equals("{")) {
      appendCode(" {\n"); // Space before brace, newline after
      lastToken = "{";
    } else if (code.equals("}")) {
      if (token != null && token.getLexema().equals("altrimenti")) {
        appendCode("\t}"); // Tab before closing brace, no newline
      } else {
        appendCode("\t}\n");
      }
      lastToken = "}";
    } else if (code.equals("(")) {
      if (!isPer) {
        appendCode("(");
      }
      lastToken = "(";
    } else if (code.equals(")")) {
      if (!isPer) {
        appendCode(")");
      }
      lastToken = ")";
    } else if (code.equals("<<")) {
      appendCode("(");
      lastToken = "<<";
    } else if (code.equals("=")) {
      String varName = "";
      if (currentTokenIndex >= 2) {
        Token prevToken = tokens.get(currentTokenIndex - 2);
        if (prevToken.getTipo().equals("ID")) {
          varName = prevToken.getLexema().substring(1);
        }
      }
      if ((lastToken.equals("intero") || lastToken.equals("stringa") ||
          lastToken.equals("galleggiante") || lastToken.equals("booleano") ||
          (isPer && semicolons == 0)) ||
          (!declaredVariables.contains(varName) && !varName.isEmpty())) {
        appendCode(" := ");
        if (!varName.isEmpty()) {
          declaredVariables.add(varName);
        }
      } else {
        appendCode(" = ");
      }
      lastToken = "=";
    } else if (code.equals("+=")) {
      appendCode(" += ");
      lastToken = "+=";
    } else if (code.equals("-=")) {
      appendCode(" -= ");
      lastToken = "-=";
    } else if (code.equals("*=")) {
      appendCode(" *= ");
      lastToken = "*=";
    } else if (code.equals("/=")) {
      appendCode(" /= ");
      lastToken = "/=";
    } else if (code.equals("%=")) {
      appendCode(" %= ");
      lastToken = "%=";
    } else if (code.equals(">")) {
      appendCode(" > ");
      lastToken = ">";
    } else if (code.equals("<")) {
      appendCode(" < ");
      lastToken = "<";
    } else if (code.equals("==")) {
      appendCode(" == ");
      lastToken = "==";
    } else if (code.equals("!=")) {
      appendCode(" != ");
      lastToken = "!=";
    } else if (code.equals(">=")) {
      appendCode(" >= ");
      lastToken = ">=";
    } else if (code.equals("<=")) {
      appendCode(" <= ");
      lastToken = "<=";
    } else if (code.equals("+")) {
      appendCode(" + ");
      lastToken = "+";
    } else if (code.equals("-")) {
      appendCode(" - ");
      lastToken = "-";
    } else if (code.equals("*")) {
      appendCode(" * ");
      lastToken = "*";
    } else if (code.equals("/")) {
      appendCode(" / ");
      lastToken = "/";
    } else if (code.equals("%")) {
      appendCode(" % ");
      lastToken = "%";
    } else {
      if (code.startsWith("_")) {
        if (lastToken.equals(";") || lastToken.equals("{")) {
          appendCode("\t\t"); // Double tab for statements inside blocks
        }
        appendCode(code.substring(1));
      } else {
        appendCode(code);
      }
      lastToken = code;
    }
  }
}
