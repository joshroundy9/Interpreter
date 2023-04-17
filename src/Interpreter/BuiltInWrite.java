package Interpreter;

import java.util.ArrayList;

import Nodes.FunctionNode;

public class BuiltInWrite extends FunctionNode {

    public BuiltInWrite()
    {
        super("Write",null, null, null);
        
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> params)
    {
        for(InterpreterDataType type : params)
        { 
            //seperate toString made in each of the 
            System.out.print(type+" ");
        }
        System.out.println();
    }
    
}
