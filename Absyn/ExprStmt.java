// 2019-03-29

package Absyn;

public class ExprStmt extends Stmt{
    public Expr expr;

    public ExprStmt(Position p, Expr e){
        pos = p;
        expr = e;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
