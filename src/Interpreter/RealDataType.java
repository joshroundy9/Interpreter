package Interpreter;

import Main.SyntaxErrorException;

public class RealDataType extends InterpreterDataType{
    
    private Double value;
    public RealDataType(Double value, boolean isConstant)
    {
        this.value = value;
        setIsConstant(isConstant);
    }
    public double getValue()
    {
        return value;
    }
    public String toString()
    {
        return value.toString();
    }
    public void fromString(String value) throws SyntaxErrorException
    { 
        try{
        this.value = Double.parseDouble(value);
        } catch(NumberFormatException n)
        {
            throw new SyntaxErrorException("Invalid real value entered, please fix and enter valid number.");
        }
    }
    public void setValue(Double value) throws SyntaxErrorException
    {
        if(getMaxRange() != null)
        if(getMinRange()>= value || getMaxRange() <= value)
        throw new SyntaxErrorException("Cannot set real value to "+value+" when its range is from "+getMinRange()+" to "+getMaxRange()+".");
        this.value = value;
    }
    
}