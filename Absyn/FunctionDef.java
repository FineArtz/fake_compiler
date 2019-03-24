// 2019-03-24

package Absyn;

import java.util.List;

public class FunctionDef extends Definitions{
    public String name;
    public List<VarDef> params;
    public Ty result;
    public BlockStmt body;
    public boolean isConstructor;

    public FunctionDef(Position p, String n, List<VarDef> a, Ty r, BlockStmt b){
        pos = p;
        name = n;
        params = a;
        result = r;
        body = b;
        isConstructor = (r == null);
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
