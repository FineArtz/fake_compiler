// 2019-03-24

package Absyn;

public class ContinueStmt extends JumpStmt {
    public ContinueStmt(Position p){
        pos = p;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
