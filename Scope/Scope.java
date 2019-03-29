// 2019-03-24

package Scope;

import Err.SomeError;
import Symbol.Symbol;
import Symbol.ClassSymbol;
import Types.CLASS;
import Types.Type;

import java.util.List;
import java.util.Map;

abstract public class Scope{
    public List<LocalScope> children;
    public Map<String, Symbol> symbol;

    public void insert(String k, Symbol v){
        if (symbol.containsKey(k))
            throw new SomeError(String.format("Symbol %s has been defined", v.name));
        else
            symbol.put(k, v);
    }

    public boolean find(String k){
        if (symbol.containsKey(k))
            return true;
        else if (this instanceof TopScope)
            return false;
        else
            return ((LocalScope)this).parent.find(k);
    }

    public Symbol get(String k){
        if (!find(k))
            return null;
        else
            return symbol.get(k);
    }

    public void afind(String k){
        if (!symbol.containsKey(k))
            throw new SomeError(String.format("Symbol %s not found", k));
    }

    public void afind(String k, Type t){
        if (!symbol.containsKey(k))
            throw new SomeError(String.format("Symbol %s not found", k));
        Symbol s = symbol.get(k);
        if (t instanceof CLASS && !(s instanceof ClassSymbol))
            throw new SomeError(String.format("Symbol %s not found", k));
    }

    public void addChild(LocalScope ls){
        children.add(ls);
    }
}
