// 2019-05-07

package IR;

import java.util.Map;

public class POP extends Inst {
    private PhysicalReg preg;

    public POP(BasicBlock b, PhysicalReg p) {
        super(b);
        preg = p;
    }

    public PhysicalReg getPreg() {
        return preg;
    }

    public void setPreg(PhysicalReg preg) {
        this.preg = preg;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        // do nothing
    }

    @Override
    public CommonReg getDefinedReg() {
        return null;
    }

    @Override
    public void setDefinedReg(CommonReg r) {
        assert false;
    }

    @Override
    public void renameUsedReg(Map<CommonReg, CommonReg> map) {}

    @Override
    public POP copy(Map<Object, Object> map) {
        return null;
    }
}
