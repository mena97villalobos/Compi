package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 * Created by Javier on 4/22/2018.
 */
public class RecDeclaration extends Declaration {
    public RecDeclaration(Declaration procFuncs,SourcePosition thePosition) {
        super(thePosition);
        D=procFuncs;
    }

    @Override
    public Object visit(Visitor v, Object o) {
        return v.visitRecDeclaration(this,o);
    }

   public Declaration D;
}
