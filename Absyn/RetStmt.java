// 2019-03-24

package Absyn;

public class RetStmt extends JumpStmt {
    public Expr expr;

    public RetStmt(Position p, Expr e){
        pos = p;
        expr = e;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
