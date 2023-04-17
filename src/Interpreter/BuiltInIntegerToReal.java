package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInIntegerToReal extends FunctionNode{
    public BuiltInIntegerToReal()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    { 
        if(!(params.get(0) instanceof IntegerDataType && params.get(1) instanceof RealDataType))
        throw new SyntaxErrorException("Invalid data types inputted into IntegerToReal function parameter types must be: Integer input, Real real output.");
        if(params.size() == 2)
        { 
            if(params.get(1).getIsConstant() == true) throw new SyntaxErrorException("Variable output for IntegerToReal function is constant.");
            ((RealDataType) params.get(1)).setValue((double)((IntegerDataType)params.get(0)).getValue());
        } else throw new SyntaxErrorException("Input into IntegerToReal function must have two parameters.");
    }
}
