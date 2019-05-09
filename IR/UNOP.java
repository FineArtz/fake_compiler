// 2019-04-23

package IR;

import java.util.Map;

public class UNOP extends Inst {
    public enum OP {
        NEG,    // -
        BNOT    // ~
    }

    private OP op;
    private Reg operand;
    private CommonReg dest;

    public UNOP(BasicBlock b, OP o, Reg opr, CommonReg d) {
        super(b);
        op = o;
        operand = opr;
        dest = d;
        reloadRegs();
    }

    public void setOprand(Reg opr) {
        operand = opr;
        reloadRegs();
    }

    public OP getOp() {
        return op;
    }

    public Reg getOperand() {
        return operand;
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
        if (operand instanceof CommonReg)
            usedReg.add((CommonReg)operand);
        usedVal.add(operand);
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
        if (operand instanceof CommonReg) {
            operand = map.get(operand);
        }
        reloadRegs();
    }
}
