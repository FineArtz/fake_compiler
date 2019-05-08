// 2019-04-25

package IR;

public abstract class StaticData extends CommonReg {
    private String name;

    public StaticData(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    @Override
    public Reg copy() {
        return this;
    }
}
