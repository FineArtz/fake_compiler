// 2019-03-24

package Absyn;

import java.util.List;

public class ClassDef extends Definitions{
    public String name;
    public List<VarDef> varMem;
    public List<FunctionDef> funMem;

    public ClassDef(Position p, String n, List<VarDef> v, List<FunctionDef> f){
        pos = p;
        name = n;
        varMem = v;
        funMem = f;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
