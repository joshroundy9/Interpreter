package Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;
import Nodes.IfNode;
import Nodes.*;
import Nodes.MathOpNode;
import Nodes.Node;
import Nodes.ProgramNode;
import Nodes.RealNode;
import Nodes.StatementNode;
import Nodes.StringNode;
import Nodes.VariableNode;
import Nodes.VariableReferenceNode;
import Nodes.BooleanCompareNode.comparisonType;
import Nodes.MathOpNode.operationType;
import Nodes.VariableNode.variableType;

public class Interpreter {

    private ProgramNode program;

    public Interpreter(ProgramNode program) {
        this.program = program;
    }

    public void interpretProgram() throws SyntaxErrorException {
        // calls the start function
        interpretFunctionCall(new FunctionCallNode("start", new ArrayList<>()), new HashMap<>());
    }

    /**
     * Interprets the function passed in.
     * 
     * @param function
     * @throws SyntaxErrorException
     */
    private void interpretFunction(FunctionNode function, ArrayList<InterpreterDataType> inputParameters)
            throws SyntaxErrorException {
        HashMap<String, InterpreterDataType> variableMap = new HashMap<>();
        ArrayList<VariableNode> variableList = function.getVariables();
        // cover input parameters
        ArrayList<ParameterNode> names = function.getParamaters();
        for (int i = 0; i < names.size(); i++) {
            variableType inputType;
            if (((VariableNode) names.get(i).getParameter()).getIsArray()) {
                inputType = InterpreterDataType.getVariableType(((ArrayDataType) inputParameters.get(i)).getIndex(0));
                if (!(inputParameters.get(i) instanceof ArrayDataType))
                    throw new SyntaxErrorException(
                            "Array data type input by parameters and needed by function do not match.");
                if (((VariableNode) names.get(i).getParameter()).getType() != inputType)
                    throw new SyntaxErrorException(
                            "Array data type input by parameters and needed by function do not match.");
            } else
                inputType = InterpreterDataType.getVariableType(inputParameters.get(i));
            if (((VariableNode) names.get(i).getParameter()).getType() != inputType)
                throw new SyntaxErrorException("Data types input by parameters and needed by function do not match.");

            if (inputParameters.get(i).getIsConstant() == true && names.get(i).getIsVariable() == true)
                throw new SyntaxErrorException("In function call variable: " + inputParameters
                        + " is constant and must be variable to meet function parameters.");
            else if (names.get(i).getIsVariable() == false) {
                VariableNode inputForDuplicate = new VariableNode(null, inputType,null,true);
                InterpreterDataType duplicate = interpretVariable(inputForDuplicate, false);
                duplicate.fromString(inputParameters.get(i).toString());
                variableMap.put(((VariableNode) names.get(i).getParameter()).getName(),
                        duplicate);
            } else
                variableMap.put(((VariableNode) names.get(i).getParameter()).getName(), inputParameters.get(i));
        }

        // covers variables
        for (VariableNode var : variableList) {
            // in the parser if a value was constant, its variable node was instantly
            // assigned a value at declaration. Variable nodes do not have variables
            // assigned at declaration.
            boolean isConstant = var.getValue() != null;
            if (var.isArray()) {
                if (isConstant) {
                    ArrayDataType array = new ArrayDataType(null, true);
                    array.fromString(var.getValue());
                    array.setMaxIndex(((int) (double) var.getHighRange()));
                    array.setIsConstant(true);
                    variableMap.put(var.getName(), array);
                } else {
                    // arrays MUST have a set length when defined.
                    ArrayList<InterpreterDataType> list = new ArrayList<>();
                    for (int i = ((int) (double) var.getLowRange()); i < (int) (double) var.getHighRange(); i++)
                        list.add(i, interpretVariable(var, isConstant));
                    ArrayDataType array = new ArrayDataType(list, isConstant);
                    array.setMaxIndex(((int) (double) var.getHighRange()));
                    variableMap.put(var.getName(), array);
                }
            } else {
                InterpreterDataType value = interpretVariable(var, isConstant);
                if(var.getHighRange()!= null && var.getLowRange() != null) {
                    value.setMinRange(var.getLowRange());
                    value.setMaxRange(var.getHighRange());
                }
                variableMap.put(var.getName(), interpretVariable(var, isConstant));
            }
        }
        interpretBlock(function.getStatements(), variableMap);

    }

