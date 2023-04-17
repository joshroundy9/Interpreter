package Nodes;

public class AssignmentNode extends StatementNode{
    Node firstVariable;
    Node secondVariable;
    
    public AssignmentNode(Node firstVariable, Node secondVariable)
    {
        this.firstVariable = firstVariable;
        this.secondVariable = secondVariable;

    }

    public String toString()
    {
        return "(ASSIGNMENT: "+firstVariable+" := "+secondVariable+")";
    }
    public VariableReferenceNode getFirst()
    {
        return (VariableReferenceNode)firstVariable;
    }
    public Node getSecond() 
    {
        return secondVariable;
    }
}
