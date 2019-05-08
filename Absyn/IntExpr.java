// 2019-03-24

package Absyn;

public class IntExpr extends Expr {
    public int val;

    public IntExpr(Position p, int v){
        pos = p;
        val = v;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
