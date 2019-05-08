// 2019-04-24

package IR;

import java.util.List;

public class CALL extends Inst {
    private Function func;
    private List<Reg> args;
    private CommonReg dest;

    public CALL(BasicBlock b, Function f, List<Reg> a, VirtualReg d) {
        super(b);
        func = f;
        args = a;
        dest = d;
        if (!f.isBuiltIn) {
            reloadRegs();
        }
    }

    public void addArg(Reg a) {
        args.add(a);
        if (a instanceof CommonReg)
            usedReg.add((CommonReg)a);
    }

    public CommonReg getDest() {
        return dest;
    }

    public List<Reg> getArgs() {
        return args;
    }

    public Function getFunc() {
        return func;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        for (Reg a : args) {
            if (a instanceof CommonReg)
                usedReg.add((CommonReg)a);
            usedVal.add(a);
        }
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
