// 2019-04-25

package IR;

import java.util.Map;

public class ALLOC extends Inst {
    private CommonReg dest;
    private Reg size;

    public ALLOC(BasicBlock b, VirtualReg d, Reg s) {
        super(b);
        dest = d;
        size = s;
        reloadRegs();
    }

    public CommonReg getDest() {
        return dest;
    }

    public Reg getSize() {
        return size;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        if (size instanceof CommonReg)
            usedReg.add((CommonReg)size);
        usedVal.add(size);
    }

    @Override
    public CommonReg getDefinedReg() {
        return dest;
    }

    @Override
    public void setDefinedReg(CommonReg r) {
        dest = r;
    }

    @Override
    public void renameUsedReg(Map<CommonReg, CommonReg> map) {
        if (size instanceof CommonReg) {
            size = map.get(size);
        }
        reloadRegs();
    }

    @Override
    public ALLOC copy(Map<Object, Object> map) {
        return new ALLOC((BasicBlock)map.getOrDefault(bb, bb), (VirtualReg)map.getOrDefault(dest, dest), (Reg)map.getOrDefault(size, size));
    }
}
