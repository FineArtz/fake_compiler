// 2019-03-24

package Absyn;

import Symbol.FuncSymbol;

import java.util.List;

public class FunCallExpr extends Expr {
    public Expr func;
    public List<Expr> args;
    public FuncSymbol funcSymbol;

    public FunCallExpr(Position p, Expr f, List<Expr> a){
        pos = p;
        func = f;
        args = a;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
