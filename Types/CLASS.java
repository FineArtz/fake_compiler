// 2019-03-24

package Types;

public class CLASS extends Type {
    public String name;

    public CLASS(String n){
        name = n;
    }

    @Override
    public boolean coerceTo(Type t){
        return (t.actual() == this);
    }
}
