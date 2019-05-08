// 2019-03-24

package Types;

public class VOID extends Type {
    public VOID() {}

    @Override
    public boolean coerceTo(Type t){
        return (t.actual() instanceof VOID);
    }

    @Override
    public int getSize() {
        return VOID_SIZE;
    }
}
