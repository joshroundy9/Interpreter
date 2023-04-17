package Interpreter;

import java.util.ArrayList;
import java.util.Random;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInGetRandom extends FunctionNode{
    public BuiltInGetRandom()
    {
        super(null, null,null,null);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException
    {
        if(!(params.get(0) instanceof IntegerDataType))
        throw new SyntaxErrorException("Invalid data types inputted into GetRandom function parameter types must be: Integer output.");
        if(params.size() == 1)
        { 
            if(params.get(0).getIsConstant() == true) throw new SyntaxErrorException("Variable input into GetRandom function is constant.");
            Random random = new Random();
            ((IntegerDataType)params.get(0)).setValue(random.nextInt());
        } else throw new SyntaxErrorException("Input into GetRandom function must have one parameter.");
    }
}