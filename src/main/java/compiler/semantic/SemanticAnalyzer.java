package compiler.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import compiler.generator.GoFileGenerator;
import compiler.lexer.Token;

public class SemanticAnalyzer {
  private List<Token> tokens;
  private SymbolTable symbolTable;
  private boolean erro = false;

  public SemanticAnalyzer(List<Token> tokens) {
    this.tokens = tokens;
    // Escopo global
    this.symbolTable = new SymbolTable("global");
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  // Função auxiliar para verificar compatibilidade de tipos em comparações
  private boolean tiposCompativeisParaComparacao(String tipo1, String tipo2) {
    // Se ambos são do mesmo tipo, são compatíveis
    if (tipo1.equals(tipo2)) {
      return true;
    }
    
    // Se um dos tipos for stringa, não é compatível com nenhum outro tipo
    if (tipo1.equals("stringa") || tipo2.equals("stringa")) {
      return false;
    }
    
    // Se um dos tipos for booleano, não é compatível com nenhum outro tipo
    if (tipo1.equals("booleano") || tipo2.equals("booleano")) {
      return false;
    }
    
    // intero e galleggiante são compatíveis entre si
    if ((tipo1.equals("intero") && tipo2.equals("galleggiante")) ||
        (tipo1.equals("galleggiante") && tipo2.equals("intero"))) {
      return true;
    }
    
    return false;
  }

  // Função auxiliar para determinar o tipo de um token
  private String determinarTipoToken(Token token, Map<String, String> tiposVariaveis) {
    if (token.getTipo().equals("NUM")) {
      return "intero";
    } else if (token.getTipo().equals("NUMDECIMAL")) {
      return "galleggiante";
    } else if (token.getTipo().equals("TEXTO")) {
      return "stringa";
    } else if (token.getTipo().equals("ID")) {
      return tiposVariaveis.get(token.getLexema());
    }
    return null;
  }

  public String analyze() {
    Set<String> declaradas = new HashSet<>();
    Set<String> funcoes = new HashSet<>();
    Map<String, Integer> funcoesParametros = new HashMap<>();
    Map<String, List<String>> funcoesTiposParametros = new HashMap<>();
    Map<String, String> tiposVariaveis = new HashMap<>(); // Mapa para armazenar tipos das variáveis
    boolean erro = false;
    
    // Primeira passagem: coletar informações sobre funções e variáveis
    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      
      // Coleta tipos de variáveis
      if (token.getTipo().equals("ID") && i > 0) {
        Token prev = tokens.get(i - 1);
        if (prev.getLexema().equals("intero") || prev.getLexema().equals("stringa") ||
            prev.getLexema().equals("booleano") || prev.getLexema().equals("galleggiante")) {
          tiposVariaveis.put(token.getLexema(), prev.getLexema());
        }
      }
      
      if (token.getLexema().equals("funzione") && i + 1 < tokens.size() && tokens.get(i + 1).getTipo().equals("NOME")) {
        String nomeFunc = tokens.get(i + 1).getLexema();
        if (funcoes.contains(nomeFunc)) {
          System.err.println("Erro: função '" + nomeFunc + "' já declarada");
          erro = true;
          throw new RuntimeException("Erro: função '" + nomeFunc + "' já declarada");
        } else {
          funcoes.add(nomeFunc);
          List<String> tiposParams = new ArrayList<>();
          int numParams = 0;
          int j = i + 2; // Pula 'funzione' e nome
          String tipoAtual = null;
          while (j < tokens.size() && !tokens.get(j).getLexema().equals(")")) {
            Token tk = tokens.get(j);
            if (tk.getLexema().equals("intero")) {
              tipoAtual = "intero";
            } else if (tk.getLexema().equals("stringa")) {
              tipoAtual = "stringa";
            } else if (tk.getLexema().equals("booleano")) {
              tipoAtual = "booleano";
            } else if (tk.getLexema().equals("galleggiante")) {
              tipoAtual = "galleggiante";
            } else if (tk.getTipo().equals("ID") && tipoAtual != null) {
              tiposParams.add(tipoAtual);
              tiposVariaveis.put(tk.getLexema(), tipoAtual); // Registra o tipo do parâmetro
              numParams++;
              tipoAtual = null;
            }
            j++;
          }
          funcoesParametros.put(nomeFunc, numParams);
          funcoesTiposParametros.put(nomeFunc, tiposParams);
        }
      }
    }

    // Segunda passagem: análise semântica
    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      
      // Verifica chamada de função
      if (token.getTipo().equals("NOME") && i + 1 < tokens.size() && tokens.get(i + 1).getLexema().equals("(")) {
        String nomeFunc = token.getLexema();
        if (!funcoes.contains(nomeFunc)) {
          System.err.println("Erro: função '" + nomeFunc + "' não declarada");
          throw new RuntimeException("Erro: função '" + nomeFunc + "' não declarada");
        }
        
        // Conta e verifica argumentos passados
        int numArgs = 0;
        int j = i + 2; // Pula nome e '('
        List<String> tiposArgs = new ArrayList<>();
        while (j < tokens.size() && !tokens.get(j).getLexema().equals(")")) {
          Token arg = tokens.get(j);
          if (arg.getTipo().equals("ID") || arg.getTipo().equals("NUM") || 
              arg.getTipo().equals("TEXTO") || arg.getTipo().equals("NUMDECIMAL")) {
            numArgs++;
            // Determina o tipo do argumento
            String tipoArg;
            if (arg.getTipo().equals("NUM")) {
              tipoArg = "intero";
            } else if (arg.getTipo().equals("NUMDECIMAL")) {
              tipoArg = "galleggiante";
            } else if (arg.getTipo().equals("TEXTO")) {
              tipoArg = "stringa";
            } else if (arg.getTipo().equals("ID")) {
              // Verifica o tipo da variável no mapa
              tipoArg = tiposVariaveis.get(arg.getLexema());
              if (tipoArg == null) {
                System.err.println("Erro: tipo da variável '" + arg.getLexema() + "' não pode ser determinado");
                erro = true;
                break;
              }
            } else {
              tipoArg = "booleano"; // Assume boolean para true/false
            }
            tiposArgs.add(tipoArg);
          }
          j++;
        }
        
        // Verifica número de argumentos
        int expectedParams = funcoesParametros.get(nomeFunc);
        if (numArgs != expectedParams) {
          System.err.println("Erro: função '" + nomeFunc + "' espera " + expectedParams + 
                           " parâmetros, mas recebeu " + numArgs);
          erro = true;
          throw new RuntimeException("Erro: função '" + nomeFunc + "' espera " + expectedParams + 
                           " parâmetros, mas recebeu " + numArgs);
        }
        
        // Verifica tipos dos argumentos
        List<String> tiposEsperados = funcoesTiposParametros.get(nomeFunc);
        for (int k = 0; k < tiposArgs.size(); k++) {
          String tipoArg = tiposArgs.get(k);
          String tipoEsperado = tiposEsperados.get(k);
          if (!tipoArg.equals(tipoEsperado)) {
            System.err.println("Erro: tipo incompatível no argumento " + (k + 1) + 
                             " da função '" + nomeFunc + "'. Esperado: " + tipoEsperado + 
                             ", Recebido: " + tipoArg);
            erro = true;
            throw new RuntimeException("Erro: tipo incompatível no argumento " + (k + 1) + 
                             " da função '" + nomeFunc + "'. Esperado: " + tipoEsperado + 
                             ", Recebido: " + tipoArg);
          }
        }
      }

      // Detecta declaração de variável: tipo + ID
      if (token.getTipo().equals("ID")) {
        boolean isDeclaracao = false;
        String tipoVar = null;
        if (i > 0) {
          Token prev = tokens.get(i - 1);
          if ((prev.getLexema().equals("intero") || prev.getLexema().equals("stringa")
              || prev.getLexema().equals("booleano") || prev.getLexema().equals("galleggiante"))) {
            tipoVar = prev.getLexema();
            isDeclaracao = true;
            
            // Verifica se a variável já foi declarada
            if (declaradas.contains(token.getLexema())) {
              System.err.println("Erro: variável '" + token.getLexema() + "' já foi declarada anteriormente");
              erro = true;
              throw new RuntimeException("Erro: variável '" + token.getLexema() + "' já foi declarada anteriormente");
            }
            
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
              } else if (tipoVar.equals("galleggiante") && valor.getTipo().equals("NUMDECIMAL")) {
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
                            || tprev.getLexema().equals("booleano") || tprev.getLexema().equals("galleggiante")) {
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
              throw new RuntimeException(
                  "Erro: tipo incompatível na atribuição de '" + token.getLexema() + "' (tipo " + tipoVar + ")");
            } else {
              try {
                symbolTable.insert(token.getLexema(), false);
                declaradas.add(token.getLexema());
              } catch (SemanticException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
              }
            }
          }
        }
        // Se não é declaração, é uso: validar se já foi declarada
        if (!isDeclaracao) {
          if (!declaradas.contains(token.getLexema())) {
            throw new RuntimeException("Erro: variável '" + token.getLexema() + "' não declarada");
          }
        }
      }

