# JavaCompiler
Java made compiler for the Compilers and OS class 


# Gramática
```javascript
<main> ::= <statements> EOF

<statements> ::= <statement> <statements>
               | ε

<statement> ::= <declaracao>
              | <ifelse>
              | <whileLoop>
              | <doWhileLoop>
              | <forLoop>
              | <expressao> ";"

<declaracao> ::= <tipo> <id> <operadorAtribuicao> (<num> | <id>) ";"

<tipo> ::= "intero" | "stringa" | "booleano"

<ifelse> ::= "se" <condicao> <bloco> "altrimenti" <bloco>

<whileLoop> ::= "mentre" "(" <termo> <operador> <termo> ")" <bloco>

<doWhileLoop> ::= "fare" <bloco> "mentre" "(" <termo> <operador> <termo> ")" ";"

<forLoop> ::= "per" "(" (<declaracao> | (<expressao> ";")) <termo> <operador> <termo> ";" <expressao> ")" <bloco>

<bloco> ::= "{" {<statement>} "}"
          | <statement>

<condicao> ::= "(" <expressaoComparativa> ")"
            | <expressaoComparativa>

<expressaoComparativa> ::= <termo> <operador> <termo>
                         | <termo>

<operador> ::= ">" | "<" | "==" | "!=" | ">=" | "<="

<expressao> ::= <atribuicao>
              | <expressaoComparativa>
              | <expressaoMatematica>

<atribuicao> ::= <id> <operadorAtribuicao> <expressaoMatematica>

<operadorAtribuicao> ::= "=" | "+=" | "-="
                       | "*=" | "/=" | "%="

<expressaoMatematica> ::= <termo> <opMat> <termo>
                        | <termo>

<opMat> ::= "+" | "-" | "*" | "/" | "%"

<termo> ::= <id> | <num>

<id> ::= "ID"

<num> ::= "NUM"

```


