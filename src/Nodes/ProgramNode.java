package Nodes;

import java.util.HashMap;
import java.util.List;

public class ProgramNode extends Node{

    private HashMap<String,FunctionNode> functions;

    public ProgramNode()
    {
        functions = new HashMap<>();
    }
    public void addFunction(FunctionNode node)
    {
        functions.put(node.getName(), node);
    }
    public FunctionNode getFunction(String id)
    {
        if(!functions.containsKey(id)) return null;
        else return functions.get(id);
    }
    @Override
    public String toString() {
        String output = "";
        for(FunctionNode n : functions.values())
            output = output+n;
        return output;
    }
    public void print()
    {
        List<FunctionNode> functionList = (List<FunctionNode>)functions.values();
        for(FunctionNode f: functionList) System.out.println(f.toString());
    }
    public HashMap<String,FunctionNode> getFunctionMap()
    {
        return functions;
    }
} 
