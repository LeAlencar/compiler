package compiler.semantic;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Set;
import java.util.HashSet;

public class SymbolTable {
  private Stack<Map<String, Symbol>> scopes;
  private int currentScope;

  public SymbolTable() {
    this.scopes = new Stack<>();
    this.currentScope = 0;
    // Inicializa o escopo global
    enterScope();
  }

  // Entra em um novo escopo
  public void enterScope() {
    scopes.push(new HashMap<>());
    currentScope++;
  }

  // Sai do escopo atual
  public void exitScope() {
    if (!scopes.isEmpty()) {
      scopes.pop();
      currentScope--;
    }
  }

  // Insere um novo símbolo no escopo atual
  public void insert(String name, String type) throws SemanticException {
    Map<String, Symbol> currentScopeTable = scopes.peek();

    // Verifica se já existe no escopo atual
    if (currentScopeTable.containsKey(name)) {
      throw new SemanticException("Variável '" + name + "' já declarada no escopo atual");
    }

    Symbol symbol = new Symbol(name, type, currentScope);
    currentScopeTable.put(name, symbol);
  }

  // Busca um símbolo em todos os escopos, do mais interno ao mais externo
  public Symbol lookup(String name) throws SemanticException {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      Symbol symbol = scopes.get(i).get(name);
      if (symbol != null) {
        return symbol;
      }
    }
    throw new SemanticException("Variável '" + name + "' não declarada");
  }

  // Verifica se um tipo é válido
  public boolean isValidType(String type) {
    return type.equals("intero") ||
        type.equals("stringa") ||
        type.equals("booleano");
  }

  // Verifica compatibilidade de tipos para operações
  public void checkTypeCompatibility(String var1, String var2, String operation) throws SemanticException {
    Symbol symbol1 = lookup(var1);
    Symbol symbol2 = lookup(var2);

    if (!symbol1.getType().equals(symbol2.getType())) {
      throw new SemanticException("Tipos incompatíveis na operação " + operation +
          ": " + symbol1.getType() + " e " + symbol2.getType());
    }
  }

  // Atualiza o valor de uma variável
  public void updateValue(String name, Object value) throws SemanticException {
    Symbol symbol = lookup(name);
    symbol.setValue(value);
  }

  // Retorna o escopo atual
  public int getCurrentScope() {
    return currentScope;
  }

  // Retorna todas as variáveis declaradas em todos os escopos
  public Set<String> getAllVariableNames() {
    Set<String> allVariables = new HashSet<>();
    for (Map<String, Symbol> scope : scopes) {
      allVariables.addAll(scope.keySet());
    }
    return allVariables;
  }
}