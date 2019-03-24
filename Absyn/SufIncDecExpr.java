// 2019-03-24

package Absyn;

public class SufIncDecExpr extends Expr {
    public Expr expr;
    public int op; // 0 for Inc, 1 for Dec

    public SufIncDecExpr(Position p, Expr e, int o){
        assert (o == 0 || o == 1);
        pos = p;
        expr = e;
        op = o;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
