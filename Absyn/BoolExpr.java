// 2019-03-24

package Absyn;

public class BoolExpr extends Expr {
    public boolean val;

    public BoolExpr(Position p, boolean v){
        pos = p;
        val = v;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
