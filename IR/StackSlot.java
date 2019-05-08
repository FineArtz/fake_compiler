// 2019-04-25

package IR;

public class StackSlot extends CommonReg {
    private Function func;
    private String name;

    public StackSlot(Function f, String n) {
        func = f;
        name = n;
        f.slots.add(this);
    }

    public Function getFunc() {
        return func;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public Reg copy() {
        assert false;
        return null;
    }
}
