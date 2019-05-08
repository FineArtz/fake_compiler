// 2019-03-24

package Types;

public class ARRAY extends Type {
    public Type element;

    public ARRAY(Type e){
        element = e;
    }

    @Override
    public boolean coerceTo(Type t){
        if (t instanceof ARRAY)
            return element.coerceTo(((ARRAY)t).element);
        else
            return false;
    }

    @Override
    public int getSize() {
        return POINTER_SIZE;
    }
}
