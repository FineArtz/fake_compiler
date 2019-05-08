// 2019-04-25

package IR;

import java.util.HashMap;
import java.util.Map;

public class PhiInst extends Inst {
    private BasicBlock block;
    private VirtualReg dest;
    public Map<BasicBlock, Reg> path = new HashMap<>();

    public PhiInst(BasicBlock b, VirtualReg r) {
        super(b);
        block = b;
        dest = r;
    }

    public void addReg(BasicBlock b, VirtualReg r) {
        path.put(b, r);
        if (r != null)
            usedReg.add(r);
    }

    public Reg getReg(BasicBlock b) {
        return path.get(b);
    }

    public VirtualReg getDest() {
        return dest;
    }

    public BasicBlock getBlock() {
        return block;
    }

    public void setDest(VirtualReg dest) {
        this.dest = dest;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        assert false;
    }

    @Override
    public CommonReg getDefinedReg() {
        return dest;
    }

    @Override
    public void setDefinedReg(CommonReg r) {
        assert false;
    }

    @Override
    public void remove() {
        block.phi.remove(dest);
    }
}
