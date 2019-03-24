// 2019-03-24

package Absyn;

public class ThisExpr extends Expr {
    public ThisExpr(Position p){
        pos = p;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
