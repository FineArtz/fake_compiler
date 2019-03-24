// 2019-03-24

package Absyn;

import Types.ARRAY;

import java.util.List;

public class NewExpr extends Expr {
    public Ty type;
    public int dim; // 0 for nonArrayObject
    public List<Expr> dimExpr;

    public NewExpr(Position p, Ty t){
        assert (!(t.type instanceof ARRAY));
        pos = p;
        type = t;
    }

    public NewExpr(Position p, Ty t, int d, List<Expr> e){
        pos = p;
        type = t;
        dim = d;
        dimExpr = e;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
