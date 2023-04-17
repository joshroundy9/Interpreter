package Interpreter;

import Main.SyntaxErrorException;
import Nodes.VariableNode.variableType;

public abstract class InterpreterDataType {
    private boolean isConstant;
    public abstract String toString();
    public abstract void fromString(String value) throws SyntaxErrorException;
    private Double minRange= null, maxRange= null;
    public boolean getIsConstant()
    {
        return isConstant;
    }
    public void setIsConstant(boolean isConstant)
    {
        this.isConstant = isConstant;
    }
    public void setMinRange(double minRange)
    {
        this.minRange = minRange;
    }
    public void setMaxRange(double maxRange)
    {
        this.maxRange = maxRange;
    }
    public Double getMaxRange()
    {
        return maxRange;
    }
    public Double getMinRange()
    {
        return minRange;
    }
    public static variableType getVariableType(InterpreterDataType data)
    {
        if(data instanceof StringDataType) return variableType.STRING;
        if(data instanceof CharacterDataType) return variableType.CHARACTER;
        if(data instanceof RealDataType) return variableType.REAL;
        if(data instanceof IntegerDataType) return variableType.INTEGER;
        if(data instanceof BooleanDataType) return variableType.BOOLEAN;
        return null;
    }
}
 