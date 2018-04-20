package Triangle.AbstractSyntaxTrees;

import Triangle.AbstractSyntaxTrees.Command;
import Triangle.AbstractSyntaxTrees.Expression;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.SyntacticAnalyzer.SourcePosition;

public class DoUntilCommand extends Command {

    public DoUntilCommand (Command cAST, Expression eAST, SourcePosition thePosition) {
        super (thePosition);
        E = eAST;
        C = cAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitDoUntilCommand(this, o);
    }

    public Expression E;
    public Command C;
}
