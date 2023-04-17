package Interpreter;

import Main.SyntaxErrorException;

public class BooleanDataType extends InterpreterDataType{
    
    private Boolean value;
    public BooleanDataType(Boolean value, boolean isConstant)
    {
        this.value = value;
        setIsConstant(isConstant);
    }
    public boolean getValue()
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
            this.value = Boolean.parseBoolean(value);
        } catch (Exception e)
        {throw new SyntaxErrorException("Invalid boolean value found, please change variable type or value to true or false.");}
    }
    
}
