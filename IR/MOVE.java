// 2019-04-25

package IR;

public class MOVE extends Inst {
    private CommonReg dest;
    private Reg src;

    public MOVE(BasicBlock b, CommonReg d, Reg s) {
        super(b);
        dest = d;
        src = s;
        reloadRegs();
    }

    public CommonReg getDest() {
        return dest;
    }

    public Reg getSrc() {
        return src;
    }

    public boolean isSame(MOVE m) {
        return (dest == m.dest && src == m.src);
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        if (src instanceof CommonReg)
            usedReg.add((CommonReg)src);
        usedVal.add(src);
    }

    @Override
    public CommonReg getDefinedReg() {
        return dest;
    }

    @Override
    public void setDefinedReg(CommonReg r) {
        dest = r;
    }
}
