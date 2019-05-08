// 2019-04-24

package IR;

public class CMP extends Inst {
    public enum OP {
        LES,    // <
        GRT,    // >
        LTE,    // <=
        GTE,    // >=
        EQL,    // ==
        NEQ     // !=
    }

    private OP op;
    private Reg lhs, rhs;
    private CommonReg dest;

    public CMP(BasicBlock b, OP o, Reg l, Reg r, VirtualReg d) {
        super(b);
        op = o;
        lhs = l;
        rhs = r;
        dest = d;
        reloadRegs();
    }

    public void setLhs(Reg l) {
        lhs = l;
        reloadRegs();
    }

    public void setRhs(Reg r) {
        rhs = r;
        reloadRegs();
    }

    public OP getOp() {
        return op;
    }

    public Reg getLhs() {
        return lhs;
    }

    public Reg getRhs() {
        return rhs;
    }

    public CommonReg getDest() {
        return dest;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        if (lhs instanceof CommonReg)
            usedReg.add((CommonReg)lhs);
        if (rhs instanceof CommonReg)
            usedReg.add((CommonReg)rhs);
        usedVal.add(lhs);
        usedVal.add(rhs);
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
