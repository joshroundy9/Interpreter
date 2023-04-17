package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInEnd extends FunctionNode{
    public BuiltInEnd()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    {
        if(!(params.get(0) instanceof ArrayDataType && params.get(1) instanceof IntegerDataType))
        throw new SyntaxErrorException("Invalid data types inputted into End function parameter types must be: Array array, Integer end");
        if(params.size() == 2)
        { 
            int output = ((ArrayDataType)params.get(0)).getValue().size();
            
            if(params.get(1).getIsConstant()) throw new SyntaxErrorException("Variable output for End function is constant, must be var.");
            ((IntegerDataType)params.get(1)).setValue(output);
        } else throw new SyntaxErrorException("Input into End function must have two parameters.");
    }
    
}
