package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 * Created by Javier on 4/20/2018.
 */
public class ArrayTypeDenoterStatic extends TypeDenoter {

    public ArrayTypeDenoterStatic(IntegerLiteral iAST, IntegerLiteral iAST2,TypeDenoter tAST ,SourcePosition thePosition) {
        super (thePosition);
        IL = iAST;
        IL2 = iAST2;
        T = tAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitArrayStatic(this, o);
    }

    public boolean equals(Object obj){ // TODO Agregado, siguiendo la vara de ArrayTypeDenoter
        if (obj != null && obj instanceof ErrorTypeDenoter)
            return true;
        else if (obj != null && obj instanceof ArrayTypeDenoterStatic)
            return this.IL.spelling.compareTo(((ArrayTypeDenoterStatic) obj).IL.spelling) == 0 &&
                    this.IL2.spelling.compareTo(((ArrayTypeDenoterStatic) obj).IL2.spelling) == 0 &&
                    this.T.equals(((ArrayTypeDenoterStatic) obj).T);
        else
            return false;
    }

    public IntegerLiteral IL;
    public IntegerLiteral IL2;
    public TypeDenoter T;

}
