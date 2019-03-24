// 2019-03-24

package Absyn;

public class IfStmt extends Stmt {
    public Expr test;
    public Stmt thenClause;
    public Stmt elseClause;

    public IfStmt(Position p, Expr x, Stmt y){
        pos = p;
        test = x;
        thenClause = y;
        elseClause = null;
    }

    public IfStmt(Position p, Expr x, Stmt y, Stmt z){
        pos = p;
        test = x;
        thenClause = y;
        elseClause = z;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
