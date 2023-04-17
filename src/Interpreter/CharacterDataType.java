package Interpreter;

import Main.SyntaxErrorException;

public class CharacterDataType extends InterpreterDataType{

    private Character value;
    public CharacterDataType(Character value, boolean isConstant)
    {
        this.value = value;
        setIsConstant(isConstant);
    }
    public Character getValue()
    {
        return value;
    }
    public String toString()
    {
        return value.toString();
    } 
    public void fromString(String value) throws SyntaxErrorException
    {
        char[] arr = value.toCharArray();
        if(((Character)arr[0]).equals('\'') &&((Character)arr[2]).equals('\'')) this.value = arr[1];
        else if(arr.length==1) this.value = arr[0];
        else throw new SyntaxErrorException("Invalid character inputted into interpreter.");
    }
}
