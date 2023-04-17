package Nodes;

import Main.Token;

public class MathOpNode extends Node {

    public enum operationType {
        ADD,MULTIPLY,DIVIDE,SUBTRACT,MOD,
        }

    private operationType type;
    private Node leftSubNode;
    private Node rightSubNode;

    public MathOpNode(operationType type, Node leftSubNode, Node rightSubNode)
    {
        this.type = type;
        this.rightSubNode = rightSubNode;
        this.leftSubNode = leftSubNode;
    }
    @Override
    public String toString() {
        
        return "("+type.toString()+"; "+leftSubNode.toString()+"; "+rightSubNode.toString()+")";
    }
    public static operationType getType(Token.tokenType type)
        {
            switch (type)
            {
                case ADD: return operationType.ADD;
                case SUBTRACT: return operationType.SUBTRACT;
                case MULT: return operationType.MULTIPLY;
                case DIVIDE: return operationType.DIVIDE;
                case MOD: return operationType.MOD;
                case LPAREN: return operationType.MULTIPLY;
            }
            return null;
        }
    public void setRightNode(Node node)
    { 
        rightSubNode = node;
    }
    public void setLeftNode(Node node)
    {
        leftSubNode = node;
    }
    public void setType(operationType type)
    {
        
    }
    public operationType getOperation()
    {
        return type;
    }
    public Node getLeft()
    {
        return leftSubNode;
    }
    public Node getRight()
    {
        return rightSubNode;
    }
}
