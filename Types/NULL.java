// 2019-03-24

package Types;

public class NULL extends Type {
    public NULL() {}

    @Override
    public boolean coerceTo(Type t){
        return (t.actual() instanceof NULL
        || t.actual() instanceof CLASS
        || t.actual() instanceof ARRAY);
    }
}
