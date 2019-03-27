// 2019-03-27

package Absyn;

import java.util.List;

public class VarDefList extends Absyn {
    public List<VarDef> varList;

    public VarDefList(Position p, List<VarDef> v) {
        pos = p;
        varList = v;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
