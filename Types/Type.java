// 2019-03-24

package Types;

public abstract class Type {
    static public int INT_SIZE = 8;
    static public int BOOL_SIZE = 8;
    static public int POINTER_SIZE = 8;
    static public int VOID_SIZE = 0;

    public Type actual(){
        return this;
    }

    public boolean coerceTo(Type t){
        return false;
    }

    public abstract int getSize();
}
