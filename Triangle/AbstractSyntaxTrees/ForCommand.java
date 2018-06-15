package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForCommand extends Command {

    public ForCommand (Identifier iAST, Expression e1AST, Expression e2AST, Command cAST, SourcePosition thePosition) {
        super (thePosition);
        I = iAST;
        E1 = e1AST;
        E2 = e2AST;
        C = cAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitForCommand(this, o);
    }

    public int revisarCommand(Command c){
        if(c instanceof SequentialCommand){
            int a = revisarCommand(((SequentialCommand) c).C1);
            int b = revisarCommand(((SequentialCommand) c).C2);
            if(a != 1)
                return a;
            else if(b != 1)
                return b;
            else
                return 1;
        }
        else if(c instanceof AssignCommand){
            try{
                SimpleVname sv = (SimpleVname) ((AssignCommand) c).V;
                if(sv.I.spelling.equals(this.I.spelling)){
                    return -1;
                }
                else{
                    try{
                        CallExpression ce = (CallExpression) ((AssignCommand) c).E;
                        return revisarArgumentos(ce.APS);
                    }
                    catch (Exception e1){
                        return 1;
                    }
                }
            }
            catch (Exception e){
                return 1;
            }
        }
        else if (c instanceof CallCommand){
            int retorno = revisarArgumentos(((CallCommand) c).APS);
            if(retorno == -2){
                return -2;
            }
        }
        return 1;
    }

    private int revisarArgumentos(ActualParameterSequence ast){
        if(ast instanceof SingleActualParameterSequence){
            try{
                VarActualParameter vap =(VarActualParameter) ((SingleActualParameterSequence) ast).AP;
                SimpleVname sv = (SimpleVname) vap.V;
                if(sv.I.spelling.equals(this.I.spelling)){
                    return -2;
                }
            }
            catch (Exception ignored){
                /*
                try{
                    ConstActualParameter cap = (ConstActualParameter) ((SingleActualParameterSequence)ast).AP;
                    SimpleVname ce = (SimpleVname) ((VnameExpression)cap.E).V;
                    if(ce.I.spelling.equals(this.I.spelling))
                        return -2;
                }
                catch (Exception e1){
                    return 1;
                }
                */
            }
        }
        else if(ast instanceof MultipleActualParameterSequence){
            try {
                VarActualParameter vap = (VarActualParameter) ((MultipleActualParameterSequence) ast).AP;
                SimpleVname sv = (SimpleVname) vap.V;
                if (sv.I.spelling.equals(this.I.spelling)) {
                    return -2;
                }
                int retorno = revisarArgumentos(((MultipleActualParameterSequence) ast).APS);
                if(retorno == -2)
                    return -2;

            }catch (Exception e){
                int ret =  revisarArgumentos(((MultipleActualParameterSequence) ast).APS);
                if(ret == -2)
                    return -2;
            }
        }
        return 1;
    }

    public Identifier I;
    public Expression E1;
    public Expression E2;
    public Command C;
}
