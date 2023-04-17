package Nodes;

import Main.SyntaxErrorException;

public class BooleanNode extends Node{

    private boolean value;

    public BooleanNode(boolean value)
    {
        this.value = value;
    }
    public BooleanNode(String fromString) throws SyntaxErrorException
    {
        if(fromString.equalsIgnoreCase("true"))
        this.value = true;
        else if(fromString.equalsIgnoreCase("false"))
        this.value = false;
        else throw new SyntaxErrorException("Invalid boolean provided to BooleanNode constructor '"+fromString+"'.");
    }
    @Override
    public String toString() { 
        return value+"";
    }
    public boolean getValue()
    {
        return value;
    }
    
}
