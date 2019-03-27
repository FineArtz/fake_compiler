// 2019-03-24

package Absyn;

import java.util.List;
import Scope.Scope;

public class Program extends Absyn {
    public List<Definitions> defs;
    public Scope scope;

    public Program(Position p, List<Definitions> d){
        pos = p;
        defs = d;
    }

    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }
}
