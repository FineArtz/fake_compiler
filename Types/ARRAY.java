// 2019-03-24

package Types;

public class ARRAY extends Type {
    public Type element;

    public ARRAY(Type e){
        element = e;
    }

    @Override
    public boolean coerceTo(Type t){
        return (t.actual() == this);
    }
}
