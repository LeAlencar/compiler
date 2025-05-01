# Gramática
funcao → **'funzione'** NOME '(' (declaracaoFuncao | ε) ')' bloco fermare

declaracaoFuncao → tipo ID | tipo ID ',' declaracaoFuncao

bloco → linha bloco | linha

linha → escrever | ler | declaracao | ifelse | while | for | atribuicao

escrever → **'carattere'** '<<' (TEXTO | '$' ID) ('.' ('$' ID | TEXTO))* ';'

ler → **'leggere'** 'xD' TEXTO ID? ';'

declaracao → tipo ID operadorAtribuicao (NUM | ID) ';'

ifelse → **'se'** condicao bloco (**'altrimenti'** bloco)?

while → **'mentre'** condicao bloco | **'fare'** bloco **'mentre'** condicao

for → **'per'** '(' declaracao ';' condicao ';' atribuicao ')' bloco

atribuicao → ID opAtribuicao (ID | NUM | TEXTO | expressao)

expressao → (ID | NUM) opMat (ID | NUM | '(' expressao ')' | expressao)

condicao → (ID | NUM) opRelacional (ID | NUM) | (ID | NUM) opRelacional (ID | NUM) (**'o'** | **'e'**) condicao | '(' (ID | NUM) opRelacional (ID | NUM) ')' | '(' (ID | NUM) opRelacional (ID | NUM) (**'o'** | **'e'**) condicao ')'

tipo → **'intero'** | **'galleggiante'** | **'stringa'** | **'booleano'**

opRelacional → '>' | '<' | '==' | '!=' | '>=' | '<='

opAtribuicao → '=' | '+=' | '-=' | '*=' | '/=' | '%='

opMat → '+' | '-' | '*' | '/' | '%'

ID → '_' (a-z | A-Z)+

NOME → (a-z | A-Z)+

NUM → (0-9)+

TEXTO → '"' (0-9 | a-z | A-Z | ' ' )+ '"'