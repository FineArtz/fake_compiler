// 2019-03-24

package Absyn;

public class BreakStmt extends JumpStmt {
    public BreakStmt(Position p) {
        pos = p;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
