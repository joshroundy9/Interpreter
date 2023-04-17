package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInStart extends FunctionNode{
    public BuiltInStart()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    {
        if(!(params.get(0) instanceof ArrayDataType && params.get(1) instanceof IntegerDataType))
        throw new SyntaxErrorException("Invalid data types inputted into Start function parameter types must be: Array array, Integer output.");
        if(params.size() != 2)
            throw new SyntaxErrorException("Input into Start function must have two parameters."); 

        ArrayList<InterpreterDataType> array = ((ArrayDataType)params.get(0)).getValue();
        int output;
        for(output = 0;output < array.size();output++)
            if(!array.get(output).toString().equals(""))break;
        
        if(array.get(output) == null && output == array.size()-1)
        throw new SyntaxErrorException("Cannot find array starting index on uninitialized array.");
        
        if(params.get(1).getIsConstant()) throw new SyntaxErrorException("Variable output for Start function is constant, must be var.");
        ((IntegerDataType)params.get(1)).setValue(output);
        
    }
    
}
