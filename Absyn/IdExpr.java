// 2019-03-24

package Absyn;

public class IdExpr extends Expr {
    public String id;

    public IdExpr(Position p, String i){
        pos = p;
        id = i;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
