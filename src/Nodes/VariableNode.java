package Nodes;

import Main.Token;

public class VariableNode extends Node{
    public enum variableType 
    {
        INTEGER,REAL,STRING,CHARACTER,BOOLEAN,ARRAY
    }
    
    /** 
     * @param token
     * @return variableType
     */
    public static variableType getTypeFromValue(Token token)
    {
        switch(token.getType())
        {
            case NUMBER:
            if(token.getValue().contains("."))
            return variableType.REAL;
            else return variableType.INTEGER;
            case TRUE:
            return variableType.BOOLEAN;
            case FALSE:
            return variableType.BOOLEAN;
            case STRINGLITERAL:
            return variableType.STRING;
            case CHARLITERAL:
            return variableType.CHARACTER;
            default: 
            return null;
        } 
    }
    private String name;
    private String value;
    private variableType type;
    private boolean isVariable;
    /*I used a double for the ranges because it works just fine for strings and integers if only incremented by integers, and also works for reals too.
        The interpreter will deal with the problems of mismatched or improper range values.*/
    private Double lowRange;
    private Double highRange;
    private boolean isArray;

    /** Constructor used for constant variables (cannot have ranges)
     * 
     * @param name
     * @param type
     * @param value 
     * @param isVariable
     */
    public VariableNode(String name, variableType type, String value, boolean isVariable)
    {
        this.name = name;
        this.value = value;
        this.type = type;
        this.isVariable = isVariable;
        lowRange = null;
        highRange = null;
        this.isArray = false;
    }
    
    /** Constructor used for variables that have ranges.
     * 
     * @param name
     * @param type
     * @param isVariable
     */
    public VariableNode(String name, variableType type, double lowRange, double highRange,boolean isArray)
    {
        this.name = name;
        this.value = null;
        this.type = type;
        this.isVariable = true;
        this.lowRange = lowRange;
        this.highRange = highRange;
        this.isArray = isArray;
    }
    /** Constructor used for variables that do not have ranges.
     * 
     * @param name
     * @param type
     * @param isVariable
     */
    public VariableNode(String name, variableType type, boolean isVariable,boolean isArray)
    {
        this.name = name;
        this.value = null;
        this.type = type;
        this.isVariable = isVariable;
        lowRange = null;
        highRange = null;
        this.isArray = isArray;

    }
    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        if(isVariable)
        {
            String base;
            if(isArray) base = "Variable array";
            else base = "Variable";
            if(lowRange != null)
            return "("+base+" "+name+" with type "+type+" and value of "+value+" with a range of "+lowRange+" to "+highRange+")";
            else return "("+base+" "+name+" with type "+type+" and value of "+value+")";
        } else
        {if(isArray)
            return "(Constant array "+name+" with type "+type+" and value of "+value+")";
            else return "(Constant "+name+" with type "+type+" and value of "+value+")";
        }
    }
    public boolean isVariable()
    {
        return isVariable;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public String getName()
    {
        return name;

    }
    public String getValue()
    {
        return value;

    }
    public variableType getType()
    {
        return type;
    }
    public boolean isArray()
    {
        return isArray;
    }
    public Double getHighRange()
    {
        return highRange;
    }
    public Double getLowRange()
    {
        return lowRange;
    }
    public boolean getIsArray()
    {
        return isArray;
    }

    
}
