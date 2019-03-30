// 2019-03-24

package Scope;

import Absyn.Position;
import Err.SomeError;
import Symbol.Symbol;
import Symbol.ClassSymbol;
import Symbol.VarSymbol;
import Types.CLASS;
import Types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class Scope{
    public List<LocalScope> children = new ArrayList<>();
    public Map<String, Symbol> symbol = new HashMap<>();

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

    public boolean find(String k, Position p){
        if (symbol.containsKey(k)){
            Symbol s = symbol.get(k);
            if (!(s instanceof VarSymbol))
                return true;
            if (p.less(((VarSymbol)s).pos)){
                if (this instanceof TopScope)
                    return false;
                else
                    return ((LocalScope)this).parent.find(k, p);
            }
            return true;
        }
        else
            return ((LocalScope)this).parent.find(k, p);
    }

    public Symbol get(String k){
        if (!find(k))
            return null;
        else{
            if (symbol.containsKey(k))
                return symbol.get(k);
            else
                return ((LocalScope)this).parent.get(k);
        }
    }

    public Symbol get(String k, Position p){
        if (!find(k, p))
            return null;
        else{
            if (symbol.containsKey(k)){
                Symbol s = symbol.get(k);
                if (!(s instanceof VarSymbol))
                    return symbol.get(k);
                if (p.less(((VarSymbol)s).pos)){
                    if (this instanceof TopScope)
                        return null;
                    else
                        return ((LocalScope)this).parent.get(k, p);
                }
                else
                    return s;
            }
            else
                return ((LocalScope)this).parent.get(k);
        }
    }

    public void afind(String k){
        if (!find(k))
            throw new SomeError(String.format("Symbol %s not found", k));
    }

    public void afind(String k, Type t){
        if (!find(k))
            throw new SomeError(String.format("Symbol %s not found", k));
        Symbol s = symbol.get(k);
        if (t instanceof CLASS && !(s instanceof ClassSymbol))
            throw new SomeError(String.format("Symbol %s not found", k));
    }

    public void afind(String k, Position p){
        if (!find(k))
            throw new SomeError(String.format("Symbol %s not found", k));
        Symbol s = get(k);
        if (s instanceof VarSymbol){
            if (p.less(((VarSymbol)s).pos))
                if (this instanceof TopScope)
                    throw new SomeError(String.format("Symbol %s not found", k));
                else
                    ((LocalScope)this).parent.afind(k, p);
        }
    }

    public void addChild(LocalScope ls){
        children.add(ls);
    }
}
