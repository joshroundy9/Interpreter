package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Main.Token.tokenType;
import Nodes.*;
import Nodes.BooleanCompareNode.comparisonType;
import Nodes.VariableNode.variableType;

public class Parser {
    private enum constantSearchStates {
        LOOKINGFORNAME, LOOKINGFOREQUALS, LOOKINGFORVALUE
    }

    private Queue<Token> tokenQueue;
    private int line = 1;
    private HashMap<tokenType, variableType> typeMap;
    private ProgramNode program;

    public Parser(List<Token> tokenList) {
        typeMap = new HashMap<>();
        typeMap.put(tokenType.INTEGER, variableType.INTEGER);
        typeMap.put(tokenType.BOOLEAN, variableType.BOOLEAN);
        typeMap.put(tokenType.CHARACTER, variableType.CHARACTER);
        typeMap.put(tokenType.STRING, variableType.STRING);
        typeMap.put(tokenType.REAL, variableType.REAL);
        tokenQueue = new LinkedList<>();
        for (Token t : tokenList)
            tokenQueue.add(t);
    }
    /**
     * Parses a list of tokens and turns them into a tree that can be read by the
     * interpreter
     * 
     * @return Node The top of the created tree.
     * @throws SyntaxErrorException Exception thrown if any code errors are found.
     */
    public Node parse() throws SyntaxErrorException {
        program = programNode();
        System.out.println(program);
        return null;
    }
    public ProgramNode getProgramNode()
    {
        return program;
    }
    /**
     * Finds all of the programs functions, adds them to the program nodes HashMap
     * and returns it.
     * 
     * @return ProgramNode The program node returned.
     * @throws SyntaxErrorException Exception thrown if any code errors are found.
     */
    private ProgramNode programNode() throws SyntaxErrorException {
        ProgramNode programNode = new ProgramNode();
        FunctionNode functionNode = new FunctionNode(null, null, null, null);
        while (functionNode != null) {
            functionNode = function();
            if (functionNode == null)
                return programNode;
            programNode.addFunction(functionNode);
        }
        return programNode;
    }
    /**
     * Finds a function and its many components and turns it into a tree
     * 
     * @return FunctionNode The function node returned if found.
     * @throws SyntaxErrorException Thrown if there are errors in the code.
     */
    private FunctionNode function() throws SyntaxErrorException {
        // makes sure that all end of line and dedent tokens are removed before parsing
        // begins, allowing as many empty lines as wanted seperating functions.
        while (matchAndRemove(tokenType.ENDOFLINE) != null);
        while (matchAndRemove(tokenType.DEDENT) != null);
        // makes sure that it has found a function as all functions are gonna have a
        // number and define token identifying them
        if (matchAndRemove(tokenType.DEFINE) == null)
            return null;
        String functionName = null;
        if (peek(1) == tokenType.IDENTIFIER)
            functionName = matchAndRemove(tokenType.IDENTIFIER).getValue();
        else
            throw new SyntaxErrorException("No valid function name provided on line " + line + ".");
        if (matchAndRemove(tokenType.LPAREN) == null)
            throw new SyntaxErrorException("Opening parenthese required after function name on line " + line + ".");

        ArrayList<ParameterNode> parameters = parameterDeclarations();
        if (matchAndRemove(tokenType.RPAREN) == null)
            throw new SyntaxErrorException(
                    "Function declaration statement must end with a ) on line " + line + ".");
        expectsEndOfLine();
        // checks and initializes variables in a loop going over each line until it
        // reaches statements.
        ArrayList<VariableNode> variables = variableDeclarations();
        ArrayList<StatementNode> statements = statements();
        return new FunctionNode(functionName, parameters, variables, statements);
    }
    /**
     * Finds and identifies the statements, returning them in an ordered list.
     * 
     * @return ArrayList<StatementNode> The list of all of a functions
     *         statements(body) returned.
     * @throws SyntaxErrorException Thrown if any errors occur along the way.
     */
    // will be worked on in assignment 5/6
    private ArrayList<StatementNode> statements() throws SyntaxErrorException {
        ArrayList<StatementNode> statements = new ArrayList<StatementNode>();
        // removes all dedents and end of lines before the statements are written,
        // allowing the writer to have as many breaks and spaces between lines as they
        // want
        while (matchAndRemove(tokenType.DEDENT) != null && matchAndRemove(tokenType.ENDOFLINE) != null);
        // loops until the dedent which is at the end of all functions then returns
        boolean cont = true;
        while (matchAndRemove(tokenType.DEDENT) == null && cont && peek(1) != null) {
            StatementNode statement = parseAssignment();
            if (statement == null)
                statement = parseFunctionCalls();
            if (statement == null)
                if (peek(1) != tokenType.ELSE)
                    statement = parseIf(false);
                else
                    cont = false;
            if (statement == null)
                statement = parseWhile();
            if (statement == null)
                statement = parseRepeat();
            if (statement == null)
                statement = parseFor();
            if (statement != null)
                statements.add(statement);
            // if (tokenQueue.peek() != null && statement != null && peek(1)!=
            // tokenType.DEDENT && peek(1) != null)
            while (matchAndRemove(tokenType.ENDOFLINE) != null);
        }
        return statements;
    }
    /**
     * Finds variable and constant declarations and returns them all in a list.
     * 
     * @return ArrayList<VariableNode>
     * @throws SyntaxErrorException
     */
    private ArrayList<VariableNode> variableDeclarations() throws SyntaxErrorException {
        ArrayList<VariableNode> variableList = new ArrayList<>();
        // loops each subsequent line checking if an indent was added
        while (matchAndRemove(tokenType.INDENT) == null) {
            // checks if the type of variable definition is constants
            if (matchAndRemove(tokenType.CONSTANTS) != null) {
                constantSearchStates state = constantSearchStates.LOOKINGFORNAME;
                /* Loops around code that identifies constant terms and adds them */
                String constantName = null;
                while (tokenQueue.peek().getType() != tokenType.ENDOFLINE) {
                    /*
                     * if there are multiple constants defined on the same line this removes them
                     * since this main loop only activates
                     * after each constant term is identified
                     */
                    matchAndRemove(tokenType.COMMA);
                    /*
                     * uses a state machine to check for each expected part of the constant
                     * declaration
                     * this is possible because constant declarations must follow the format
                     * constants NAME = VALUE
                     * (optionally followed by a comma with extra terms)
                     */
                    if (state == constantSearchStates.LOOKINGFORNAME) {
                        if (tokenQueue.peek().getType() != tokenType.IDENTIFIER)
                            throw new SyntaxErrorException(
                                    "'constants' keyword must be followed by a valid name on line " + line + ".");
                        constantName = matchAndRemove(tokenType.IDENTIFIER).getValue();

                        state = constantSearchStates.LOOKINGFOREQUALS;
                    } else if (state == constantSearchStates.LOOKINGFOREQUALS) {
                        if (matchAndRemove(tokenType.COMPARISON) == null)
                            throw new SyntaxErrorException(
                                    "Constants must be defined immediately after being named on line " + line + ".");

                        state = constantSearchStates.LOOKINGFORVALUE;
                    } else if (state == constantSearchStates.LOOKINGFORVALUE) {
                        boolean isNegative = false, isArray = false;
                        tokenType nextType = peek(1);
                        while(matchAndRemove(tokenType.SUBTRACT) != null) isNegative = !isNegative;
                        switch (nextType)
                        {
                            case NUMBER,STRINGLITERAL,TRUE,FALSE,CHARLITERAL: break;
                            case LBRACKET: isArray = true; break;
                            default: throw new SyntaxErrorException("A valid constant value \""+nextType+"\" was not provided after = on line " + line + ".");
                        }
                        
                        // since all constant values are either followed by a comma or the end of the
                        // line, this will work
                        if(isArray)
                            variableList.add(new VariableNode(constantName, variableType.ARRAY,parseArray(),false));
                         else {
                            Token value = matchAndRemove(nextType);
                            if (value == null)
                                throw new SyntaxErrorException(
                                        "A valid constant value was not provided after = on line " + line + ".");
                            variableType variableT = VariableNode.getTypeFromValue(value);
                            if (isNegative && variableT == variableType.INTEGER || variableT == variableType.REAL)
                                variableList.add(new VariableNode(constantName, variableT, "-" + value.getValue(), false));
                            else
                                variableList.add(new VariableNode(constantName, variableT, value.getValue(), false));
                        }
                        state = constantSearchStates.LOOKINGFORNAME;
                        constantName = null;
                        
                    }
                }
                if (state != constantSearchStates.LOOKINGFORNAME)
                    throw new SyntaxErrorException("Incomplete declaration of constant on line " + line + ".");
                expectsEndOfLine();
                /*
                 * unlike the handling for constants, variables has a less linear structure
                 * because they are not defined
                 * on the same line and can have ranges depending on the developers choice.
                 */
            } else if (matchAndRemove(tokenType.VARIABLES) != null) {

                ArrayList<String> variableNames = new ArrayList<>();
                // loops over all the names before a type is selected
                while (matchAndRemove(tokenType.COLON) == null) {
                    if (variableNames.size() > 0 && matchAndRemove(tokenType.COMMA) == null)
                        throw new SyntaxErrorException(
                                "Consecutive variables declared on the same line must be seperated by commas on line "
                                        + line + ".");

                    if (tokenQueue.peek().getType() != tokenType.IDENTIFIER)
                        throw new SyntaxErrorException("Invalid variable name given on line " + line + ".");
                    variableNames.add(matchAndRemove(tokenType.IDENTIFIER).getValue());
                    // handling array initialization
                }
                // after the colon is found look for type
                boolean isArray = false;
                if(peek(1) == tokenType.ARRAY)
                {
                    matchAndRemove(tokenType.ARRAY);
                    isArray = true;
                    if(matchAndRemove(tokenType.OF) == null)
                    throw new SyntaxErrorException("When defining an array type the format \"array of *type* from ~from int to int~\" where ~ means optional, must be followed.");
                }
                tokenType typeForVariables = tokenQueue.peek().getType();

                if (!typeMap.containsKey(typeForVariables))
                    throw new SyntaxErrorException(
                            "Invalid type provided for the given variable names on line " + line + ".");
                tokenQueue.remove();
                if (matchAndRemove(tokenType.FROM) == null) {
                    if(isArray) throw new SyntaxErrorException("Arrays MUST have set beginning and end values when defined.");
                    for (String n : variableNames)
                        if (!isArray)
                            variableList.add(new VariableNode(n, typeMap.get(typeForVariables), true, isArray));
                        else
                            variableList.add(new VariableNode(n, typeMap.get(typeForVariables), true, isArray));

                } else {
                    if (tokenQueue.peek().getType() != tokenType.NUMBER)
                        throw new SyntaxErrorException(
                                "A valid range number must be provided after the word \"from\" on line " + line + ".");
                    double firstNum = Double.parseDouble(matchAndRemove(tokenType.NUMBER).getValue());
                    if (tokenQueue.peek().getType() != tokenType.TO)
                        throw new SyntaxErrorException("Word \"to\" is required after the low number for the range.");
                    matchAndRemove(tokenType.TO);
                    if (tokenQueue.peek().getType() != tokenType.NUMBER)
                        throw new SyntaxErrorException(
                                "A valid range number must be provided after the word \"to\" on line " + line + ".");
                    double secondNum = Double.parseDouble(matchAndRemove(tokenType.NUMBER).getValue());
                    for (String n : variableNames)
                        if (!isArray)
                            variableList.add(
                                    new VariableNode(n, typeMap.get(typeForVariables), firstNum, secondNum, isArray));
                        else
                            variableList.add(
                                    new VariableNode(n, typeMap.get(typeForVariables), firstNum, secondNum, isArray));
                }
                expectsEndOfLine();
            }
        }
        return variableList;
    }
    /**
     * Searches within the function declaration lines parentheses for its
     * parameters, adds them to the list, and returns.
     * 
     * @return ArrayList<VariableNode> The list of VariableNodes returned.
     * @throws SyntaxErrorException Thrown if errors are found in the code.
     */
    private ArrayList<ParameterNode> parameterDeclarations() throws SyntaxErrorException {
        ArrayList<ParameterNode> nodeList = new ArrayList<>();
        ArrayList<String> queuedNames = new ArrayList<>();
        boolean currentQueueVar = false;
        // loops until the function returns, will return an empty nodeList if no
        // parameters are found.
        while (true) {
            boolean areQueuedNamesArray = false;
            String name = null;
            if (matchAndRemove(tokenType.VAR) != null)
                currentQueueVar = true;
            if (tokenQueue.peek().getType() == tokenType.IDENTIFIER) {
                name = matchAndRemove(tokenType.IDENTIFIER).getValue();
                queuedNames.add(name);
            }
            if (name == null && nodeList.size() == 0)
                return nodeList;
            if (matchAndRemove(tokenType.COMMA) != null) {
            }else if (matchAndRemove(tokenType.COLON) == null)  throw new SyntaxErrorException("Colon required after parameter names on line "+line+".");
            else {
                if (queuedNames.size() == 0)
                    throw new SyntaxErrorException("Colon must be preceeded by parameter names on line " + line + ".");
            
                //check if the current queue is an array
                if(matchAndRemove(tokenType.ARRAY)!= null)
                {
                    if(matchAndRemove(tokenType.OF)== null)
                    throw new SyntaxErrorException("Invalid parameter array declaration on line "+line+". Format must be \"array of *TYPE*\"");
                    areQueuedNamesArray = true;
                }
                // hashmap for tokentype to variabletype
                tokenType type = tokenQueue.peek().getType();

                if (!typeMap.containsKey(type))
                    throw new SyntaxErrorException(
                            "Colon must be followed by a type for the previously stated parameters, or type was not spelled properly completely in lowercase, on line "
                                    + line + ".");
                tokenQueue.remove();
                for (int i = 0; i < queuedNames.size(); i++) {
                    nodeList.add(new ParameterNode(new VariableNode(queuedNames.get(i), typeMap.get(type), currentQueueVar,
                            areQueuedNamesArray),currentQueueVar));
                }
                queuedNames = new ArrayList<>();
                if (matchAndRemove(tokenType.SEMICOLON) == null) {
                    return nodeList;
                }
            }
        }
    }
    /**
     * Parses functions when called.
     * 
     * @return StatementNode The function node (extends statement node) returned.
     * @throws SyntaxErrorException Thrown when an error is found in the code.
     */
    private StatementNode parseFunctionCalls() throws SyntaxErrorException {
        if (peek(1) != tokenType.IDENTIFIER)
            return null;
        String referencedFunction = matchAndRemove(tokenType.IDENTIFIER).getValue();
        if (matchAndRemove(tokenType.ENDOFLINE) != null)
            return new FunctionCallNode(referencedFunction, new ArrayList<>());
        ArrayList<ParameterNode> arguments = new ArrayList<>();
        while (peek(1) != tokenType.ENDOFLINE) 
        {
            boolean isVar = matchAndRemove(tokenType.VAR) != null;
            Node booleanCompare = boolCompare();
            if (booleanCompare != null)
                arguments.add(new ParameterNode(booleanCompare,isVar));
             else
                throw new SyntaxErrorException("Invalid parameter \""+peek(1)+"\" in the function on line " + line + ".");
            if (matchAndRemove(tokenType.COMMA) == null && peek(1) != tokenType.ENDOFLINE)
                throw new SyntaxErrorException("Comma expected after parameter in the function on line " + line + ".");
        }
        return new FunctionCallNode(referencedFunction, arguments);
    }
    /**
     * Parses if statements when called.
     * 
     * @param preceedingIf Was this function called from another if statement?
     * @return StatementNode The if node (extends statement node) returned.
     * @throws SyntaxErrorException Thrown when an error occurs.
     */
    private StatementNode parseIf(boolean preceedingIf) throws SyntaxErrorException {
        // checks for
        Node condition;
        boolean isElse = false;
        // since the statements function does not complete a loop before this there will
        // be a dedent before if its not in a chain
        if (preceedingIf)
            if (!preceedingIf && matchAndRemove(tokenType.DEDENT) == null)
                throw new SyntaxErrorException(
                        "Dedent required after the preceeding if statement on line " + line + ".");
        tokenType next = peek(1);
        // stops the if chain if next token is if and there was a preceeding if
        // statement.
        if (next == tokenType.ELSE && !preceedingIf)
            throw new SyntaxErrorException("Else statement requires if before it on " + line + ".");
        if (next == tokenType.IF && preceedingIf)
            return null;
        else if (next == tokenType.ELSE)
            isElse = true;
        else if (next != tokenType.IF && next != tokenType.ELSEIF && next != tokenType.ELSE)
            return null;
        tokenQueue.remove();
        condition = boolCompare();
        if (isElse && condition != null)
            throw new SyntaxErrorException("Else statements cannot have conditions on line " + line + ".");
        if (condition == null && !isElse)
            throw new SyntaxErrorException("Invalid condition provided after 'if' on line " + line + ".");

        if (matchAndRemove(tokenType.THEN) == null && !isElse)
            throw new SyntaxErrorException("'then' required at the end of an if statement on line " + line + ".");

        expectsEndOfLine();
        if (matchAndRemove(tokenType.INDENT) == null)
            throw new SyntaxErrorException(
                    "One indentation required after if statement declaration on line " + line + ".");

        ArrayList<StatementNode> statements = statements();
        StatementNode elseNode = null;
        if (peek(1) == tokenType.ELSE || peek(1) == tokenType.ELSEIF) {
            elseNode = parseIf(true);

        }

        return new IfNode(condition, statements, elseNode);
    }
    /**
     * Parses while loops when called.
     * 
     * @return StatementNode The while node (extends statement node) returned.
     * @throws SyntaxErrorException Thrown when an error occurs.
     */
    private StatementNode parseWhile() throws SyntaxErrorException {
        if (matchAndRemove(tokenType.WHILE) == null)
            return null;
        Node condition = boolCompare();
        expectsEndOfLine();
        if (matchAndRemove(tokenType.INDENT) == null)
            throw new SyntaxErrorException(
                    "One indentation required after while loop declaration on line " + line + ".");
        ArrayList<StatementNode> statements = statements();
        return new WhileNode(condition, statements);
    }
    /**
     * Parses repeat until nodes when called.
     * 
     * @return StatementNode The statement node (extends statement node) returned.
     * @throws SyntaxErrorException Thrown when an error occurs.
     */
    private StatementNode parseRepeat() throws SyntaxErrorException {
        if (matchAndRemove(tokenType.REPEAT) == null)
            return null;
        if (matchAndRemove(tokenType.UNTIL) == null)
            throw new SyntaxErrorException("\"until\" required after repeat to form loop on line " + line + ".");
        Node condition = boolCompare();
        expectsEndOfLine();
        if (matchAndRemove(tokenType.INDENT) == null)
            throw new SyntaxErrorException(
                    "One indentation required after repeat until loop declaration on line " + line + ".");
        ArrayList<StatementNode> statements = statements();
        return new RepeatUntilNode(condition, statements);
    }
    /**
     * Parses for loops when called.
     * 
     * @return StatementNode The for node (extends statement node) returned.
     * @throws SyntaxErrorException Thrown when an error occurs.
     */
    private StatementNode parseFor() throws SyntaxErrorException {
        if (matchAndRemove(tokenType.FOR) == null)
            return null;
        // checks for variable ref.
        Token token = matchAndRemove(tokenType.IDENTIFIER);
        if (token == null)
            throw new SyntaxErrorException("Variable reference required after 'for' on line " + line + ".");
        VariableReferenceNode variable;
        // checks for array recursively
        if (matchAndRemove(tokenType.LBRACKET) != null) {
            Node arrayNumber = expression();
            if (arrayNumber == null)
                throw new SyntaxErrorException("Invalid array number provided on line" + line + ".");
            if (matchAndRemove(tokenType.RBRACKET) == null)
                throw new SyntaxErrorException("Closing ']' required for array on line " + line + ".");
            variable = new VariableReferenceNode(token.toString(), arrayNumber);
        } else {
            variable = new VariableReferenceNode(token.toString());
        }
        // checks for other requirements then returns for node
        if (matchAndRemove(tokenType.FROM) == null)
            throw new SyntaxErrorException("'From' required after variable declaration on line " + line + ".");
        Node lowBound = expression();
        if (lowBound == null)
            throw new SyntaxErrorException("Invalid lower bound provided on line " + line + ".");
        if (matchAndRemove(tokenType.TO) == null)
            throw new SyntaxErrorException("'to' required after lower bound on line " + line + ".");
        Node upBound = expression();
        if (upBound == null)
            throw new SyntaxErrorException("Invalid upper bound provided on line " + line + ".");
        expectsEndOfLine();
        if (matchAndRemove(tokenType.INDENT) == null)
            throw new SyntaxErrorException("Indent required after for loop declaration on line " + line + ".");
        ArrayList<StatementNode> statements = statements();
        if (statements == null)
            throw new SyntaxErrorException(
                    "Invalid statements provided after for loop declaration on line " + line + ".");
        return new ForNode(variable, lowBound, upBound, statements);
    }
    /**
     * Checks for assignment statements and returns them if found.
     * 
     * @return StatementNode The AssignmentNode returned.
     * @throws SyntaxErrorException Thrown if errors are found after a variable name
     *                              is found.
     */
    private StatementNode parseAssignment() throws SyntaxErrorException {
        /*
         * MUST be checked before function call or function call might think an
         * // assignment line is one.
         * //
         * // if first statement is an identifier and there is an assignment operator in
         * // the same line, it must be an assignment statement.
         */
        boolean cont = false;
        if (peek(1) == tokenType.IDENTIFIER) {

            int i = 2;
            while (peek(i) != tokenType.ENDOFLINE && cont == false) {
                if (peek(i) == tokenType.ASSIGNMENT)
                    cont = true;
                i++;
            }
        }
        if (!cont)
            return null;
        Token variableName = matchAndRemove(tokenType.IDENTIFIER);
        VariableReferenceNode variable;
        if (variableName == null)
            return null;
        if (matchAndRemove(tokenType.LBRACKET) != null) {
            Node arrayNumber = expression();
            if (matchAndRemove(tokenType.RBRACKET) == null)
                throw new SyntaxErrorException("Closing ']' required on line " + line + ".");
            variable = new VariableReferenceNode(variableName.toString(), arrayNumber);
        } else {
            variable = new VariableReferenceNode(variableName.toString());
        }
        if (matchAndRemove(tokenType.ASSIGNMENT) == null)
            throw new SyntaxErrorException("Assignment operator not found on line " + line + ".");
        tokenType type = tokenQueue.peek().getType();

        switch (type) {
            case STRINGLITERAL: return new AssignmentNode(variable, new StringNode(matchAndRemove(tokenType.STRINGLITERAL).getValue()));
            case TRUE: tokenQueue.remove();return new AssignmentNode(variable, new BooleanNode(true));
            case FALSE:tokenQueue.remove();return new AssignmentNode(variable, new BooleanNode(false));
            case CHARLITERAL:return new AssignmentNode(variable, new CharacterNode(matchAndRemove(type).toString().charAt(0)));
            case NUMBER, IDENTIFIER:// in the case of numbers and names of variables they could be a boolean comparison
                return new AssignmentNode(variable, boolCompare());
            case LBRACKET: return new AssignmentNode(variable, new VariableNode(null, variableType.ARRAY, parseArray(), true));
            default:throw new SyntaxErrorException("Invalid value for assignment provided on line " + line + ".");
        }

    }
    /**
     * Returns an expression, if the expression has a boolean comparison symbol
     * after, it returns a booleancomparenode with the two expressions and the
     * symbol between them.
     * 
     * @return Node The Node returned.
     * @throws SyntaxErrorException Thrown if any errors occur, reasoning for the
     *                              errors will be returned in the stack trace.
     */
    private Node boolCompare() throws SyntaxErrorException {
        boolean inverted = matchAndRemove(tokenType.NOT) != null;
        Node firstExpression = null, secondExpression = null;
        // checks if its simply a true or false boolean first
        if (matchAndRemove(tokenType.TRUE) != null)
            firstExpression= new BooleanNode(!inverted);
        else if (matchAndRemove(tokenType.FALSE) != null)
            firstExpression= new BooleanNode(inverted);
        else if(typeMap.containsKey(peek(2)))
            firstExpression = boolCompare();
        // then checks for expressions, and boolean comparisons.
        else firstExpression = expression();
        if (firstExpression == null)
            return null;
        HashMap<tokenType, comparisonType> typeMap = new HashMap<>();
        typeMap.put(tokenType.COMPARISON, comparisonType.EQUAL);
        typeMap.put(tokenType.GREATER, comparisonType.GREATER);
        typeMap.put(tokenType.LESS, comparisonType.LESS);
        typeMap.put(tokenType.LESSERTHANEQUAL, comparisonType.LESSEQUAL);
        typeMap.put(tokenType.GREATERTHANEQUAL, comparisonType.GREATEREQUAL);
        typeMap.put(tokenType.OR, comparisonType.OR);
        typeMap.put(tokenType.AND, comparisonType.AND);
        if (tokenQueue.peek().getType() != tokenType.AND && tokenQueue.peek().getType() != tokenType.OR && !typeMap.containsKey(tokenQueue.peek().getType()))
        {
            if(inverted) throw new SyntaxErrorException("\"not\" cannot be placed before an expression, only before a boolean.");
            return firstExpression;
        }
        comparisonType type = typeMap.get(tokenQueue.remove().getType());
        if (matchAndRemove(tokenType.TRUE) != null)
            secondExpression= new BooleanNode(!inverted);
        else if (matchAndRemove(tokenType.FALSE) != null)
        secondExpression= new BooleanNode(inverted);
        else if(typeMap.containsKey(peek(2)))
            secondExpression = boolCompare();
        else secondExpression = expression();
        if (secondExpression == null)
            throw new SyntaxErrorException("No valid expression found afte comparison symbol on line " + line + ".");
        BooleanCompareNode firstComparison = new BooleanCompareNode(firstExpression, type, secondExpression,inverted);
        /*at the end of a boolean compare, it checks for and or or, if found it looks for another boolean comparison, 
        if one is not found an error is thrown and a parent node is created for them*/
        if(tokenQueue.peek().getType() == tokenType.AND || tokenQueue.peek().getType() == tokenType.OR)
        {
            tokenType booleanCompareType = tokenQueue.remove().getType();
            Node secondComparison = boolCompare();
            if(!(secondComparison instanceof BooleanCompareNode)) throw new SyntaxErrorException("\"and\" and \"or\" can only be used when comparing two boolean expressions.");
            return new BooleanCompareNode(firstComparison, typeMap.get(booleanCompareType), secondComparison, inverted);
        } else return firstComparison;
    }
    /**
     * Generates a tree for a math expression if found.
     * 
     * @return Node The top of the math expression tree.
     * @throws SyntaxErrorException
     */
    private Node expression() throws SyntaxErrorException {
        MathOpNode mainNode = null;
        Node currentTerm = null;
        do {
            // checks for preexisting node
            if (mainNode == null) {
                // check for term

                currentTerm = term();
                if (currentTerm != null) {
                    // check for sign
                    tokenType sign = null;
                    if (matchAndRemove(tokenType.ADD) != null)
                        sign = tokenType.ADD;
                    else if (matchAndRemove(tokenType.SUBTRACT) != null)
                        sign = tokenType.SUBTRACT;
                    if (sign == null && mainNode == null)
                        return currentTerm;
                    else if (sign != null && mainNode != null)
                        mainNode = new MathOpNode(MathOpNode.getType(sign), mainNode, currentTerm);
                    else if (sign != null && mainNode == null) {
                        // check for term (base case)
                        Node term2 = term();
                        if (term2 == null)
                            throw new SyntaxErrorException("Invalid term after math operator on line " + line + ".");
                        mainNode = new MathOpNode(MathOpNode.getType(sign), currentTerm, term2);
                    }
                } else
                    return null;
            } else {
                // if there is already a preexisting node look for sign then number
                tokenType sign = null;
                if (matchAndRemove(tokenType.ADD) != null)
                    sign = tokenType.ADD;
                else if (matchAndRemove(tokenType.SUBTRACT) != null)
                    sign = tokenType.SUBTRACT;
                if (sign == null)
                    return mainNode;
                Node term = term();
                if (term == null)
                    throw new SyntaxErrorException("Missing number after sign on line " + line + "");
                mainNode = new MathOpNode(MathOpNode.getType(sign), mainNode, term);
            }
        } while (currentTerm != null);
        return mainNode;
    }
    /**
     * Searches for individual terms (a number operator number group) if found or an
     * individual number and sends them to the
     * expression.
     * 
     * @return Node The mathopnode or number node found.
     * @throws SyntaxErrorException
     */
    private Node term() throws SyntaxErrorException {
        // check for factor
        Node factor = factor();
        if (factor == null)
            return null;
        // check for sign
        tokenType sign = searchForMathOperator();
        Node mathOpNode = null;
        Node factor2 = null;
        if (sign == null)
            return factor;
        while(sign != null)
        {
            factor2 = factor();
            if (factor2 == null)
                throw new SyntaxErrorException("No valid number after sign on line " + line + ".");
            else {
                if(peek(1) == tokenType.MULT || peek(1) == tokenType.MOD|| peek(1) == tokenType.DIVIDE)
                {
                    if(mathOpNode != null)
                    return mathOpNode = new MathOpNode(MathOpNode.getType(sign), mathOpNode, factor2);
                    else mathOpNode = new MathOpNode(MathOpNode.getType(sign), factor,factor2);
                    sign = searchForMathOperator();
                } else{
                if(mathOpNode != null)
                return new MathOpNode(MathOpNode.getType(sign), mathOpNode,factor2);
                else return new MathOpNode(MathOpNode.getType(sign), factor,factor2);
                }
            }
        }
        return null;
    }
    /**
     * Finds an individual number and its sign and returns it to term.
     * If an opeing parenthese is found it calls expression again.
     * 
     * @return Node The number node returned.sw
     * @throws SyntaxErrorException
     */
    private Node factor() throws SyntaxErrorException {
        //check for lparen
        if(matchAndRemove(tokenType.LPAREN) == null)
        {
            // check for negative sign
            boolean negativeMode = false;
            while(matchAndRemove(tokenType.SUBTRACT) != null)
            negativeMode = !negativeMode;

            tokenType type = tokenQueue.peek().getType();
            
            if(!(type == tokenType.NUMBER || type == tokenType.IDENTIFIER || type == tokenType.STRINGLITERAL || type == tokenType.CHARLITERAL))
            {
                if(negativeMode ) throw new SyntaxErrorException("Invalid \"-\" sign on line "+line+".");
                return null;
            }
            
            Token token = tokenQueue.remove();
            // check for number, string, character or identifier(variable name)
            if(type == tokenType.NUMBER)
            {
                if (token.getValue().indexOf(".") == -1) {
                    if (negativeMode)
                        return new IntegerNode(Integer.parseInt(token.getValue()) * -1);
                    else
                        return new IntegerNode(Integer.parseInt(token.getValue()));
                } else {
                    if (negativeMode)
                        return new RealNode(Float.parseFloat(token.getValue()) * -1);
                    else
                        return new RealNode(Float.parseFloat(token.getValue()));
                }
            }else if(type == tokenType.IDENTIFIER)
            {
                if(matchAndRemove(tokenType.LBRACKET) == null)
                    return new VariableReferenceNode(token.toString());
                Node arrayNumber = expression();
                if(arrayNumber == null) 
                    throw new SyntaxErrorException("Invalid array number provided on line"+line+".");
                if(matchAndRemove(tokenType.RBRACKET) == null)
                    throw new SyntaxErrorException("Closing ']' required for array on line "+line+".");
                return new VariableReferenceNode(token.toString(), arrayNumber);

            }else if(type == tokenType.STRINGLITERAL)
            {
                if(negativeMode) throw new SyntaxErrorException("You cannot negate a string on line +"+line+".");
                return new StringNode(token.getValue());
            }
            else if(type == tokenType.CHARLITERAL)
            {
                if(negativeMode) throw new SyntaxErrorException("You cannot negate a character on line +"+line+".");
                return new CharacterNode(token.getValue().toCharArray()[0]);
            }
            return null;
        } else {
            /*if opening parenthese is found, expression is called recursively handling the nested statement and 
            returns it if a closing paren is found.*/
            Node nestedExpression = expression();
            if(matchAndRemove(tokenType.RPAREN) == null)
            throw new SyntaxErrorException("Closing parenthese required to end nested expression on line "+line+".");
            return nestedExpression;
        }
    }
    /**
     * Checks for a token type, if its present the token is removed from the list
     * and returned, if not it returns null.
     * 
     * @param type The type of token to look for.sw
     * @return Token
     */
    private Token matchAndRemove(Token.tokenType type) {
        if (tokenQueue.peek() != null && type == tokenQueue.peek().getType()) {
            if(type == tokenType.ENDOFLINE) line++;
            return tokenQueue.remove();
        } else
            return null;
    }
    /**
     * Match and remove just for end of line tokens, works on more than one of them
     * if present.
     * 
     * @return Token The end of line token returned if found.
     * @throws SyntaxErrorException
     */
    private Token expectsEndOfLine() throws SyntaxErrorException {
        boolean successful = false;
        while (matchAndRemove(tokenType.ENDOFLINE) != null) {
            successful = true;
        }
        if (successful != true)
            throw new SyntaxErrorException("End of line token expected and not found on line " + line + ".");
        else
            return new Token(null, tokenType.ENDOFLINE);
    }
    private String parseArray() throws SyntaxErrorException
    {
        if(matchAndRemove(tokenType.LBRACKET) == null)
        return null;
        String data = "[";
        for(int i = 0; i <100; i++)
        {
            if(i == 99) throw new SyntaxErrorException("No array defining closing bracket found or array elements > 99");
            Token token = tokenQueue.remove();
            if(token.getValue() != null)
            {
                /*the lexer automatically removes single and double quotes from strings and chars, so by adding them back it makes
                identifiying the data type when the array is being initialized much easier.*/
                if(token.getType() == tokenType.STRINGLITERAL)
                    data+="\""+token.getValue()+"\"";
                else if (token.getType() == tokenType.CHARLITERAL)
                data+="'"+token.getValue()+"'";
                else
                data+=token.getValue();
            }else data+=token.getType().toString().toLowerCase();
            if(matchAndRemove(tokenType.COMMA) != null) data+=",";
            else if(matchAndRemove(tokenType.RBRACKET) != null){ data+="]";break;}
            
        }
        return data;
    }
    /**
     * Peeks one or more tokens ahead and gets its type.
     * 
     * @param numberAhead The number of tokens to peek ahead.
     * @return tokenType The token type of the peeked token.
     */
    private Token.tokenType peek(int numberAhead) {
        if (numberAhead >= tokenQueue.size())
            return null;
        if (numberAhead == 1) {
            return tokenQueue.peek().getType();
        } else {
            Object[] tokenArray = tokenQueue.toArray();
            if (numberAhead >= tokenArray.length)
                return null;
            else
                return ((Token) tokenArray[numberAhead - 1]).getType();
        }
    }
        /**
     * Searches for a math operator token (* / + -) and returns its type if found.
     * 
     * @return tokenType The type of math operator found
     */
    private tokenType searchForMathOperator() {
        tokenType sign = null;
        if (matchAndRemove(tokenType.MULT) != null)
            sign = tokenType.MULT;
        else if (matchAndRemove(tokenType.DIVIDE) != null)
            sign = tokenType.DIVIDE;
        else if (matchAndRemove(tokenType.MOD) != null)
            sign = tokenType.MOD;
        return sign;
    }

}
