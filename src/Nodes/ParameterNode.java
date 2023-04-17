package Nodes;

public class ParameterNode extends Node{

    private Node variable;
    private boolean isVariable;

    public ParameterNode(Node variable, boolean isVariable)
    {
        this.variable = variable;
        this.isVariable = isVariable;
    }

    @Override
    public String toString() {
        
            return "[PARAM:"+variable+" IsVariable:"+isVariable+"]";
    }
    public boolean getIsVariable()
    {
        return isVariable;
    }
    public Node getParameter(){
        return variable;
    }
     
}
