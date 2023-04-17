package Nodes;

import java.util.ArrayList;

public class ForNode extends StatementNode{ 
    private VariableReferenceNode variable;
    private Node lowBound;
    private Node upBound;
    private ArrayList<StatementNode> statements;

    public ForNode(VariableReferenceNode variable, Node lowBound, Node upBound, ArrayList<StatementNode> statements)
    {
        this.lowBound=lowBound;
        this.upBound = upBound;
        this.variable = variable;
        this.statements = statements;
    }
    @Override
    public String toString() {
        String string = "(FOR: VAR: "+variable+", FROM: "+lowBound+", TO: "+upBound+", STATEMENTS: ";
        for(StatementNode s : statements)
        string = string+s;
        string = string + ")";
        return string; 
    }
    public VariableReferenceNode getVariable()
    {
        return variable;
    }
    public Node getLowBound()
    {
        return lowBound;
    }
    public Node getHighBound()
    {
        return upBound;
    }
    public ArrayList<StatementNode> getStatements()
    {
        return statements;
    }
}