      // Verifica comparações em condicionais e laços
      if (token.getLexema().equals("se") || token.getLexema().equals("mentre") || 
          token.getLexema().equals("per") || token.getLexema().equals("fare")) {
        int j = i + 1;
        while (j < tokens.size() && !tokens.get(j).getLexema().equals("{")) {
          Token tk = tokens.get(j);
          if (tk.getLexema().equals("==") || tk.getLexema().equals("!=") || 
              tk.getLexema().equals(">") || tk.getLexema().equals("<") || 
              tk.getLexema().equals(">=") || tk.getLexema().equals("<=")) {
            
            // Pega o token antes do operador
            Token antes = tokens.get(j - 1);
            // Pega o token depois do operador
            Token depois = tokens.get(j + 1);
            
            String tipoAntes = determinarTipoToken(antes, tiposVariaveis);
            String tipoDepois = determinarTipoToken(depois, tiposVariaveis);
            
            if (tipoAntes == null || tipoDepois == null) {
              System.err.println("Erro: não foi possível determinar o tipo de uma das variáveis na comparação");
              erro = true;
              throw new RuntimeException("Erro: não foi possível determinar o tipo de uma das variáveis na comparação");
            }
            
            if (!tiposCompativeisParaComparacao(tipoAntes, tipoDepois)) {
              System.err.println("Erro: tipos incompatíveis na comparação. Tipo1: " + tipoAntes + ", Tipo2: " + tipoDepois);
              erro = true;
              throw new RuntimeException("Erro: tipos incompatíveis na comparação. Tipo1: " + tipoAntes + ", Tipo2: " + tipoDepois);
            }
          }
          j++;
        }
      }
    }
    if (!erro) {
      String goCode = translateToGo();
      System.out.println("\nSemanticamente correta\n");
      GoFileGenerator.generateGoFile(goCode, "output.go");

      return goCode;
    }

    return null;
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

    // verifica se precisa importar fmt -------------------------------------------------------------------------------------------------
    for (Token t : tokens) {
      if (t.getLexema().equals("carattere") || t.getLexema().equals("leggere")) {
        needsFmt = true;
        break;
      }
    }

    // adiciona importacao de fmt se necessario -------------------------------------------------------------------------------------------------
    if (needsFmt) {
      sb.append("import (\n\t\"fmt\"\n)\n\n");
    }

    // inicia o main ------------------------------------------------------------------------------------------------------------------------------
    sb.append("func main() {\n");

    // traduz o codigo ------------------------------------------------------------------------------------------------------------------------------
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
        String nomeFunc = tokens.get(i + 1).getLexema();
        i++;
        sb.append("func ").append(nomeFunc).append("(");
        
        // Parâmetros
        int j = i + 1;
        boolean dentroPar = false;
        boolean primeiroParam = true;
        StringBuilder paramBuilder = new StringBuilder();
        String tipoParam = "";
        
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
          
          // Parâmetros: ID + tipo
          if (tk.getLexema().equals(",")) {
            if (!primeiroParam) {
              sb.append(paramBuilder.toString().trim());
              paramBuilder = new StringBuilder();
            }
            sb.append(", ");
            primeiroParam = false;
          } else if (tk.getTipo().equals("ID")) {
            String id = tk.getLexema();
            if (id.startsWith("_"))
              id = id.substring(1);
            paramBuilder.append(id);
            paramBuilder.append(tipoParam);
            tipoParam = "";
          } else if (tk.getLexema().equals("intero")) {
            tipoParam = " int";
          } else if (tk.getLexema().equals("stringa")) {
            tipoParam = " string";
          } else if (tk.getLexema().equals("booleano")) {
            tipoParam = " bool";
          } else if (tk.getLexema().equals("galleggiante")) {
            tipoParam = " float64";
          }
          primeiroParam = false;
          j++;
        }
        
        // Adiciona o último parâmetro se houver
        if (paramBuilder.length() > 0) {
          sb.append(paramBuilder.toString().trim());
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
        sb.append("\treturn\n}\n\n");
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
          } else if (arg.getTipo().equals("NUM") || arg.getTipo().equals("NUMDECIMAL")) {
            sb.append(arg.getLexema());
          } else if (arg.getTipo().equals("TEXTO")) {
            sb.append("\"").append(arg.getLexema().replace("\"", "\\\"")).append("\"");
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
        case "galleggiante":
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
            sb.append("\n\t");
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
          // Verifica se o próximo token é altrimenti
          if (i + 1 < tokens.size() && tokens.get(i + 1).getLexema().equals("altrimenti")) {
            sb.append("} ");
          } else {
            sb.append("}\n");
          }
          break;
        case "mentre":
          sb.append("\twhile (");
          break;
        case "fare":
          sb.append("\tdo {");
          break;
        case "per":
          sb.append("\tfor (");
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
        case "o":
          sb.append(" || ");
          break;
        case "e":
          sb.append(" && ");
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
          } else if (t.getTipo().equals("NUMDECIMAL")) {
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
