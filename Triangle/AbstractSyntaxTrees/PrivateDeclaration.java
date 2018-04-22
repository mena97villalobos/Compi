package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class PrivateDeclaration extends Declaration {
    public PrivateDeclaration(Declaration D1, Declaration D2, SourcePosition thePosition) {
        super(thePosition);
        this.D1 = D1;
        this.D2 = D2;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitPrivateDeclaration(this, o);
    }

    public Declaration D1, D2;
}
