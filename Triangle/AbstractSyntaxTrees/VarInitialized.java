package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 * Created by Javier on 4/20/2018.
 */
public class VarInitialized extends Declaration {

    public VarInitialized(Identifier iAST, Expression eAST, SourcePosition thePosition) {
        super (thePosition);
        I = iAST;
        E = eAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitVarInitialized(this, o);
    }

    public Identifier I;
    public Expression E;
    public TypeDenoter T; //TODO Agregado para revisar proyecto 2

}
