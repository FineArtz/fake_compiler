// 2019-03-24

package Absyn;

import Types.Type;

public class Ty extends Absyn{
    public Type type;

    public Ty(Position p, Type t){
        pos = p;
        type = t;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
