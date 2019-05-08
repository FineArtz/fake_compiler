// 2019-03-24

package Absyn;

import Symbol.VarSymbol;

public class IdExpr extends Expr {
    public String id;
    public VarSymbol var;
    public boolean needMem;

    public IdExpr(Position p, String i){
        pos = p;
        id = i;
        var = null;
        needMem = false;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
