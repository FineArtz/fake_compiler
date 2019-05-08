// 2019-05-03

package BE;

import IR.PhysicalReg;

public class NASMReg extends PhysicalReg {
    private final int id;
    private final String name;
    private final boolean isGeneral;
    private final boolean isCallerSave;
    private final boolean isCalleeSave;

    public NASMReg(int i, String n, boolean g, boolean r, boolean e) {
        id = i;
        name = n;
        isGeneral = g;
        isCallerSave = r;
        isCalleeSave = e;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isGeneral() {
        return isGeneral;
    }

    @Override
    public boolean isCallerSave() {
        return isCallerSave;
    }

    @Override
    public boolean isCalleeSave() {
        return isCalleeSave;
    }
}
