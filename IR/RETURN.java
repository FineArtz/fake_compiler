// 2019-04-24

package IR;

import java.util.Map;

public class RETURN extends JumpInst {
    private Reg retVal;

    public RETURN(BasicBlock b, Reg r) {
        super(b);
        retVal = r;
        reloadRegs();
    }

    public Reg getRetVal() {
        return retVal;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        if (retVal != null) {
            if (retVal instanceof CommonReg)
                usedReg.add((CommonReg) retVal);
            usedVal.add(retVal);
        }
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
    public void renameUsedReg(Map<CommonReg, CommonReg> map) {
        if (retVal != null) {
            if (retVal instanceof CommonReg) {
                retVal = map.get(retVal);
            }
        }
        reloadRegs();
    }

    @Override
    public RETURN copy(Map<Object, Object> map) {
        return new RETURN((BasicBlock)map.getOrDefault(bb, bb), (Reg)map.getOrDefault(retVal, retVal));
    }
}
