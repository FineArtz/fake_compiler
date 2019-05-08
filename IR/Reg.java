// 2019-04-23

package IR;

public abstract class Reg {
    public abstract void accept(IRVisitor v);
    public abstract Reg copy();
}
