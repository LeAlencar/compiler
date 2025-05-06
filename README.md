# Gramática
funcao → **'funzione'** NOME '(' (declaracaoFuncao | ε) ')' bloco fermare

declaracaoFuncao → tipo ID | tipo ID ',' declaracaoFuncao

bloco → linha bloco | linha

linha → escrever | ler | declaracao ';' | ifelse | while | for | atribuicao ';'

escrever → **'carattere'** '<<' (TEXTO | '$' ID) ('.' ('$' ID | TEXTO))* ';'

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

opAtribuicao → '=' | '+=' | '-=' | '*=' | '/=' | '%='

opMat → '+' | '-' | '*' | '/' | '%'

ID → '_' (a-z | A-Z)+

NOME → (a-z | A-Z)+

NUM → (0-9)+

TEXTO → '"' (0-9 | a-z | A-Z | ' ' )+ '"'

# TCP SERVER

1. Compile todos os arquivos do projeto
No terminal, navegue até a raiz do seu projeto e execute:
Apply to Client.java
Run
Isso vai compilar todos os arquivos e colocar os .class na pasta bin.
2. Inicie o servidor
No terminal, execute:
Apply to Client.java
Run
Isso vai iniciar o servidor na porta 8080. Você verá a mensagem:
Apply to Client.java
3. Em outro terminal, execute o cliente
No mesmo diretório, execute:
Apply to Client.java
Run
O cliente vai enviar um código de exemplo para o servidor. O código de exemplo está definido no Client.java:
Apply to Client.java
4. Veja o resultado
O cliente vai imprimir algo como:
Apply to Client.java
Se houver erro de sintaxe, aparecerá:
Apply to Client.java
5. Teste com outros códigos
Você pode editar o código fonte no Client.java (variável sourceCode) para enviar outros exemplos para o servidor, como:
Apply to Client.java
Depois, recompile apenas o cliente:
Apply to Client.java
Run
E execute novamente o cliente.