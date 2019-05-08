// 2019-03-24

package Absyn;

public class StringExpr extends Expr {
    public String val;

    public StringExpr(Position p, String v){
        pos = p;
        val = v;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
