// 2019-03-24

package Absyn;

public class MemAccessExpr extends Expr {
    public Expr expr;
    public String mem;

    public MemAccessExpr(Position p, Expr e, String m){
        pos = p;
        expr = e;
        mem = m;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
