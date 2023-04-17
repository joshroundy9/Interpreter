package Nodes;

import Main.SyntaxErrorException;

public class RealNode extends Node {
    private Double value;

    public RealNode(double value)
    {
        this.value = value;
    }

    public RealNode(String value) throws SyntaxErrorException
    {
        try{
        this.value = Double.parseDouble(value);
        } catch(Exception e){
            throw new SyntaxErrorException("Invalid float value provided to RealNode constructor.");
        }
    }
    
    @Override
    public String toString() {
        return value+"";
    }
    public double getValue()
    {
        return value;
    }

    
}
 