package Main;
public class SyntaxErrorException extends Exception{ 
    public SyntaxErrorException(String error){super(error);}
    public SyntaxErrorException(){super();}
}
