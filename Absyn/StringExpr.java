// 2019-03-24

package Absyn;

public class StringExpr extends Expr {
    public String value;

    public StringExpr(Position p, String v){
        pos = p;
        value = v;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
