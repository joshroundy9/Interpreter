package Main;

public class Token {
    public enum tokenType {
        //words
        IDENTIFIER, NUMBER, ENDOFLINE, WHILE, FOR, CONSTANTS,VARIABLES,DEFINE,VAR,IF,ELSEIF,ELSE,BLOCK,TO,FROM,THEN,REPEAT,UNTIL,NOT,AND,OR,
        //types
        INTEGER,REAL,BOOLEAN,CHARACTER,STRING,MOD, ARRAY,OF,
        STRINGLITERAL,CHARLITERAL, TRUE, FALSE,
        //operators
        DIVIDE,ADD,SUBTRACT,MULT,COMMA,SEMICOLON,COMPARISON,
        GREATER,LESS,COLON,
        GREATERTHANEQUAL,LESSERTHANEQUAL,ASSIGNMENT,
        LPAREN, RPAREN,LBRACKET,RBRACKET,
        //indentation
        INDENT,DEDENT,
    } 

    private tokenType type;

    private String value;

    public Token(String value, tokenType type)
    {
        this.value = value;
        this.type = type;
    }


    
    /** Overrides the toString method and returns the value.
     * @return String 
     */
    public String toString()
    {
        return value;
    }
    
    /** Returns the value of the token.
     * @return String 
     */
    public String getValue()
    {
        return value;
    }
    
    /** Returns the tokens type.
     * @return tokenType 
     */
    public tokenType getType()
    {
        return type;
    }
    /** Sets the tokens type.
     */
    public void setType(tokenType type)
    {
        this.type=type;
    }
    public void print()
    {
        if(value != null)
        {
            System.out.println(type.toString()+"("+getValue()+")");
        } else {
            System.out.println(type.toString());
        }
    }
}
