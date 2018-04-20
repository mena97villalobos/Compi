package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class Proc_Funcs extends Command {

    public Proc_Funcs (Identifier iAST, FormalParameterSequence fpsAST,
                            Command cAST, SourcePosition thePosition) {
        super (thePosition);
        I = iAST;
        FPS = fpsAST;
        C = cAST;
    }

    public Object visit (Visitor v, Object o) {
        return v.visitProcDeclaration(this, o);
    }

    public Declaration D1; //Proc Func 1
}
