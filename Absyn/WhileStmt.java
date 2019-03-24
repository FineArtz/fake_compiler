// 2019-03-24

package Absyn;

public class WhileStmt extends LoopStmt {
    public Expr test;
    public Stmt body;

    public WhileStmt(Position p, Expr t, Stmt b){
        pos = p;
        test = t;
        body = b;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
