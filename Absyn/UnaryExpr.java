// 2019-03-24

package Absyn;

public class UnaryExpr extends Expr {
    public int op;
    // 0 for Inc, 1 for Dec, 2 for Pos, 3 for Neg, 4 for LNot, 5 for BNot
    public Expr expr;

    public UnaryExpr(Position p, int o, Expr e){
        assert (0 <= o && o <= 5);
        pos = p;
        op = o;
        expr = e;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
