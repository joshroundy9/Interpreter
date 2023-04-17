package Interpreter;

import java.util.ArrayList;

import Main.SyntaxErrorException;


public class ArrayDataType extends InterpreterDataType {
    private ArrayList<InterpreterDataType> value;
    private int maxIndex = 1000;
    public ArrayDataType(ArrayList<InterpreterDataType> value,boolean isConstant) {
        this.value = value;
        setIsConstant(isConstant);
    }
    public InterpreterDataType getIndex(int index)
    {
        return value.get(index);
    }
    public void setMaxIndex(int maxIndex)
    {
        this.maxIndex = maxIndex;
    }
    public String toString() {
        String string = "[";
        for (int i = 0;i<value.size();i++)
        {
            string += value.get(i);
            if(i!=value.size()-1) string+=",";
        }
        
        string = string + "]";
        return string;
    }
    

    public void fromString(String value) throws SyntaxErrorException {
        char[] charList = value.toCharArray();
        if (!((Character) charList[0]).equals('['))
            throw new SyntaxErrorException("Array values must start with a '['.");
        String accumulated = "";
        ArrayList<String> valueList = new ArrayList<>();
        for (int i = 1; i < value.length(); i++) {
            if (((Character) charList[i]).equals(',')) {
                valueList.add(accumulated);
                accumulated = "";
            } else if (((Character) charList[i]).equals(']'))
            {
                valueList.add(accumulated);
                break;
            }else {
                accumulated = accumulated + charList[i];
            }
        } 
        if(valueList.size() > maxIndex) throw new SyntaxErrorException("Array has max index of "+maxIndex+" and cannot fit requested "+valueList.size()+" variables.");
        createIDTS(valueList);
    }
    public ArrayList<InterpreterDataType> getValue()
    {
        return value;
    }

    private void createIDTS(ArrayList<String> valueList) throws SyntaxErrorException {
        // takes first value and finds its data type then applies it to every value
        ArrayList<InterpreterDataType> value = new ArrayList<>();
        if(areAllValuesBoolean(valueList))
        for(String s : valueList) value.add(new BooleanDataType(Boolean.parseBoolean(s),getIsConstant()));
        else if(areAllValuesStringLiteral(valueList))
        for(String s : valueList) value.add(new StringDataType(s.replace("\"",""),getIsConstant()));
        else if(areAllValuesCharLiteral(valueList))
        for(String s : valueList) value.add(new CharacterDataType(s.toCharArray()[1],getIsConstant()));
        else if(areAllValuesInteger(valueList))
        for(String s : valueList) value.add(new IntegerDataType(Integer.parseInt(s),getIsConstant()));
        else if(areAllValuesReal(valueList))
        for(String s : valueList) value.add(new RealDataType(Double.parseDouble(s),getIsConstant()));
        else throw new SyntaxErrorException("All values stored in an array must be of one data type: "+valueList+".");
        
        this.value = value;
    }
    /* These functions are used in the fromString method in this class
     *this may be temporary if i find a better way.
     */
    public static boolean areAllValuesBoolean(ArrayList<String> valueList)
    {
        boolean isValid = true;
        for(String s : valueList)
        {
            //allows for variable reference nodes and straight booleans
            if(!(s.equalsIgnoreCase("TRUE") || s.equalsIgnoreCase("false") || Character.isLetter(s.toCharArray()[0])))
            isValid = false;
        }
        return isValid;
    }
    public static boolean areAllValuesStringLiteral(ArrayList<String> valueList)
    {
        boolean isValid = true;
        for(String s : valueList)
        {
            //allows for variable reference nodes and straight booleans
            if(!(((Character)s.toCharArray()[0]).equals('"')))
            isValid = false;
        }
        return isValid;
    }
    public static boolean areAllValuesCharLiteral(ArrayList<String> valueList)
    {
        boolean isValid = true;
        for(String s : valueList)
        {
            //allows for variable reference nodes and straight booleans
            if(!(((Character)s.toCharArray()[0]).equals('\'')))
            isValid = false;
        }
        return isValid;
    }
    public static boolean areAllValuesInteger(ArrayList<String> valueList)
    {
        boolean isValid = true;
        for(String s : valueList)
        {
            //allows for variable reference nodes and straight booleans
            if((!Character.isDigit(s.toCharArray()[0]) || s.lastIndexOf(".") != -1 || Character.isLetter(s.toCharArray()[0])))
            isValid = false;
        }
        return isValid;
    }
    public static boolean areAllValuesReal(ArrayList<String> valueList)
    {
        boolean isValid = true;
        for(String s : valueList)
        {
            //allows for variable reference nodes and straight booleans
            if(!(Character.isDigit(s.toCharArray()[0]) || s.lastIndexOf(".") != -1 || Character.isLetter(s.toCharArray()[0])))
            isValid = false;
        }
        return isValid;
    }
}