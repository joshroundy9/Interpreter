package Nodes;

public class VariableReferenceNode extends Node{

    String name;
    Node arrayIndex;

    public VariableReferenceNode(String name, Node arrayIndex)
    {
        this.name = name;
        this.arrayIndex = arrayIndex;
    }
    public VariableReferenceNode(String name)
    {
    this.name = name;
    arrayIndex = null; 
    }
    @Override
    public String toString() {

        if(arrayIndex != null)
        return "(Variable: "+name+", array: "+arrayIndex+")";
        else return "(Variable: "+name+")";
    }
    public String getName()
    {
        return name;
    }
    public Node getArrayIndex()
    {
        return arrayIndex;
    } 
    
}
