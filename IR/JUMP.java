// 2019-04-23

package IR;

import java.util.Map;

public class JUMP extends JumpInst {
    private BasicBlock target;

    public JUMP(BasicBlock b, BasicBlock t) {
        super(b);
        target = t;
    }

    public BasicBlock getTarget() {
        return target;
    }

    public void setTarget(BasicBlock target) {
        this.target = target;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {}

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
    public JUMP copy(Map<Object, Object> map) {
        return new JUMP((BasicBlock)map.getOrDefault(bb, bb), (BasicBlock)map.getOrDefault(target, target));
    }
}
