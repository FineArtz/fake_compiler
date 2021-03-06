// 2019-04-24

package IR;

import java.util.Map;

public class CJUMP extends JumpInst {
    private Reg cond;
    private BasicBlock thenBB, elseBB;

    public CJUMP(BasicBlock b, Reg c, BasicBlock t, BasicBlock e) {
        super(b);
        cond = c;
        thenBB = t;
        elseBB = e;
        reloadRegs();
    }

    public Reg getCond() {
        return cond;
    }

    public BasicBlock getElseBB() {
        return elseBB;
    }

    public BasicBlock getThenBB() {
        return thenBB;
    }

    public void setCond(Reg cond) {
        this.cond = cond;
        reloadRegs();
    }

    public void setElseBB(BasicBlock elseBB) {
        this.elseBB = elseBB;
    }

    public void setThenBB(BasicBlock thenBB) {
        this.thenBB = thenBB;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        if (cond instanceof VirtualReg)
            usedReg.add((VirtualReg)cond);
        usedVal.add(cond);
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
        if (cond instanceof CommonReg) {
            cond = map.get(cond);
        }
        reloadRegs();
    }

    @Override
    public CJUMP copy(Map<Object, Object> map) {
        return new CJUMP((BasicBlock)map.getOrDefault(bb, bb), (Reg)map.getOrDefault(cond, cond), (BasicBlock)map.getOrDefault(thenBB, thenBB), (BasicBlock)map.getOrDefault(elseBB, elseBB));
    }
}
