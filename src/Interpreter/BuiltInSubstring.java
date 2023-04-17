package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInSubstring extends FunctionNode{
    
    public BuiltInSubstring()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    {
        if(!(params.get(0) instanceof StringDataType && params.get(1) instanceof IntegerDataType && params.get(2) instanceof IntegerDataType && params.get(3) instanceof StringDataType))
        throw new SyntaxErrorException("Invalid data types inputted into Substring function parameter types must be: String input, Integer index, Integer length, String output");
        if(params.size() == 4)
        { 
            if(params.get(3).getIsConstant() == false) throw new SyntaxErrorException("Variable output for Substring function is constant.");

            char[] input = ((StringDataType)params.get(0)).getValue().toCharArray();
            int requestedLength = ((IntegerDataType)params.get(2)).getValue(),
            requestedIndex = ((IntegerDataType)params.get(1)).getValue();
            String output = "";
            //checks if the length is more than the length asked by the user
            if(input.length>requestedLength+requestedIndex)
            throw new SyntaxErrorException("The length of substring cannot surpass the length of the input string minus the starting position in Substring function.");
            for(int i = requestedIndex;i<requestedLength+requestedIndex;i++)
            {
                output = output+input[i];
            }

            ((StringDataType)params.get(3)).fromString(output);
        } else throw new SyntaxErrorException("Input into Substring function must have four parameters.");
    }
}