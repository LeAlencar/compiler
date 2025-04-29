package compiler.parser;

import compiler.lexer.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    private Parser parser;
    private List<Token> tokens;

    @BeforeEach
    void setUp() {
        tokens = new ArrayList<>();
    }

    // Test Case 1: Simple Variable Declaration
    @Test
    void testSimpleVariableDeclaration() {
        // int x = 10;
        tokens.add(new Token("TYPE_INT", "intero"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "10"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertDoesNotThrow(() -> parser.main());
    }

    // Test Case 2: Invalid Variable Declaration
    @Test
    void testInvalidVariableDeclaration() {
        // int x = ; (missing value)
        tokens.add(new Token("TYPE_INT", "intero"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertThrows(RuntimeException.class, () -> parser.main());
    }

    // Test Case 3: Simple If-Else Statement
    @Test
    void testSimpleIfElse() {
        // if (x > 0) { x = 1; } else { x = 2; }
        tokens.add(new Token("IF", "se"));
        tokens.add(new Token("LPAREN", "("));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("GTR", ">"));
        tokens.add(new Token("NUM", "0"));
        tokens.add(new Token("RPAREN", ")"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "1"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("RBRACE", "}"));
        tokens.add(new Token("ELSE", "altrimenti"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "2"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("RBRACE", "}"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertDoesNotThrow(() -> parser.main());
    }

    // Test Case 4: While Loop
    @Test
    void testWhileLoop() {
        // while (x > 0) { x -= 1; }
        tokens.add(new Token("WHILE", "mentre"));
        tokens.add(new Token("LPAREN", "("));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("GTR", ">"));
        tokens.add(new Token("NUM", "0"));
        tokens.add(new Token("RPAREN", ")"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("SUB_ASSIGN", "-="));
        tokens.add(new Token("NUM", "1"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("RBRACE", "}"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertDoesNotThrow(() -> parser.main());
    }

    // Test Case 5: For Loop
    @Test
    void testForLoop() {
        // for (int i = 0; i < 10; i += 1) { x = 2; }
        tokens.add(new Token("FOR", "per"));
        tokens.add(new Token("LPAREN", "("));
        tokens.add(new Token("TYPE_INT", "intero"));
        tokens.add(new Token("ID", "i"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "0"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("ID", "i"));
        tokens.add(new Token("LSS", "<"));
        tokens.add(new Token("NUM", "10"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("ID", "i"));
        tokens.add(new Token("ADD_ASSIGN", "+="));
        tokens.add(new Token("NUM", "1"));
        tokens.add(new Token("RPAREN", ")"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "2"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("RBRACE", "}"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertDoesNotThrow(() -> parser.main());
    }

    // Test Case 6: Do-While Loop
    @Test
    void testDoWhileLoop() {
        // do { x = 2; } while (x < 100);
        tokens.add(new Token("DO", "fare"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "2"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("RBRACE", "}"));
        tokens.add(new Token("WHILE", "mentre"));
        tokens.add(new Token("LPAREN", "("));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("LSS", "<"));
        tokens.add(new Token("NUM", "100"));
        tokens.add(new Token("RPAREN", ")"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertDoesNotThrow(() -> parser.main());
    }

    // Test Case 7: Complex Mathematical Expression
    @Test
    void testComplexMathematicalExpression() {
        // x = x + 2 * 3;
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ADD", "+"));
        tokens.add(new Token("NUM", "2"));
        tokens.add(new Token("MUL", "*"));
        tokens.add(new Token("NUM", "3"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertDoesNotThrow(() -> parser.main());
    }

    // Test Case 8: Nested Control Structures
    @Test
    void testNestedControlStructures() {
        // if (x > 0) { while (y < 10) { y += 1; } }
        tokens.add(new Token("IF", "se"));
        tokens.add(new Token("LPAREN", "("));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("GTR", ">"));
        tokens.add(new Token("NUM", "0"));
        tokens.add(new Token("RPAREN", ")"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("WHILE", "mentre"));
        tokens.add(new Token("LPAREN", "("));
        tokens.add(new Token("ID", "y"));
        tokens.add(new Token("LSS", "<"));
        tokens.add(new Token("NUM", "10"));
        tokens.add(new Token("RPAREN", ")"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("ID", "y"));
        tokens.add(new Token("ADD_ASSIGN", "+="));
        tokens.add(new Token("NUM", "1"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("RBRACE", "}"));
        tokens.add(new Token("RBRACE", "}"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertDoesNotThrow(() -> parser.main());
    }

    // Test Case 9: Invalid Syntax - Missing Semicolon
    @Test
    void testMissingSemicolon() {
        // int x = 10 (missing semicolon)
        tokens.add(new Token("TYPE_INT", "intero"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "10"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertThrows(RuntimeException.class, () -> parser.main());
    }

    // Test Case 10: Invalid Syntax - Mismatched Braces
    @Test
    void testMismatchedBraces() {
        // if (x > 0) { x = 1; (missing closing brace)
        tokens.add(new Token("IF", "se"));
        tokens.add(new Token("LPAREN", "("));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("GTR", ">"));
        tokens.add(new Token("NUM", "0"));
        tokens.add(new Token("RPAREN", ")"));
        tokens.add(new Token("LBRACE", "{"));
        tokens.add(new Token("ID", "x"));
        tokens.add(new Token("ASSIGN", "="));
        tokens.add(new Token("NUM", "1"));
        tokens.add(new Token("SEMICOLON", ";"));
        tokens.add(new Token("EOF", "EOF"));

        parser = new Parser(tokens);
        assertThrows(RuntimeException.class, () -> parser.main());
    }
} 