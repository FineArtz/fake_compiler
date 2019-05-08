// 2019-04-28

package FE;

import Absyn.Expr;

public class GlobalInit {
    private String name;
    private Expr expr;

    public GlobalInit(String n, Expr e) {
        name = n;
        expr = e;
    }

    public String getName() {
        return name;
    }

    public Expr getExpr() {
        return expr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }
}
