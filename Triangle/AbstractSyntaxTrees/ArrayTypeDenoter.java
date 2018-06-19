/*
 * @(#)ArrayTypeDenoter.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ArrayTypeDenoter extends TypeDenoter {

    public ArrayTypeDenoter(IntegerLiteral ilAST, TypeDenoter tAST,
                            SourcePosition thePosition) {
        super(thePosition);
        IL = ilAST;
        T = tAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitArrayTypeDenoter(this, o);
    }

    public boolean equals(Object obj) {
        //Proyecto 3 modificado para soportar comparación contra ArrayTypeDenoterStatic
        if (obj instanceof ErrorTypeDenoter)
            return true;
        else if (obj instanceof ArrayTypeDenoter)
            return this.IL.spelling.compareTo(((ArrayTypeDenoter) obj).IL.spelling) == 0 &&
                    this.T.equals(((ArrayTypeDenoter) obj).T);
        else if (obj instanceof ArrayTypeDenoterStatic) {
            ArrayTypeDenoterStatic arrayTypeDenoterStatic = (ArrayTypeDenoterStatic) obj;
            int campos = Integer.parseInt(arrayTypeDenoterStatic.IL2.spelling) - Integer.parseInt(arrayTypeDenoterStatic.IL.spelling) + 1;
            return campos == Integer.parseInt(this.IL.spelling) && this.T.equals(arrayTypeDenoterStatic.T);
        } else
            return false;
    }

    public IntegerLiteral IL;
    public TypeDenoter T;
}
