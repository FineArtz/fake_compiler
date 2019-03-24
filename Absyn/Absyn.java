// 2019-03-22

package Absyn;

abstract public class Absyn{
    public Position pos;

    abstract public void accept(ASTVisitor v);
}
