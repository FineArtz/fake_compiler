// 2019-04-24

package IR;

public class CONST extends Reg {
    private int val;

    public CONST(int v) {
        val = v;
    }

    public int getVal() {
        return val;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public CONST copy() {
        return new CONST(val);
    }
}
