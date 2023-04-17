package Nodes;

import Main.SyntaxErrorException;

public class IntegerNode extends Node{

    private int integer;

    public IntegerNode(int integer)
    {
        this.integer = integer;
    }
    public IntegerNode(String fromString) throws SyntaxErrorException
    {
        try{
            this.integer = Integer.parseInt(fromString);
        } catch(NumberFormatException e)
        {
            throw new SyntaxErrorException("Invalid integer provided to IntegerNode constructor "+fromString+".");
        }
    }
    @Override
    public String toString() {
        return integer+"";
    }
    public int getValue()
    {
        return integer; 
    }
    
}
