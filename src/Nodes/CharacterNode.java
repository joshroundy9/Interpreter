package Nodes;

import Main.SyntaxErrorException;

public class CharacterNode extends Node{

    private Character value;

    public CharacterNode(Character value)
    {
        this.value = value;
    }
    public CharacterNode(String value) throws SyntaxErrorException
    {
        if(value.length() != 1 || value.length() != 0)
        throw new SyntaxErrorException("Invalid string input to CharacterNode constructor, must be of length 1 or 0.");
    }
    @Override
    public String toString() {
        return value+"";
    }
    public Character getValue() 
    {
        return value;
    }
}
