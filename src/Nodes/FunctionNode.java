package Nodes;

import java.util.ArrayList;

import Interpreter.InterpreterDataType;
import Main.SyntaxErrorException;

public class FunctionNode extends Node{ 
    protected ArrayList<ParameterNode> parameters; 
    protected ArrayList<VariableNode> variables;
    protected ArrayList<StatementNode> statements;
    protected String name;
    

    public FunctionNode(String name, ArrayList<ParameterNode> parameters,ArrayList<VariableNode> variables, ArrayList<StatementNode> statements)
    {
        this.statements = statements;
        this.parameters = parameters;
        this.variables = variables;
        this.name = name;
    }
    @Override
    public String toString() {
        
        return "(F["+ name+"] param["+parameters.toString()+"] vars["+variables.toString()+"] stmts["+statements.toString()+"])";
    } 
    public boolean isVariadic()
    {
        if(name == null) return false;
        if(name.equals("Read") || name.equals("Write")) return true;
        else return false;
    }
    public String getName()
    {
        return name;
    }
    public ArrayList<ParameterNode> getParamaters()
    {
        return parameters;
    }
    public ArrayList<VariableNode> getVariables()
    {
        return variables;
    }
    public ArrayList<StatementNode> getStatements()
    {
        return statements;
    }
    public  void execute(ArrayList<InterpreterDataType> params) throws SyntaxErrorException{}
}