    /**
     * Interprets the current statment block which could be layered inside
     * functions, loops, if statements, ect.
     * 
     * @param statements
     * @throws SyntaxErrorException
     */
    private void interpretBlock(ArrayList<StatementNode> statements, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        for (StatementNode node : statements) {
            if (node instanceof AssignmentNode)
                interpretAssignment((AssignmentNode) node, variableMap);
            else if (node instanceof FunctionCallNode)
                interpretFunctionCall((FunctionCallNode) node, variableMap);
            else if (node instanceof ForNode)
                interpretFor((ForNode) node, variableMap);
            else if (node instanceof WhileNode)
                interpretWhile((WhileNode) node, variableMap);
            else if (node instanceof RepeatUntilNode)
                interpretRepeatUntil((RepeatUntilNode) node, variableMap);
            else if (node instanceof IfNode)
                interpretIf((IfNode) node, variableMap);

        }
    }

    private void interpretFunctionCall(FunctionCallNode node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {

        // for built in functions
        HashMap<String, FunctionNode> builtInFunctions = new HashMap<>();
        builtInFunctions.put("Left", new BuiltInLeft());
        builtInFunctions.put("Right", new BuiltInRight());
        builtInFunctions.put("Substring", new BuiltInSubstring());
        builtInFunctions.put("IntegerToReal", new BuiltInIntegerToReal());
        builtInFunctions.put("RealToInteger", new BuiltInRealToInteger());
        builtInFunctions.put("Read", new BuiltInRead());
        builtInFunctions.put("Write", new BuiltInWrite());
        builtInFunctions.put("SquareRoot", new BuiltInSquareRoot());
        builtInFunctions.put("Start", new BuiltInStart());
        builtInFunctions.put("End", new BuiltInEnd());
        builtInFunctions.put("GetRandom", new BuiltInGetRandom());
        ArrayList<InterpreterDataType> inputs = new ArrayList<>();

        for (ParameterNode param : node.getArguments())
            inputs.add(interpretExpression(param.getParameter(), variableMap));

        // built in functions and user made are seperated, however the lists are
        // processed after in the same way
        FunctionNode function;
        if (builtInFunctions.containsKey(node.getReferencedFunction())) {
            function = builtInFunctions.get(node.getReferencedFunction());
            function.execute(inputs);
            // isVariable checks are done within the built in functions and only have to be
            // done for user created functions.
        } else {

            function = program.getFunction(node.getReferencedFunction());

            if (function == null)
                throw new SyntaxErrorException("Invalid function name used \"" + node.getReferencedFunction() + "\".");

            if (function.getParamaters().size() == inputs.size())
                interpretFunction(function, inputs);
            else
                throw new SyntaxErrorException(
                        "Invalid number of parameters in function call for function " + function.getName() + ".");

        }
    }

    private void interpretAssignment(AssignmentNode node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        InterpreterDataType first = interpretVariableReference(node.getFirst(), variableMap);
        if (first.getIsConstant())
            throw new SyntaxErrorException("Cannot modify constant variable with value: " + first + ".");
        InterpreterDataType second = interpretExpression(node.getSecond(), variableMap);
        if (first.getClass().getSimpleName() != second.getClass().getSimpleName())
            throw new SyntaxErrorException("Cannot assign data type " + first.getClass().getSimpleName() + " to "
                    + second.getClass().getSimpleName() + ".");
        first.fromString(second.toString());
    }

    private void interpretFor(ForNode node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        if(!variableMap.containsKey(node.getVariable().getName()))
        throw new SyntaxErrorException("Iterator integer in for loops must be defined beforehand.");
        InterpreterDataType variable = interpretVariableReference(node.getVariable(), variableMap),
            lowBound = interpretExpression(node.getLowBound(), variableMap),
            highBound = interpretExpression(node.getHighBound(), variableMap);
        if(!(variable instanceof IntegerDataType)) throw new SyntaxErrorException("For loops can only take in integers for their iterator variables.");
        if (!(lowBound instanceof IntegerDataType && highBound instanceof IntegerDataType
                && variable instanceof IntegerDataType))
            throw new SyntaxErrorException("In for loops the variable, upper, and lower bounds must all be integers.");
        for (int i = ((IntegerDataType) lowBound).getValue(); i <= ((IntegerDataType) highBound).getValue(); i++) {
            ((IntegerDataType) variable).setValue(i);
            interpretBlock(node.getStatements(), variableMap);
        }
    }

    private void interpretRepeatUntil(RepeatUntilNode node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        boolean condition = interpretBooleanExpression(node.getCondition(), variableMap).getValue();
        while (!condition) {
            interpretBlock(node.getStatements(), variableMap);
            condition = interpretBooleanExpression(node.getCondition(), variableMap).getValue();
        }
    }

    private void interpretWhile(WhileNode node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        boolean condition = interpretBooleanExpression(node.getCondition(), variableMap).getValue();
        while (condition) {
            interpretBlock(node.getStatements(), variableMap);
            condition = interpretBooleanExpression(node.getCondition(), variableMap).getValue();
        }
    }

    private void interpretIf(IfNode node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        // if the statement does not have a condition it is an else statement so no
        // check will be made
        boolean condition;
        if (node.getCondition() == null)
            condition = true;
        else
            condition = interpretBooleanExpression(node.getCondition(), variableMap).getValue();
        if (condition)
            interpretBlock(node.getStatements(), variableMap);
        else if (node.getElse() != null)
            interpretIf(node.getElse(), variableMap);
        // if condition not met and no else is found, nothing happens
    }

    /*
     * Will be called in if statements, loops where the user can input true/false, a
     * boolean variable, or a booleancomparisonnode
     */
    private BooleanDataType interpretBooleanExpression(Node node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        if (node instanceof BooleanNode)
            return new BooleanDataType(((BooleanNode) node).getValue(), false);
        else if (node instanceof VariableReferenceNode) {
            InterpreterDataType result = interpretVariableReference(((VariableReferenceNode) node), variableMap);
            if (!(result instanceof BooleanDataType))
                throw new SyntaxErrorException("Condition must be a boolean.");
            else
                return (BooleanDataType) result;
        } else if (node instanceof BooleanCompareNode) {
            return interpretBooleanComparison((BooleanCompareNode) node, variableMap);
        } else
            throw new SyntaxErrorException("Invalid input provided for condition.");
    }

    /**
     * @param node
     * @param variableMap
     * @return BooleanDataType
     * @throws SyntaxErrorException
     */
    private BooleanDataType interpretBooleanComparison(BooleanCompareNode node,
            HashMap<String, InterpreterDataType> variableMap) throws SyntaxErrorException {
        // recursively checks if there are nested boolean comparisons
        if (node.getLeft() instanceof BooleanCompareNode && node.getRight() instanceof BooleanCompareNode) {
            boolean leftComparison = interpretBooleanComparison((BooleanCompareNode) node.getLeft(), variableMap)
                    .getValue(),
                    rightComparison = interpretBooleanComparison((BooleanCompareNode) node.getLeft(), variableMap)
                            .getValue();
            if (node.getComparison() == comparisonType.AND)
                return new BooleanDataType(leftComparison && rightComparison, false);
            else
                return new BooleanDataType(leftComparison || rightComparison, false);
        }
        // handles all cases involving when either side is either a data, variable
        // reference or mathop node.
        InterpreterDataType left = interpretExpression(node.getLeft(), variableMap);
        InterpreterDataType right = interpretExpression(node.getRight(), variableMap);
        // checks for each type and does the comparisons applicable to each type.
        if (left instanceof StringDataType) {
            if (!(right instanceof StringDataType) || node.getComparison() != comparisonType.EQUAL)
                throw new SyntaxErrorException("Strings can only be compared with '=' to other strings.");
            boolean resultVal = ((StringDataType) left).getValue().equals(((StringDataType) right).getValue());
            if(node.getInverted()) resultVal=!resultVal;
            return new BooleanDataType(resultVal,
                    false);
        } else if (left instanceof CharacterDataType) {
            if (!(right instanceof CharacterDataType) || node.getComparison() != comparisonType.EQUAL)
                throw new SyntaxErrorException("Characters can only be compared with '=' to other characters.");
            boolean resultVal= ((CharacterDataType) left).getValue().equals(((CharacterDataType) right).getValue());
            if(node.getInverted()) resultVal=!resultVal;
            return new BooleanDataType(resultVal, false);
        } else if (left instanceof IntegerDataType) {
            if (!(right instanceof IntegerDataType))
                throw new SyntaxErrorException("Integers can only be compared to other integers.");
            int leftVal, rightVal;
            boolean resultVal = true;
            // gets and intializes the left and right values
            leftVal = ((IntegerDataType) left).getValue();
            rightVal = ((IntegerDataType) right).getValue();

            switch (node.getComparison()) {
                case EQUAL:
                    resultVal = rightVal == leftVal;
                    break;
                case GREATER:
                    resultVal = rightVal > leftVal;
                    break;
                case LESS:
                    resultVal = rightVal < leftVal;
                    break;
                case GREATEREQUAL:
                    resultVal = rightVal >= leftVal;
                    break;
                case LESSEQUAL:
                    resultVal = rightVal <= leftVal;
                    break;
            }
            if(node.getInverted()) resultVal=!resultVal;
            return new BooleanDataType(resultVal, false);
        } else if (left instanceof RealDataType) {
            if (!(right instanceof RealDataType))
                throw new SyntaxErrorException("Integers can only be compared to other integers.");
            double leftVal, rightVal;
            boolean resultVal = true;
            // gets and intializes the left and right values
            leftVal = ((RealDataType) left).getValue();
            rightVal = ((RealDataType) right).getValue();

            switch (node.getComparison()) {
                case EQUAL:
                    resultVal = rightVal == leftVal;
                    break;
                case GREATER:
                    resultVal = rightVal > leftVal;
                    break;
                case LESS:
                    resultVal = rightVal < leftVal;
                    break;
                case GREATEREQUAL:
                    resultVal = rightVal >= leftVal;
                    break;
                case LESSEQUAL:
                    resultVal = rightVal <= leftVal;
                    break;
            }
            if(node.getInverted()) resultVal=!resultVal;
            return new BooleanDataType(resultVal, false);
        } else if(left instanceof BooleanDataType)
        {
            if(!(right instanceof BooleanDataType))
            throw new SyntaxErrorException("Booleans can only be compared to other booleans.");
            boolean output = false, rightVal = ((BooleanDataType)right).getValue(), leftVal = ((BooleanDataType)left).getValue();
            switch(node.getComparison())
            {
                case OR: output = leftVal || rightVal; break;
                case AND: output = leftVal && rightVal; break;
            }
            if(node.getInverted()) output = !output;
            return new BooleanDataType(output, false);
        } else
            throw new SyntaxErrorException("Invalid node provided into InterpretBooleanComparison.");
        /* should only occur if something goesn wrong in the interpreter */
    }

    private InterpreterDataType interpretExpression(Node node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        if (node instanceof MathOpNode)
            return interpretMathOpNode((MathOpNode) node, variableMap);
        else if (node instanceof BooleanCompareNode)
            return interpretBooleanComparison((BooleanCompareNode) node, variableMap);
        else if (node instanceof VariableReferenceNode)
            return interpretVariableReference((VariableReferenceNode) node, variableMap);
        else if (node instanceof VariableNode) {
            ArrayDataType value = new ArrayDataType(null, false);
            value.fromString(((VariableNode) node).getValue());
            return value;
        } else
            return interpretDataNode(node);
    }

    private InterpreterDataType interpretMathOpNode(MathOpNode node, HashMap<String, InterpreterDataType> variableMap)
            throws SyntaxErrorException {
        InterpreterDataType right = interpretExpression(node.getRight(), variableMap),
                left = interpretExpression(node.getLeft(), variableMap);

        if (left instanceof StringDataType) {
            if (!(right instanceof StringDataType) || node.getOperation() != operationType.ADD)
                throw new SyntaxErrorException("Strings can only be added to other strings.");
            return new StringDataType(((StringDataType) left).getValue() + ((StringDataType) right).getValue(), false);
        } else if (left instanceof RealDataType) {
            if (!(right instanceof RealDataType))
                throw new SyntaxErrorException("Reals can only be make expressions with to other reals.");
            Double result = null, leftVal = ((RealDataType) left).getValue(),
                    rightVal = ((RealDataType) right).getValue();
            switch (node.getOperation()) {
                case ADD:
                    result = leftVal + rightVal;
                    break;
                case SUBTRACT:
                    result = leftVal - rightVal;
                    break;
                case MULTIPLY:
                    result = leftVal * rightVal;
                    break;
                case DIVIDE:
                    result = leftVal / rightVal;
                    break;
                case MOD:
                    result = leftVal % rightVal;
                    break;
            }
            return new RealDataType(result, false);
        } else if (left instanceof IntegerDataType) {
            if (!(right instanceof IntegerDataType))
                throw new SyntaxErrorException("Reals can only make expressions with to other reals.");
            int result = 0, leftVal = ((IntegerDataType) left).getValue(),
                    rightVal = ((IntegerDataType) right).getValue();
            switch (node.getOperation()) {
                case ADD:
                    result = leftVal + rightVal;
                    break;
                case SUBTRACT:
                    result = leftVal - rightVal;
                    break;
                case MULTIPLY:
                    result = leftVal * rightVal;
                    break;
                case DIVIDE:
                    result = leftVal / rightVal;
                    break;
                case MOD:
                    result = leftVal % rightVal;
                    break;
            }
            return new IntegerDataType(result, false);
        } else
            throw new SyntaxErrorException("Invalid data type found in MathOpNode: " + left.getClass() + ".");
    }

    private InterpreterDataType interpretVariableReference(VariableReferenceNode node,
            HashMap<String, InterpreterDataType> variableMap) throws SyntaxErrorException {
        /*
         * TODO: ADD ARRAY SUPPORT
         */
        if (node.getArrayIndex() == null) {
            if (variableMap.containsKey(node.getName()))
                return variableMap.get(node.getName());
            else
                throw new SyntaxErrorException("Invalid variable referenced \"" + node.getName() + "\".");
        } else {
            InterpreterDataType arrayIndex = interpretExpression(node.getArrayIndex(), variableMap);
            if (!(arrayIndex instanceof IntegerDataType))
                throw new SyntaxErrorException("Array index must be integer data type.");
            ArrayDataType arrayReferenced = null;

            if (variableMap.containsKey(node.getName()) && variableMap.get(node.getName()) instanceof ArrayDataType)
                arrayReferenced = (ArrayDataType) variableMap.get(node.getName());
            else
                throw new SyntaxErrorException("Invalid array referenced \"" + node.getName() + "\".");

            return arrayReferenced.getIndex(((IntegerDataType) arrayIndex).getValue());
        }

    }

    private InterpreterDataType interpretVariable(VariableNode var, boolean isConstant) throws SyntaxErrorException {
        InterpreterDataType value = null;
        switch (var.getType()) {
            case BOOLEAN:
                value = new BooleanDataType(null, isConstant);
                break;
            case STRING:
                value = new StringDataType(null, isConstant);
                break;
            case REAL:
                value = new RealDataType(null, isConstant);
                break;
            case CHARACTER:
                value = new CharacterDataType(null, isConstant);
                break;
            case INTEGER:
                value = new IntegerDataType(null, isConstant);
                break;
            case ARRAY:
                value = new ArrayDataType(null, isConstant);
        }
        if (isConstant)
            value.fromString(var.getValue());
        return value;

    }

    private InterpreterDataType interpretDataNode(Node node) {
        if (node instanceof StringNode)
            return new StringDataType(((StringNode) node).getValue(), false);
        else if (node instanceof IntegerNode)
            return new IntegerDataType(((IntegerNode) node).getValue(), false);
        else if (node instanceof RealNode)
            return new RealDataType((double) ((RealNode) node).getValue(), false);
        else if (node instanceof IntegerNode)
            return new CharacterDataType(((CharacterNode) node).getValue(), false);
        else if (node instanceof BooleanNode)
            return new BooleanDataType(((BooleanNode) node).getValue(), false);
        return null;
    }

}
