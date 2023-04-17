package Interpreter;

import java.util.ArrayList;
import java.util.Scanner;

import Main.SyntaxErrorException;
import Nodes.FunctionNode;

public class BuiltInRead extends FunctionNode{
    Scanner scanner;
    public BuiltInRead()
    {
      super("Read", null, null,null);
      scanner = new Scanner(System.in);
    }
    @Override
    public void execute(ArrayList<InterpreterDataType> list) throws SyntaxErrorException
    {
        for(InterpreterDataType data : list)
        { 
            //the check if the inputted string is valid is part of the fromString method in the DataType
            if(data.getIsConstant()) throw new SyntaxErrorException("Any parameters passed into Read must be variable.");
           data.fromString(scanner.nextLine());
            
        }
    }
}
