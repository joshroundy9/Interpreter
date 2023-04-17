package Nodes;

import java.util.ArrayList;

public class WhileNode extends StatementNode {

    private Node condition;
    private ArrayList<StatementNode> statements;

    public WhileNode(Node condition, ArrayList<StatementNode> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    @Override
    public String toString() {
        String string = "(WHILE: " + condition + ", STATEMENTS: ";
        for (StatementNode s : statements)
            string = string + s;
        string = string + ")";
        return string;
    }
    public Node getCondition()
    {
        return condition;
    }
    public ArrayList<StatementNode> getStatements()
    {
        return statements;
    }

} 
