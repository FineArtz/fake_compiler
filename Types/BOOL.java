// 2019-03-24

package Types;

public class BOOL extends Type {
    public BOOL() {}

    @Override
    public boolean coerceTo(Type t){
        return (t.actual() instanceof BOOL);
    }

    @Override
    public int getSize() {
        return BOOL_SIZE;
    }
}
