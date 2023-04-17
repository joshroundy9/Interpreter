package Main;

import java.util.ArrayList;
import java.util.HashMap;

import Nodes.AssignmentNode;
import Nodes.ForNode;
import Nodes.FunctionNode;
import Nodes.IfNode;
import Nodes.*;
import Nodes.ProgramNode;
import Nodes.RepeatUntilNode;
import Nodes.StatementNode;
import Nodes.VariableNode;
import Nodes.VariableReferenceNode;
import Nodes.WhileNode;
import Nodes.VariableNode.variableType;

public class SemanticAnalysis {
    
    /*
     * Takes the program, iterates over its functions then calls the check function function on each of them
     */
    public void CheckAssignments(ProgramNode programNode) throws SyntaxErrorException
    {
        HashMap<String,FunctionNode> functions = programNode.getFunctionMap();
        for(FunctionNode function : functions.values())
        {
            checkFunction(function);
        }
    }
    /*
     * Creates a variable hashmap with name, variabletype then adds the functions given parameters to it then checks the functions block.
     */
    public void checkFunction(FunctionNode functionNode) throws SyntaxErrorException
    {
        HashMap<String,variableType> variableList = new HashMap<>();
        for(ParameterNode parameter : functionNode.getParamaters())
            variableList.put(((VariableNode)parameter.getParameter()).getName(),((VariableNode)parameter.getParameter()).getType());
        for(VariableNode variableNode : functionNode.getVariables())
            variableList.put(variableNode.getName(),variableNode.getType());

        checkBlock(functionNode.getStatements(), variableList);
    }
    /*
     * Recursively checks the statements and their blocks for assignment nodes, in the case of if statements they are checked for elses as well.
     */
    public void checkBlock(ArrayList<StatementNode> statements,HashMap<String,variableType> variableList) throws SyntaxErrorException
    {
        for(StatementNode statement : statements)
        {
            if(statement instanceof AssignmentNode)
            {
                CheckAssignment((AssignmentNode)statement, variableList);
            } else if(statement instanceof IfNode)
            {
                IfNode ifNode = ((IfNode)statement);
                checkBlock(ifNode.getStatements(), variableList);
                if(ifNode.getElse() != null)
                checkBlock(ifNode.getElse().getStatements(), variableList);
            } else if(statement instanceof WhileNode)
            {
                checkBlock(((WhileNode)statement).getStatements(), variableList);
            } else if(statement instanceof ForNode)
            {
                checkBlock(((ForNode)statement).getStatements(), variableList);
            }else if(statement instanceof RepeatUntilNode)
            {
                checkBlock(((RepeatUntilNode)statement).getStatements(), variableList);
            }
        }

    }
    /*
     * Given a mathOpNode this function returns its variable type if both sides are equal, if they are not equal, an exception is thrown.
     */
    public variableType getMathOpNodeType(MathOpNode mathOpNode,HashMap<String,variableType> variableList) throws SyntaxErrorException
    {
        variableType first = getVariableTypeFromNode(mathOpNode.getLeft(), variableList)
        ,second;
        if(mathOpNode.getRight() instanceof MathOpNode)
        second = getMathOpNodeType((MathOpNode)mathOpNode.getRight(), variableList);
        else second = getVariableTypeFromNode(mathOpNode.getRight(), variableList);
        
        if(second != first) throw new SyntaxErrorException("Types in MathOpNode:"+mathOpNode+" do not match.");
        else return first;
    }
    /*
     * checks all assignment nodes found in the blocks and uses the variable map and if a constant is given on the right side uses the type of node 
     * it is to find the variable type, if they are not equal it throws an exception.
     */
    public void CheckAssignment(AssignmentNode assignmentNode, HashMap<String,variableType> variableList) throws SyntaxErrorException
    {
        if(!variableList.containsKey(assignmentNode.getFirst().getName()))
        throw new SyntaxErrorException("AssignmentNode "+assignmentNode+" has invalid first variable.");
        variableType correctType = variableList.get(assignmentNode.getFirst().getName()), 
        secondType;
        if(assignmentNode.getSecond() instanceof MathOpNode)
        secondType = getMathOpNodeType((MathOpNode)assignmentNode.getSecond(), variableList);
        else secondType = getVariableTypeFromNode(assignmentNode.getSecond(), variableList);
        
        if(correctType != secondType)
        throw new SyntaxErrorException("Types in assignment node: "+assignmentNode+" do not match.");
    }
    /*
     * Given a variable reference node or a constant node, this function returns its variableType
     */
    private variableType getVariableTypeFromNode(Node node, HashMap<String,variableType> variableList) throws SyntaxErrorException
    {
        if(node instanceof VariableReferenceNode)
        return variableList.get(((VariableReferenceNode)node).getName());
        else {
            Node assignmentValue = node;
            if(assignmentValue instanceof IntegerNode)
            return variableType.INTEGER;
            else if(assignmentValue instanceof StringNode)
            return variableType.STRING;
            else if(assignmentValue instanceof RealNode)
            return variableType.REAL;
            else if(assignmentValue instanceof BooleanNode)
            return variableType.BOOLEAN;
            else if(assignmentValue instanceof CharacterNode)
            return variableType.CHARACTER;
            else throw new SyntaxErrorException("Invalid type for node: "+node+".");
        }
    }
}
