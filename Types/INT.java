// 2019-03-24

package Types;

public class INT extends Type {
    public INT() {}

    @Override
    public boolean coerceTo(Type t){
        return (t.actual() instanceof INT);
    }

    @Override
    public int getSize() {
        return INT_SIZE;
    }
}
