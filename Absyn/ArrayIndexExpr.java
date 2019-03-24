// 2019-03-24

package Absyn;

public class ArrayIndexExpr extends Expr {
    public Expr arr;
    public Expr index;

    public ArrayIndexExpr(Position p, Expr a, Expr i){
        pos = p;
        arr  = a;
        index = i;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
