// 2019-04-23

package IR;

public class VirtualReg extends CommonReg {
    public String name;
    public PhysicalReg preg = null;
    public StackSlot slot = null;
    public int stackPos = 0;
    public boolean isGlobal = false;

    public VirtualReg(String n) {
        name = n;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public VirtualReg copy() {
        return new VirtualReg(name);
    }
}
