package Nodes;

public class BooleanCompareNode extends Node{
    public enum comparisonType {
        GREATER,GREATEREQUAL,LESS,LESSEQUAL,EQUAL, OR, AND
    }
    private comparisonType type;
    private Node left;
    private Node right;
    private boolean inverted;
    public BooleanCompareNode(Node left,comparisonType type, Node right,boolean inverted)
    {
        this.left = left;
        this.right = right;
        this.type = type;
        this.inverted = inverted;
    }

    @Override
    public String toString() {
        return "("+type+"; "+left+", "+right+", INVERTED:"+inverted+")";
    }
    public Node getLeft() 
    {
        return left;
    }
    public Node getRight()
    {
        return right;
    }
    public comparisonType getComparison()
    {
        return type;
    }
    public boolean getInverted()
    {
        return inverted;
    }
    
}
