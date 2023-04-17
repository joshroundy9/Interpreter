package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Lexer {

    private ArrayList<Token> tokenList = new ArrayList<Token>();
    private HashMap<String, Token.tokenType> knownWords = new HashMap<>();
    private HashMap<Character, Token.tokenType> knownSymbols = new HashMap<>();
    private HashSet<Character> invalidCharacters = new HashSet<>();

    private boolean commentState = false;

    private int previousIndentLevel = -1;
    private int indentLevel = 0;

    public Lexer() {
        knownWords.put("while", Token.tokenType.WHILE);
        knownWords.put("for", Token.tokenType.FOR);
        knownWords.put("constants", Token.tokenType.CONSTANTS);
        knownWords.put("integer", Token.tokenType.INTEGER);
        knownWords.put("real", Token.tokenType.REAL);
        knownWords.put("boolean", Token.tokenType.BOOLEAN);
        knownWords.put("character", Token.tokenType.CHARACTER);
        knownWords.put("string", Token.tokenType.STRING);
        knownWords.put("define", Token.tokenType.DEFINE);
        knownWords.put("var", Token.tokenType.VAR);
        knownWords.put("if", Token.tokenType.IF);
        knownWords.put("elsif", Token.tokenType.ELSEIF);
        knownWords.put("else", Token.tokenType.ELSE);
        knownWords.put("block", Token.tokenType.BLOCK);
        knownWords.put("to", Token.tokenType.TO);
        knownWords.put("from", Token.tokenType.FROM);
        knownWords.put("mod", Token.tokenType.MOD);
        knownWords.put("variables", Token.tokenType.VARIABLES);
        knownWords.put("true", Token.tokenType.TRUE);
        knownWords.put("false", Token.tokenType.FALSE);
        knownWords.put("then", Token.tokenType.THEN);
        knownWords.put("repeat", Token.tokenType.REPEAT);
        knownWords.put("until", Token.tokenType.UNTIL);
        knownWords.put("array", Token.tokenType.ARRAY);
        knownWords.put("of", Token.tokenType.OF);
        knownWords.put("and", Token.tokenType.AND);
        knownWords.put("not", Token.tokenType.NOT);
        knownWords.put("or", Token.tokenType.OR);
        // symbols map
        knownSymbols.put('=', Token.tokenType.COMPARISON);
        knownSymbols.put('+', Token.tokenType.ADD);
        knownSymbols.put('*', Token.tokenType.MULT);
        knownSymbols.put('%', Token.tokenType.MOD);
        knownSymbols.put('-', Token.tokenType.SUBTRACT);
        knownSymbols.put('/', Token.tokenType.DIVIDE);
        knownSymbols.put(',', Token.tokenType.COMMA);
        knownSymbols.put(';', Token.tokenType.SEMICOLON);
        knownSymbols.put('[', Token.tokenType.LBRACKET);
        knownSymbols.put(']', Token.tokenType.RBRACKET);
        knownSymbols.put('(', Token.tokenType.LPAREN);
        knownSymbols.put(')', Token.tokenType.RPAREN);
        // invalid characters
        invalidCharacters.add('!');
        invalidCharacters.add('?');
        invalidCharacters.add('@');
        invalidCharacters.add('#');
        invalidCharacters.add('`');
        invalidCharacters.add('~');
        invalidCharacters.add('_');
        invalidCharacters.add('$');
    }

    /**
     * Takes a string and converts into tokens consisting of its words and numbers,
     * putting them into the tokenList.
     * 
     * @param line The string used as input.
     */
    public void lex(String lineString, int line, int totalLines) throws SyntaxErrorException {
        int prestate = 1;
        // int 1-4 1=nothing 2=start of multicharacter operator 3=string literal
        // 4=character literal

        int state = 1; // Integers 1-4 Used for the state machine logic
        String accumulatedChars = "";
        Character previousOperator = 'c';

        boolean indentStage = true;
        int spaceCount = 0;

        boolean indentsInserted = false;
        int character;

        char[] charArray = lineString.toCharArray();
        for (character = 0; character < charArray.length; character++) {
            Character c = charArray[character];
            /*
             * I used a switch in this scenario because I think it is the easiest way to
             * read the code when using this state machine
             * since it has four distinct states with very different logic behind each
             * state.
             */
            // first state machine (for comments)
            if (commentState && c.equals('}')) {
                // hotfix during assignment 6 for comments with code after them (thanks
                // discussion!)
                boolean foundError = false;
                for (int i = character + 1; i < charArray.length; i++) {
                    if (!((Character) charArray[i]).equals(' ') && !((Character) charArray[i]).equals('\t'))
                        foundError = true;
                }
                if (foundError)
                    throw new SyntaxErrorException(
                            "Single or multi line comments are not allowed to have any code on the same line after the closing brace on line "
                                    + line + ".");
                commentState = false;
            } else if (c.equals('{'))
                commentState = true;

            // OCCURS IF NOT IN A COMMENT
            else if (!commentState) {
                // indent level logic
                if (indentStage) {
                    if (!c.equals(' ') && !c.equals('\t')) {
                        indentStage = false;
                    } else if (c.equals('\t'))
                        spaceCount += 4;
                    else if (c.equals(' '))
                        spaceCount++;

                } else if (!indentsInserted && !indentStage) {
                    indentLevel = Math.floorDiv(spaceCount, 4);
                    indentsInserted = true;
                    if (indentLevel == 0 && previousIndentLevel != 0) { // if a dedent occurs
                        for (int i = 0; i < previousIndentLevel; i++) {
                            Token indent = new Token(null, Token.tokenType.DEDENT);
                            tokenList.add(indent);
                        }
                        previousIndentLevel = 0;

                    } else if (indentLevel > 0 && indentLevel > previousIndentLevel) { // If an indent occurs
                        for (int i = 0; i < indentLevel - previousIndentLevel; i++) {
                            Token indent = new Token(null, Token.tokenType.INDENT);
                            tokenList.add(indent);

                        }
                        previousIndentLevel = indentLevel;
                    } else if (indentLevel < previousIndentLevel) {
                        for (int i = 0; i < previousIndentLevel - indentLevel; i++) {
                            Token indent = new Token(null, Token.tokenType.DEDENT);
                            tokenList.add(indent);
                        }
                        previousIndentLevel = indentLevel;
                    }

                }

                // second state machine
                switch (prestate) {
                    case 1: // checks for the operators that can be a precurser to a multicharacter operator
                        if (c.equals(':') || c.equals('<') || c.equals('>')) {
                            prestate = 2;
                            previousOperator = c;
                            createWordIfPossible(accumulatedChars);
                            accumulatedChars = "";
                        } else if (c.equals('"')) {
                            prestate = 3;
                            createWordIfPossible(accumulatedChars);
                            accumulatedChars = "";
                        } else if (c.equals('\'')) {
                            prestate = 4;
                            createWordIfPossible(accumulatedChars);
                            accumulatedChars = "";
                        }

                        if (prestate == 1) {
                            // original state machine for operators and basic tokens
                            // checks for invalid tokens
                            if (invalidCharacters.contains(c))
                                throw new SyntaxErrorException("Invalid character '" + c + "' on line " + line + ".");
                            switch (state) {
                                case 1: // checks for operators, letters numbers or decimals and switches the state
                                        // accordingly
                                    boolean foundOperator = false;
                                    Token operator;
                                    // dictionary for operators
                                    if (knownSymbols.containsKey(c)) {
                                        operator = new Token(null, knownSymbols.get(c));
                                        tokenList.add(operator);
                                        foundOperator = true;
                                    }
                                    if (foundOperator)
                                        break;
                                    if (Character.isLetter(c)) {
                                        accumulatedChars = accumulatedChars + c.toString();
                                        state = 2;
                                    } else if (c.equals('.')) {
                                        accumulatedChars = accumulatedChars + c.toString();
                                        state = 3;
                                    } else if (Character.isDigit(c)) {
                                        accumulatedChars = accumulatedChars + c.toString();
                                        state = 4;
                                    }
                                    break;

                                case 2: // looks for more characters or numbers in a word, if none are found an
                                        // identifier token is created
                                    if (Character.isLetterOrDigit(c)) {
                                        accumulatedChars = accumulatedChars + c.toString();
                                    } else {
                                        createWordIfPossible(accumulatedChars);
                                        state = 1;
                                        // requeues the character that did not become an identifier back into the system
                                        character--;
                                        accumulatedChars = "";
                                    }
                                    break;

                                case 3: // looks for more decimal points as a "." was already found, if none are found a
                                        // number token is created
                                    if (Character.isDigit(c)) {
                                        accumulatedChars = accumulatedChars + c.toString();
                                    } else {
                                        Token token = new Token(accumulatedChars, Token.tokenType.NUMBER);
                                        tokenList.add(token);
                                        state = 1;
                                        accumulatedChars = "";
                                        // requeues the character that did not become a number back into the system
                                        character--;
                                    }
                                    break;

                                case 4: // checks for a "." then a number, if none are found a number token is created
                                    if (c.equals('.')) {
                                        accumulatedChars = accumulatedChars + c.toString();
                                        state = 3;
                                    } else if (Character.isDigit(c)) {
                                        accumulatedChars = accumulatedChars + c.toString();
                                    } else {
                                        Token token = new Token(accumulatedChars, Token.tokenType.NUMBER);
                                        tokenList.add(token);
                                        state = 1;
                                        accumulatedChars = "";
                                        // requeues the character that did not become a number back into the system
                                        character--;
                                    }
                            }

                        }
                        // continued prestate machine
                        break;
                    case 2: //
                        Token token;
                        if (c.equals('=')) {
                            if (previousOperator.equals(':')) {
                                token = new Token(null, Token.tokenType.ASSIGNMENT);
                                tokenList.add(token);
                            } else if (previousOperator.equals('<')) {
                                token = new Token(null, Token.tokenType.LESSERTHANEQUAL);
                                tokenList.add(token);
                            } else if (previousOperator.equals('>')) {
                                token = new Token(null, Token.tokenType.GREATERTHANEQUAL);
                                tokenList.add(token);
                            }
                            prestate = 1;
                        } else {
                            if (previousOperator.equals(':')) {
                                token = new Token(null, Token.tokenType.COLON);
                                tokenList.add(token);
                            } else if (previousOperator.equals('<')) {
                                token = new Token(null, Token.tokenType.LESS);
                                tokenList.add(token);
                            } else if (previousOperator.equals('>')) {
                                token = new Token(null, Token.tokenType.GREATER);
                                tokenList.add(token);
                            }
                            previousOperator = 'c';
                            prestate = 1;
                            // requeues the character that did not become a multi character operator back
                            // into the system
                            character--;
                        }
                        break;
                    case 3:
                        if (c.equals('"')) {
                            token = new Token(accumulatedChars, Token.tokenType.STRINGLITERAL);
                            tokenList.add(token);
                            accumulatedChars = "";
                            prestate = 1;
                        } else if (c.equals('\''))
                            throw new SyntaxErrorException(
                                    "Cannot have char literal inside string literal on line " + line + ".");
                        else
                            accumulatedChars = accumulatedChars + c.toString();
                        break;

                    case 4:
                        if (c.equals('\'')) {
                            token = new Token(accumulatedChars, Token.tokenType.CHARLITERAL);
                            tokenList.add(token);
                            accumulatedChars = "";
                            prestate = 1;
                        } else if (c.equals('"'))
                            throw new SyntaxErrorException(
                                    "Cannot have string literal inside char literal on line " + line + ".");
                        else if (accumulatedChars.length() >= 1)
                            throw new SyntaxErrorException(
                                    "Char literals can only contain one character on line " + line + ".");
                        else
                            accumulatedChars = accumulatedChars + c.toString();
                        break;
                }
            }
        }

        if (prestate == 3)
            throw new SyntaxErrorException(
                    "Opening \" found, closing \" required on the same line on line " + line + ".");
        if (prestate == 4)
            throw new SyntaxErrorException(
                    "Opening ' found, closing ' required on the same line on line " + line + ".");

        /*
         * Checks for a remaining string and makes a token for it since the loop leaves
         * text in the
         * accumulatedChars string if the last character was a number or letter.
         */
        if (state == 2 && !accumulatedChars.equals("")) {
            createWordIfPossible(accumulatedChars);

        } else if (state == 3 || state == 4 && !accumulatedChars.equals("")) {
            Token last = new Token(accumulatedChars, Token.tokenType.NUMBER);
            tokenList.add(last);
        }
        /*
         * Since this method only applies to one line of the text and the loop cycles
         * over all the characters,
         * after the for loop is over it means the line is over, so the appropriate
         * token is added.
         */
        Token token = new Token(null, Token.tokenType.ENDOFLINE);
        state = 1;
        tokenList.add(token);

        if (line == totalLines) {

            for (int i = 0; i < indentLevel; i++) {
                Token t = new Token(null, Token.tokenType.DEDENT);
                tokenList.add(t);
            }
        }
    }

    /**
     * Takes a string and maps it to a known word if possible,
     * if it fails to find a known word it creates an identifier with the string.
     * 
     * @param accString The input string.
     */
    public void createWordIfPossible(String accString) {
        if (!accString.equals("")) {
            Token last;
            if (knownWords.containsKey(accString)) {
                last = new Token(accString, knownWords.get(accString));

            } else
                last = new Token(accString, Token.tokenType.IDENTIFIER);
            tokenList.add(last);
        }
    }

    /**
     * Getter for the collection of tokens.
     * 
     * @return List<Token> The list of tokens.
     */
    public List<Token> getTokens() {
        return tokenList;
    }
}
