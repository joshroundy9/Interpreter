package Main;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Interpreter.Interpreter;



public class Shank {
    public static void main(String args[])
    {
        if(args.length != 1)
        {
            System.out.println("Invalid Args: Input the file name as one argument with no spaces arg count: "+args.length+".");
            System.exit(0);
        }
        Path myPath = Paths.get(args[0]);

        // Catches the IOException in case the file is not found or is invalid
        try {

            List <String> lines = Files.readAllLines(myPath, StandardCharsets.UTF_8);
            
            Lexer lex = new Lexer();
            for(int i = 0;i<lines.size();i++)
            {
                lex.lex(lines.get(i),i+1,lines.size());
            }
            //prints tokens
            /*for(Token token : lex.getTokens())
            {
                token.print();
            }*/
            
            Parser parser = new Parser(lex.getTokens());
            parser.parse();
            SemanticAnalysis analyzer = new SemanticAnalysis();
            analyzer.CheckAssignments(parser.getProgramNode());
            Interpreter interpreter = new Interpreter(parser.getProgramNode());
            interpreter.interpretProgram();
        } catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Invalid file name, or the file could not be found.");
            System.exit(0);
        } catch(SyntaxErrorException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
