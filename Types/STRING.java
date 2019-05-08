// 2019-03-24

package Types;

public class STRING extends Type {
    public STRING() {}

    @Override
    public boolean coerceTo(Type t){
        return (t.actual() instanceof STRING);
    }

    @Override
    public int getSize() {
        return POINTER_SIZE;
    }
}
