package Nodes;


import java.util.ArrayList;

public class IfNode extends StatementNode {
    private Node condition;
    private ArrayList<StatementNode> statements;
    private StatementNode elseNode; //if null do nothing 

    public IfNode(Node condition, ArrayList<StatementNode> statements, StatementNode elseNode)
    {
        this.condition = condition;
        this.statements = statements;
        this.elseNode = elseNode;
    }

    @Override
    public String toString() {
        String string = "{IF: "+condition+", STATEMENTS: ";
        for(StatementNode s : statements)
        {
            string = string+s;
        }
        string = string+", ELSE: "+elseNode+"}";
        return string;
    } 
    public Node getCondition()
    {
        return condition;
    }
    public IfNode getElse()
    {
        return (IfNode) elseNode;
    }
    public ArrayList<StatementNode> getStatements()
    {
        return statements;
    }
}
