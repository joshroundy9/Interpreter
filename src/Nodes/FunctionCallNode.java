package Nodes;

import java.util.ArrayList;

public class FunctionCallNode extends StatementNode{
    String referencedFunction;
    ArrayList<ParameterNode>  arguments;
    public FunctionCallNode(String referencedFunction, ArrayList<ParameterNode> arguments)
    {
        this.referencedFunction = referencedFunction;
        this.arguments = arguments;
    }
    @Override
    public String toString() {
        String result = "(FunctionCall: "+referencedFunction+", Arguments";
        for(Node n : arguments)
        {
            result = result+" "+n;
        }
        return result+")";
    }
    
    public String getReferencedFunction()
    {
        return referencedFunction; 
    }
    public ArrayList<ParameterNode> getArguments()
    {
        return arguments;
    }
}
