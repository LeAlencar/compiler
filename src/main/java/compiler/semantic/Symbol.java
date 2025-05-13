package compiler.semantic;

public class Symbol {
  private String name; // Nome do identificador
  private String type; // Tipo (intero, stringa, booleano)
  private boolean initialized; // Se foi inicializado
  private Object value; // Valor, se houver
  private int scope; // Escopo da variável

  public Symbol(String name, String type, int scope) {
    this.name = name;
    this.type = type;
    this.scope = scope;
    this.initialized = false;
    this.value = null;
  }

  // Getters e Setters
  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
    this.initialized = true;
  }

  public int getScope() {
    return scope;
  }

  @Override
  public String toString() {
    return "Symbol{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", initialized=" + initialized +
        ", value=" + value +
        ", scope=" + scope +
        '}';
  }
}