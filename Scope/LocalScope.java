// 2019-03-24

package Scope;

public class LocalScope extends Scope{
    protected Scope parent;

    public LocalScope(Scope p){
        parent = p;
    }
}
