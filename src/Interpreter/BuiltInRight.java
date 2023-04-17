package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInRight extends FunctionNode{
    
    public BuiltInRight()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    {
        if(!(params.get(0) instanceof StringDataType && params.get(1) instanceof IntegerDataType && params.get(2) instanceof StringDataType))
        throw new SyntaxErrorException("Invalid data types inputted into Right function parameter types must be: String input, Integer length, String output.");
        if(params.size() == 3)
        { 
            char[] input = ((StringDataType)params.get(0)).getValue().toCharArray();
            int requestedLength = ((IntegerDataType)params.get(1)).getValue();
            String output = "";
            //checks if the length is more than the length asked by the user
            if(input.length<requestedLength)
            throw new SyntaxErrorException("The length of substring cannot surpass the length of the input string in Right function.");
            for(int i = input.length-requestedLength-1;i<input.length;i++)
            {
                output = output+input[i];
            }
            if(params.get(2).getIsConstant()) throw new SyntaxErrorException("Variable output for Right function is constant.");
            ((StringDataType)params.get(2)).fromString(output);
        } else throw new SyntaxErrorException("Input into Right function must have three parameters.");
    }
}