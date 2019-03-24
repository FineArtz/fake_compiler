// 2019-03-24

package Scope;

import Types.Type;

abstract public class Entity{
    protected String name;
    protected Type type;

    public Entity(String name, Type type){
        this.name = name;
        this.type = type;
    }
}
