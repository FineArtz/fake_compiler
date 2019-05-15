// 2019-04-24

package IR;

import java.util.Map;

public class STORE extends Inst {
    private Reg value;
    private int size;
    private Reg addr;
    private int offset;
    private boolean isStatic;

    public STORE(BasicBlock b, Reg v, int s, Reg a, int o) {
        super(b);
        value = v;
        size = s;
        addr = a;
        offset = o;
        isStatic = false;
        reloadRegs();
    }

    public STORE(BasicBlock b, Reg v, int s, StaticData a) {
        this(b, v, s, a, 0);
        isStatic = true;
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

    public boolean isStatic() {
        return isStatic;
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

    @Override
    public void renameUsedReg(Map<CommonReg, CommonReg> map) {
        if (value instanceof CommonReg) {
            value = map.get(value);
        }
        if (addr instanceof CommonReg && !(addr instanceof StackSlot)) {
            addr = map.get(addr);
        }
        reloadRegs();
    }

    @Override
    public STORE copy(Map<Object, Object> map) {
        return (isStatic ? new STORE((BasicBlock)map.getOrDefault(bb, bb), (Reg)map.getOrDefault(value, value), size, (StaticData)map.getOrDefault(addr, addr)) : new STORE((BasicBlock)map.getOrDefault(bb, bb), (Reg)map.getOrDefault(value, value), size, (Reg)map.getOrDefault(addr, addr), offset));
    }
}
