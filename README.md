# Gramática

funcao → **'funzione'** NOME '(' (declaracaoFuncao | ε) ')' bloco? fermare

declaracaoFuncao → tipo ID | tipo ID ',' declaracaoFuncao

bloco → linha bloco | linha

linha → escrever | ler | declaracao ';' | ifelse | while | for | atribuicao ';' | chamadaFunc

chamadaFunc → NOME '(' argumentos? ')' ';'

argumentos → exprChamadaFunc (',' exprChamadaFunc)*

exprChamadaFunc → ID | NUM | TEXTO | chamadaFunc

escrever → **'carattere'** '<<' (TEXTO | '$' ID) ('.' ('$' ID | TEXTO))\* ';'

ler → **'leggere'** 'xD' TEXTO ID? ';'

declaracao → tipo ID operadorAtribuicao expressao

ifelse → **'se'** condicao '{' bloco '}' (**'altrimenti'** '{' bloco '}')?

while → **'mentre'** condicao bloco | **'fare'** '{' bloco '}' **'mentre'** condicao ';'

for → **'per'** '(' declaracao ';' condicao ';' atribuicao ';' ')' bloco

atribuicao → ID opAtribuicao expressao

expressao → fator (opMat fator)?

fator → ID | NUM | TEXTO | '(' expressao ')' | NUMDECIMAL

condicao → fator opRelacional fator ((**'o'** | **'e'**) condicao)* | '(' condicao ')' 

tipo → **'intero'** | **'galleggiante'** | **'stringa'** | **'booleano'**

opRelacional → '>' | '<' | '==' | '!=' | '>=' | '<='

opAtribuicao → '=' | '+=' | '-=' | '\*=' | '/=' | '%='

opMat → '+' | '-' | '\*' | '/' | '%'

ID → '\_' (a-z | A-Z)+

NOME → (a-z | A-Z)+

NUM → (0-9)+

NUMDECIMAL → (0-9)+ '.' (0-9)+

TEXTO → '"' (0-9 | a-z | A-Z | ' ' )+ '"'

````
stringa _s = \"Hello, World!\";" +
      "carattere << $_s;intero _x = 10;" +
        "se _x == 10 {leggere xD \"Escreva seu nome\" _leitura;_x=1;}" +
        "fare {_x = _x + 1;} mentre _x < 15;" +
        "per (intero _i = 0; _i < 10; _i += 1;) {_x = _x * 2;}
        ```
````

# Server

# Compilador como Servidor

Este projeto implementa um compilador como um servidor web que recebe código para compilação via requisições HTTP.

## Requisitos

- Java 11 ou superior
- Maven
- Postman (ou qualquer cliente HTTP) para testar as requisições

## Como Executar

1. Clone o repositório
2. Na pasta do projeto, execute:

```bash
mvn spring-boot:run
```

3. O servidor iniciará na porta 8080

## Endpoints Disponíveis

### Verificar Status do Servidor

```
GET http://localhost:8080/api/compiler/health
```

Resposta esperada:

```json
{
  "status": "Server Rodando"
}
```

### Compilar Código

```
POST http://localhost:8080/api/compiler/compile
```

Corpo da requisição (JSON):

```json
{
  "code": "stringa _s = \"Hello, World!\"; carattere << $_s;"
}
```

Resposta de sucesso:

```json
{
    "Compilado com Sucesso": true,
    "Resultado": "código compilado...",
    "tokens": [...]
}
```

Resposta de erro:

```json
{
  "Compilado com Sucesso": false,
  "erro": "mensagem de erro",
  "Código Inválido": "código que causou o erro"
}
```

## Testando com Postman

1. Abra o Postman
2. Crie uma nova requisição POST para `http://localhost:8080/api/compiler/compile`
3. Configure o corpo da requisição:
   - Selecione "raw"
   - Escolha "JSON" no dropdown
   - Cole o JSON com o código a ser compilado
4. Envie a requisição

