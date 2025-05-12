# Gramática

funcao → **'funzione'** NOME '(' (declaracaoFuncao | ε) ')' bloco fermare

declaracaoFuncao → tipo ID | tipo ID ',' declaracaoFuncao

bloco → linha bloco | linha

linha → escrever | ler | declaracao ';' | ifelse | while | for | atribuicao ';'

escrever → **'carattere'** '<<' (TEXTO | '\$' ID) ('.' ('$' ID | TEXTO))\* ';'

ler → **'leggere'** 'xD' TEXTO ID? ';'

declaracao → tipo ID operadorAtribuicao expressao

ifelse → **'se'** condicao '{' bloco '}' (**'altrimenti'** '{' bloco '}')?

while → **'mentre'** condicao bloco | **'fare'** bloco **'mentre'** condicao ';'

for → **'per'** '(' declaracao ';' condicao ';' atribuicao ';' ')' bloco

atribuicao → ID opAtribuicao expressao

expressao → fator (opMat fator)?

fator → ID | NUM | '(' expressao ')'

condicao → (ID | NUM) opRelacional (ID | NUM) | (ID | NUM) opRelacional (ID | NUM) (**'o'** | **'e'**) condicao | '(' (ID | NUM) opRelacional (ID | NUM) ')' | '(' (ID | NUM) opRelacional (ID | NUM) (**'o'** | **'e'**) condicao ')'

tipo → **'intero'** | **'galleggiante'** | **'stringa'** | **'booleano'**

opRelacional → '>' | '<' | '==' | '!=' | '>=' | '<='

opAtribuicao → '=' | '+=' | '-=' | '\*=' | '/=' | '%='

opMat → '+' | '-' | '\*' | '/' | '%'

ID → '\_' (a-z | A-Z)+

NOME → (a-z | A-Z)+

NUM → (0-9)+

TEXTO → '"' (0-9 | a-z | A-Z | ' ' )+ '"'

````
stringa _s = \"Hello, World!\";" +
      "carattere << $_s;intero _x = 10;" +
        "se _x == 10 {leggere xD \"Escreva seu nome\" _leitura;_x=1;}" +
        "fare {_x = _x + 1;} mentre _x < 15;" +
        "per (intero _i = 0; _i < 10; _i += 1;) {_x = _x * 2;}
        ```
````
