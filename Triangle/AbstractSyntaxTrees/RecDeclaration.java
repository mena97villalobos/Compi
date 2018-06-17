package Triangle.AbstractSyntaxTrees;

import Triangle.CodeGenerator.KnownRoutine;
import Triangle.SyntacticAnalyzer.SourcePosition;

import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, KnownRoutine> direccionesDisponibles = new HashMap<>();
    public static Map<String, Integer> direccionesDisponiblesParches = new HashMap<>();
    public static Map<String, int[]> direccionesXparsear = new HashMap<>();
}
