// 2019-04-25

package IR;

import java.util.Map;

public class LOAD extends Inst {
    private CommonReg dest;
    private int size;
    private Reg addr;
    private int offset;
    private boolean isStatic;
    private boolean isAddr;

    public LOAD(BasicBlock b, CommonReg d, int s, Reg a, int o) {
        super(b);
        dest = d;
        size = s;
        addr = a;
        offset = o;
        isStatic = false;
        reloadRegs();
    }

    public LOAD(BasicBlock b, CommonReg d, int s, StaticData a, boolean i) {
        this(b, d, s, a, 0);
        isStatic = true;
        isAddr = i;
    }

    public CommonReg getDest() {
        return dest;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public Reg getAddr() {
        return addr;
    }

    public void setAddr(Reg addr) {
        this.addr = addr;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isSame(LOAD l) {
        return (dest == l.dest && size == l.size && addr == l.addr && offset == l.offset);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isAddr() {
        return isAddr;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        if (addr instanceof CommonReg && !(addr instanceof StackSlot)) {
            usedReg.add((CommonReg) addr);
        }
        usedVal.add(addr);
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
        if (addr instanceof CommonReg && !(addr instanceof StackSlot)) {
            addr = map.get(addr);
        }
        reloadRegs();
    }
}
