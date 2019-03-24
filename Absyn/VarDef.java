// 2019-03-24

package Absyn;

public class VarDef extends Definitions {
    public String name;
    public boolean escape = true;
    public Ty type;
    public Expr init;

    public VarDef(Position p, String n, Ty t, Expr i){
        pos = p;
        name = n;
        type = t;
        init = i;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
