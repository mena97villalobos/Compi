package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class DoWhileCommand extends Command {

    public DoWhileCommand(Command cAST, Expression eAST, SourcePosition thePosition) {
        super(thePosition);
        E = eAST;
        C = cAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitDoWhileCommand(this, o);
    }

    public Expression E;
    public Command C;
}
