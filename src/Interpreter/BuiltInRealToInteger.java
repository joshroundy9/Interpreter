package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInRealToInteger extends FunctionNode{
    public BuiltInRealToInteger()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    {
        if(!( params.get(0) instanceof RealDataType && params.get(1) instanceof IntegerDataType ))
        throw new SyntaxErrorException("Invalid data types inputted into RealToInteger function parameter types must be: Real input, Integer output.");
        if(params.size() == 2)
        { 
            if(params.get(1).getIsConstant()) throw new SyntaxErrorException("Variable output for RealToInteger function is constant.");
            ((IntegerDataType)params.get(1)).setValue((int)((RealDataType)params.get(0)).getValue());
        } else throw new SyntaxErrorException("Input into RealToInteger function must have two parameters.");
    }
}