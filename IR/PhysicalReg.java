// 2019-04-23

package IR;

public abstract class PhysicalReg extends CommonReg {
    public abstract String getName();
    public abstract String getLowbitName();
    public abstract boolean isGeneral();
    public abstract boolean isCallerSave();
    public abstract boolean isCalleeSave();

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }

    @Override
    public PhysicalReg copy() {
        return null;
    }
}
