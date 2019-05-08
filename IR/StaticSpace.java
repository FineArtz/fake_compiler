// 2019-04-25

package IR;

public class StaticSpace extends StaticData {
    private int size;

    public StaticSpace(String n, int s) {
        super(n);
        size = s;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }
}
