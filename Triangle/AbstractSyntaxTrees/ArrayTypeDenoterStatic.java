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

    public boolean equals(Object obj){ //Se añade de en el proyecto 2 para comparacion de tipo
        if (obj instanceof ErrorTypeDenoter)
            return true;
        else if (obj instanceof ArrayTypeDenoterStatic)
            return this.IL.spelling.compareTo(((ArrayTypeDenoterStatic) obj).IL.spelling) == 0 &&
                    this.IL2.spelling.compareTo(((ArrayTypeDenoterStatic) obj).IL2.spelling) == 0 &&
                    this.T.equals(((ArrayTypeDenoterStatic) obj).T);
        else if(obj instanceof ArrayTypeDenoter) { //Proyecto 3, comparar con un array original de Triangle
            int campos = Integer.parseInt(this.IL2.spelling) - Integer.parseInt(this.IL.spelling) + 1;
            return campos == Integer.parseInt(((ArrayTypeDenoter) obj).IL.spelling) && this.T.equals((((ArrayTypeDenoter) obj).T));
        }
        else
            return false;
    }

    public IntegerLiteral IL;
    public IntegerLiteral IL2;
    public TypeDenoter T;

}
