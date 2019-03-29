// 2019-03-24

package Scope;

public class LocalScope extends Scope{
    public Scope parent;

    public LocalScope(Scope p){
        assert (p != null);
        parent = p;
        p.addChild(this);
    }
}
