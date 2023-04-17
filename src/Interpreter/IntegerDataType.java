package Interpreter;

import Main.SyntaxErrorException;

public class IntegerDataType extends InterpreterDataType{
    private Integer value;

    public IntegerDataType(Integer value, boolean isConstant)
    {
        this.value = value;
        setIsConstant(isConstant);
    }
    public int getValue()
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
        this.value = Integer.parseInt(value);
        } catch(NumberFormatException n)
        {
            throw new SyntaxErrorException("Invalid real value entered, please fix and enter valid number.");
        }
    }
    public void setValue(int value) throws SyntaxErrorException
    {
        if(getMaxRange() != null)
        if(getMinRange()>= value || getMaxRange() <= value)
        throw new SyntaxErrorException("Cannot set integer value to "+value+" when its range is from "+getMinRange()+" to "+getMaxRange()+".");
        this.value = (Integer)value;
    }
    
}
