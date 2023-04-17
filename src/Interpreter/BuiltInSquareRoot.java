package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInSquareRoot extends FunctionNode{
    
    public BuiltInSquareRoot()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    {
        if(!(params.get(0) instanceof RealDataType && params.get(1) instanceof RealDataType))
        throw new SyntaxErrorException("Invalid data types inputted into SquareRoot function parameter types must be: Real, Real.");
        if(params.size() == 2)
        { 
            if(params.get(1).getIsConstant()) throw new SyntaxErrorException("Variable output for SquareRoot function is constant.");

            ((RealDataType)params.get(1)).setValue(Math.sqrt(((RealDataType)params.get(0)).getValue()));
        } else throw new SyntaxErrorException("Input into SquareRoot function must have two parameters.");
    }
}
