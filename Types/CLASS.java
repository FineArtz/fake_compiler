// 2019-03-24

package Types;

public class CLASS extends Type {
    public String name;

    public CLASS(String n){
        name = n;
    }

    @Override
    public boolean coerceTo(Type t){
        if (t instanceof CLASS)
            return name.equals(((CLASS)t).name);
        else
            return false;
    }

    @Override
    public int getSize() {
        return POINTER_SIZE;
    }
}
