// 2019-03-24

package Absyn;

public class ForStmt extends LoopStmt {
    public Expr init;
    public Expr cond;
    public Expr step;
    public Stmt body;

    public ForStmt(Position p, Expr x, Expr y, Expr z, Stmt b){
        pos = p;
        init = x;
        cond = y;
        step = z;
        body = b;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
