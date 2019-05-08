// 2019-04-24

package IR;

public class STORE extends Inst {
    private Reg value;
    private int size;
    private Reg addr;
    private int offset;

    public STORE(BasicBlock b, Reg v, int s, Reg a, int o) {
        super(b);
        value = v;
        size = s;
        addr = a;
        offset = o;
        reloadRegs();
    }

    public Reg getAddr() {
        return addr;
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    public Reg getValue() {
        return value;
    }

    public void setAddr(Reg addr) {
        this.addr = addr;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isSame(STORE s) {
        return (value == s.value && size == s.size && addr == s.addr && offset == s.offset);
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public void reloadRegs() {
        usedReg.clear();
        usedVal.clear();
        if (value instanceof CommonReg)
            usedReg.add((CommonReg) value);
        if (addr instanceof CommonReg && !(addr instanceof StackSlot))
            usedReg.add((CommonReg) addr);
        usedVal.add(value);
        usedVal.add(addr);
    }

    @Override
    public CommonReg getDefinedReg() {
        return null;
    }

    @Override
    public void setDefinedReg(CommonReg r) {
        assert false;
    }
}
