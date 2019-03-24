// 2019-03-24

package Absyn;

public class NullExpr extends Expr {
    public NullExpr(Position p){
        pos = p;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
