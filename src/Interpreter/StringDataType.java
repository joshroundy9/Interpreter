package Interpreter;

import Main.SyntaxErrorException;

public class StringDataType extends InterpreterDataType{
    
    private String value;
    public StringDataType(String value, boolean isConstant)
    {
        this.value = value;
        setIsConstant(isConstant);
    }
    public String getValue()
    {
        return value;
    }
    public String toString()
    {
        return value.toString();
    }
    public void fromString(String value) throws SyntaxErrorException
    {
        if(getMaxRange() != null)
        if(getMinRange()>= value.length() || getMaxRange() <= value.length())
        throw new SyntaxErrorException("Cannot set string value to "+value+" when its range is from "+getMinRange()+" to "+getMaxRange()+".");
        this.value = value;
    } 

    
}