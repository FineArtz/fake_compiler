// 2019-03-24

package Absyn;

public class BoolExpr extends Expr {
    public boolean value;

    public BoolExpr(Position p, boolean v){
        pos = p;
        value = v;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
