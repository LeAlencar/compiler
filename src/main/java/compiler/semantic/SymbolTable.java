package compiler.semantic;
import java.util.HashMap;

public class SymbolTable {
    String scope;
    SymbolTable parent;
    HashMap<String, Symbol> table;

    public SymbolTable(String sc) {
        this.scope = sc;
        this.table = new HashMap<String,Symbol>();
    }

    public SymbolTable(String scope, SymbolTable parent) {
        this.scope = scope;
        this.parent = parent;
        this.table = new HashMap<String, Symbol>();
    }

  
    public Symbol lookup(String sym) {
        Symbol result;
        result = table.get(sym);
        if (result != null) {
            return result;
        } else if (parent != null) {
            return parent.lookup(sym);
        } else  {
            return null;
        }
    }

    public void insert(String scope, boolean isConst, SymbolTable sub) throws SemanticException {
        if (table.containsKey(scope)) {
            throw new SemanticException("redeclaração de " + scope);
        } else {
            sub.parent = this;
            table.put(scope, new Symbol(scope, this, sub, isConst));
        }
    }

    public void insert(String scope, boolean isConst) throws SemanticException {
        if (table.containsKey(scope)) {
            throw new SemanticException("redeclaração de " + scope);
        } else {
            table.put(scope, new Symbol(scope, this, isConst));
        }
    }

    public void print() {
        print(0);
    }

    public void print(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        System.out.println(scope +  " - " + table.size() + " symbols");
        for (Symbol entry : table.values()) {
            entry.print(level + 1);
        }
    }
}