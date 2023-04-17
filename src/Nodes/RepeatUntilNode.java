package Nodes;

import java.util.ArrayList;

public class RepeatUntilNode extends StatementNode {

    Node condition;
    ArrayList<StatementNode> statements;

    public RepeatUntilNode(Node condition, ArrayList<StatementNode> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    @Override
    public String toString() {
        String string = "(REPEAT UNTIL: " + condition + ", STATEMENTS[ ";
        for (StatementNode s : statements)
            string = string + s;
        string = string + "])";
        return string;
    }
    public Node getCondition()
    {
        return condition;
    }
    public ArrayList<StatementNode> getStatements(){
        return statements;
    }
}
 