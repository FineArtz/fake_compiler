// 2019-04-24

package IR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CALL extends Inst {
    private Function func;
    private List<Reg> args;
    private CommonReg dest;

    public CALL(BasicBlock b, Function f, List<Reg> a, VirtualReg d) {
        super(b);
        func = f;
        args = a;
        dest = d;
        reloadRegs();
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

    public void setFunc(Function func) {
        this.func = func;
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

    @Override
    public void renameUsedReg(Map<CommonReg, CommonReg> map) {
        for (int i = 0; i < args.size(); ++i) {
            if (args.get(i) instanceof CommonReg) {
                args.set(i, map.get(args.get(i)));
            }
        }
        reloadRegs();
    }

    @Override
    public CALL copy(Map<Object, Object> map) {
        List<Reg> cargs = new ArrayList<>();
        args.forEach(x -> cargs.add((Reg)map.getOrDefault(x, x)));
        return new CALL((BasicBlock)map.getOrDefault(bb, bb), func, cargs, (VirtualReg)map.getOrDefault(dest, dest));
    }
}
