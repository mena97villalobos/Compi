package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ElsifCommand extends Command {

    public ElsifCommand(Expression eAST, Command c1AST, Command c2AST,
                        SourcePosition thePosition) {
        super (thePosition);
        E = eAST;
        C1 = c1AST; //Comand del elsif
        C2 = c2AST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitElsifCommand(this, o); //TODO visitor del Elif Command
    }

    public Expression E;
    public Command C1, C2;
}
