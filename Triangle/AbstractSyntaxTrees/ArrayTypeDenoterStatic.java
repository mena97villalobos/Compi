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

    public boolean equals(Object o){
        //TODO Se deja impleentacion para futuros proyectos
        return true;
    }

    public IntegerLiteral IL;
    public IntegerLiteral IL2;
    public TypeDenoter T;

}
