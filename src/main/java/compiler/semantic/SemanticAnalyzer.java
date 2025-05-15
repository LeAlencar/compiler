package compiler.semantic;

import compiler.generator.GoFileGenerator;
import compiler.lexer.Token;
import compiler.semantic.SymbolTable;
import compiler.semantic.SemanticException;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class SemanticAnalyzer {
  private List<Token> tokens;
  private SymbolTable symbolTable;

  public SemanticAnalyzer(List<Token> tokens) {
    this.tokens = tokens;
    // Escopo global
    this.symbolTable = new SymbolTable("global");
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  public void analyze() {
    Set<String> declaradas = new HashSet<>();
    Set<String> funcoes = new HashSet<>();
    boolean erro = false;
    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      // Verifica declaração de função repetida
      if (token.getLexema().equals("funzione") && i + 1 < tokens.size() && tokens.get(i + 1).getTipo().equals("NOME")) {
        String nomeFunc = tokens.get(i + 1).getLexema();
        if (funcoes.contains(nomeFunc)) {
          System.err.println("Erro: função '" + nomeFunc + "' já declarada");
          erro = true;
          break;
        } else {
          funcoes.add(nomeFunc);
        }
      }
      // Detecta declaração de variável: tipo + ID
      if (token.getTipo().equals("ID")) {
        boolean isDeclaracao = false;
        String tipoVar = null;
        if (i > 0) {
          Token prev = tokens.get(i - 1);
          if ((prev.getLexema().equals("intero") || prev.getLexema().equals("stringa")
              || prev.getLexema().equals("booleano"))) {
            tipoVar = prev.getLexema();
            isDeclaracao = true;
            if (!declaradas.contains(token.getLexema())) {
              // Verifica compatibilidade de tipo na atribuição
              boolean tipoCompativel = true;
              String valorAtribuido = null;
              if (i + 2 < tokens.size() && tokens.get(i + 1).getLexema().equals("=")) {
                Token valor = tokens.get(i + 2);
                valorAtribuido = valor.getLexema();
                // Se for literal
                if (tipoVar.equals("intero") && valor.getTipo().equals("NUM")) {
                  tipoCompativel = true;
                } else if (tipoVar.equals("stringa") && valor.getTipo().equals("TEXTO")) {
                  tipoCompativel = true;
                } else if (tipoVar.equals("booleano")
                    && (valorAtribuido.equals("true") || valorAtribuido.equals("false"))) {
                  tipoCompativel = true;
                } else if (valor.getTipo().equals("ID")) {
                  // Se for variável, precisa estar declarada e ser do mesmo tipo
                  if (declaradas.contains(valorAtribuido)) {
                    // Procurar tipo da variável atribuída
                    int tipoIdx = -1;
                    for (int j = 0; j < i; j++) {
                      if (tokens.get(j).getLexema().equals(valorAtribuido)) {
                        if (j > 0) {
                          Token tprev = tokens.get(j - 1);
                          if (tprev.getLexema().equals(tipoVar)) {
                            tipoCompativel = true;
                            break;
                          } else if (tprev.getLexema().equals("intero") || tprev.getLexema().equals("stringa")
                              || tprev.getLexema().equals("booleano")) {
                            tipoCompativel = false;
                            break;
                          }
                        }
                      }
                    }
                  } else {
                    tipoCompativel = false;
                  }
                } else {
                  tipoCompativel = false;
                }
              }
              if (!tipoCompativel) {
                System.err.println(
                    "Erro: tipo incompatível na atribuição de '" + token.getLexema() + "' (tipo " + tipoVar + ")");
                erro = true;
                break;
              } else {
                try {
                  symbolTable.insert(token.getLexema(), false);
                  declaradas.add(token.getLexema());
                } catch (SemanticException e) {
                  System.err.println(e.getMessage());
                  erro = true;
                  break;
                }
              }
            }
          }
        }
        // Se não é declaração, é uso: validar se já foi declarada
        if (!isDeclaracao) {
          if (!declaradas.contains(token.getLexema())) {
            System.err.println("Erro: variável '" + token.getLexema() + "' não declarada");
            erro = true;
            break;
          }
        }
      }
    }
    // Imprime a tabela de símbolos ao final
    symbolTable.print();
    // Só traduz se não houver erro
    if (!erro) {
      String goCode = translateToGo();
      System.out.println("\nCódigo traduzido para GoLang:\n" + goCode);
      GoFileGenerator.generateGoFile(goCode, "output.go");
    }
  }

  // Traduz os tokens para GoLang conforme a gramática
  public String translateToGo() {
    boolean carattere = false;
    boolean leggere = false;
    boolean mainAberto = true;
    boolean dentroFuncao = false;
    boolean needsFmt = false;
    boolean declaracaoNova = false;
    String declaracaoNovaString = "";
    StringBuilder sb = new StringBuilder();
    sb.append("package main\n\n");

    // First pass to check if fmt is needed
    for (Token t : tokens) {
      if (t.getLexema().equals("carattere") || t.getLexema().equals("leggere")) {
        needsFmt = true;
        break;
      }
    }

    // Only add fmt import if needed
    if (needsFmt) {
      sb.append("import (\n\t\"fmt\"\n)\n\n");
    }

    sb.append("func main() {\n");
    for (int i = 0; i < tokens.size(); i++) {
      Token t = tokens.get(i);
      String lex = t.getLexema();
      // Detecta início de declaração de função
      if (lex.equals("funzione") && i + 1 < tokens.size() && tokens.get(i + 1).getTipo().equals("NOME")) {
        if (mainAberto) {
          sb.append("}\n\n"); // Fecha o main
          mainAberto = false;
        }
        dentroFuncao = true;
        // Nome da função
        String nomeFunc = tokens.get(i + 1).getLexema();
        i++;
        sb.append("func ").append(nomeFunc).append("(");
        // Parâmetros
        int j = i + 1;
        boolean dentroPar = false;
        while (j < tokens.size()) {
          Token tk = tokens.get(j);
          if (tk.getLexema().equals("(")) {
            dentroPar = true;
            j++;
            continue;
          }
          if (tk.getLexema().equals(")")) {
            i = j;
            break;
          }
          // Parâmetros: tipo + ID
          if (tk.getLexema().equals(",")) {
            sb.append(", ");
          } else if (tk.getTipo().equals("ID")) {
            String id = tk.getLexema();
            if (id.startsWith("_"))
              id = id.substring(1);
            sb.append(id).append(" ");
          } else if (tk.getLexema().equals("intero")) {
            sb.append("int");
          } else if (tk.getLexema().equals("stringa")) {
            sb.append("string");
          } else if (tk.getLexema().equals("booleano")) {
            sb.append("bool");
          }
          j++;
        }
        sb.append(") {\n");
        continue;
      }
      // Detecta padrão 'fare { ... } mentre <condição> ;'
      if (lex.equals("fare") && i + 1 < tokens.size() && tokens.get(i + 1).getLexema().equals("{")) {
        sb.append("\tfor {");
        i += 1; // Pula o '{'
        continue;
      }
      if (lex.equals("}") && i + 3 < tokens.size() && tokens.get(i + 1).getLexema().equals("mentre")) {
        // Fecha o bloco do for e adiciona if <condição> { break; }
        sb.append("\n");
        sb.append("\tif ");
        // Copia a condição após 'mentre', removendo '_' dos IDs
        int j = i + 2;
        while (j < tokens.size() && !tokens.get(j).getLexema().equals(";")) {
          Token condTok = tokens.get(j);
          if (condTok.getTipo().equals("ID")) {
            String id = condTok.getLexema();
            if (id.startsWith("_")) {
              sb.append(id.substring(1));
            } else {
              sb.append(id);
            }
          } else {
            sb.append(condTok.getLexema());
          }
          sb.append(" ");
          j++;
        }
        sb.append("{ break; }");
        sb.append("\n}\n");
        i = j; // Pula até o ';'
        continue;
      }
      // Tradução especial para 'per' (for)
      if (lex.equals("per") && i + 1 < tokens.size() && tokens.get(i + 1).getLexema().equals("(")) {
        sb.append("\tfor ");
        i += 2; // pula 'per' e '('
        // Captura as três cláusulas
        StringBuilder init = new StringBuilder();
        StringBuilder cond = new StringBuilder();
        StringBuilder inc = new StringBuilder();
        int clause = 0;
        boolean foundAssign = false;
        while (i < tokens.size() && !tokens.get(i).getLexema().equals(")")) {
          Token tk = tokens.get(i);
          if (tk.getLexema().equals(";")) {
            clause++;
            i++;
            continue;
          }
          String val = tk.getLexema();
          if (tk.getTipo().equals("ID") && val.startsWith("_"))
            val = val.substring(1);
          // Inicialização: não imprime tipo, troca '=' por ':='
          if (clause == 0) {
            if (val.equals("intero") || val.equals("stringa") || val.equals("booleano")) {
              // ignora tipo
            } else if (val.equals("=") && !foundAssign) {
              init.append(":= ");
              foundAssign = true;
            } else {
              init.append(val).append(" ");
            }
          } else if (clause == 1)
            cond.append(val).append(" ");
          else if (clause == 2)
            inc.append(val).append(" ");
          i++;
        }
        sb.append(init.toString().trim()).append("; ");
        sb.append(cond.toString().trim()).append("; ");
        sb.append(inc.toString().trim());
        sb.append(" ");
        continue;
      }
      // Fecha função ao encontrar 'fermare'
      if (dentroFuncao && lex.equals("fermare")) {
        sb.append("}\n\n");
        dentroFuncao = false;
        continue;
      }
      // Tradução de chamada de função: NOME '(' argumentos? ')'
      if (!dentroFuncao && t.getTipo().equals("NOME") && i + 1 < tokens.size()
          && tokens.get(i + 1).getLexema().equals("(")
          && (i == 0 || !(tokens.get(i - 1).getTipo().equals("NUM") || tokens.get(i - 1).getTipo().equals("ID")
              || tokens.get(i - 1).getTipo().equals("TEXTO") || tokens.get(i - 1).getTipo().equals("NOME")))) {
        sb.append(t.getLexema()).append("(");
        i += 2; // pula NOME e '('
        // Argumentos
        boolean firstArg = true;
        while (i < tokens.size() && !tokens.get(i).getLexema().equals(")")) {
          Token arg = tokens.get(i);
          if (!firstArg && arg.getLexema().equals(",")) {
            sb.append(", ");
            i++;
            continue;
          }
          if (arg.getTipo().equals("ID")) {
            String id = arg.getLexema();
            if (id.startsWith("_"))
              id = id.substring(1);
            sb.append(id);
          } else {
            sb.append(arg.getLexema());
          }
          firstArg = false;
          i++;
        }
        sb.append(")");
        // Pular o ')'
        while (i < tokens.size() && !tokens.get(i).getLexema().equals(";"))
          i++;
        continue;
      }
      switch (lex) {
        case "intero":
          sb.append("\tvar ");
          declaracaoNova = true;
          break;
        case "stringa":
          sb.append("\tvar ");
          declaracaoNova = true;
          break;
        case "booleano":
          sb.append("\tvar ");
          declaracaoNova = true;
          break;
        case "=":
          sb.append(" = ");
          break;
        case "+=":
          sb.append(" += ");
          break;
        case "-=":
          sb.append(" -= ");
          break;
        case "*=":
          sb.append(" *= ");
          break;
        case "/=":
          sb.append(" /= ");
          break;
        case "%=":
          sb.append(" %= ");
          break;
        case "+":
          sb.append(" + ");
          break;
        case "-":
          sb.append(" - ");
          break;
        case "*":
          sb.append(" * ");
          break;
        case "/":
          sb.append(" / ");
          break;
        case "%":
          sb.append(" % ");
          break;
        case ";":
          if (carattere) {
            sb.append(")\n");
            carattere = false;
          } else if (declaracaoNovaString != "") {
            sb.append("\n");
            sb.append(declaracaoNovaString + " = " + declaracaoNovaString + "\n");
            declaracaoNovaString = "";
            declaracaoNova = false;
          } else {
            sb.append("\n");
          }
          break;
        case "carattere":
          sb.append("\tfmt.Println(");
          carattere = true;
          break;
        case "<<":
          break;
        case ".":
          sb.append(", ");
          break;
        case "se":
          sb.append("\tif ");
          break;
        case "altrimenti":
          sb.append(" else ");
          break;
        case "{":
          sb.append("{\n");
          break;
        case "}":
          sb.append("}\n");
          break;
        case "mentre":
          sb.append("\tfor ");
          break;
        case "fare":
          sb.append("\tfor ");
          break;
        case "per":
          sb.append("\tfor ");
          break;
        case "(":
          sb.append("(");
          break;
        case ")":
          sb.append(")");
          break;
        case "==":
          sb.append(" == ");
          break;
        case "!=":
          sb.append(" != ");
          break;
        case ">":
          sb.append(" > ");
          break;
        case "<":
          sb.append(" < ");
          break;
        case ">=":
          sb.append(" >= ");
          break;
        case "<=":
          sb.append(" <= ");
          break;
        case "xD":
          break;
        case "leggere":
          sb.append("\tfmt.Print(");
          leggere = true;
          break;
        case "TEXTO":
          sb.append(tokens.get(i).getLexema());
          break;
        default:
          if (t.getTipo().equals("ID")) {
            String id = tokens.get(i).getLexema();
            if (id.startsWith("_")) {
              sb.append(id.substring(1));
              if (declaracaoNova) {
                declaracaoNovaString = id.substring(1);
                declaracaoNova = false;
              }
            }
            if (carattere) {
              sb.append(")");
              carattere = false;
            }
            if (leggere) {
              sb.append(")");
              leggere = false;
            }
          } else if (t.getTipo().equals("NUM")) {
            sb.append(tokens.get(i).getLexema());
          } else if (t.getTipo().equals("TEXTO")) {
            sb.append(tokens.get(i).getLexema());
            if (leggere) {
              sb.append(")\n\tfmt.Scanln(&");
            }
          }
      }
    }
    // Ao final, feche o main apenas se ele ainda estiver aberto
    if (mainAberto) {
      sb.append("\n}\n");
    }
    return sb.toString();
  }
}
