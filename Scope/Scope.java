// 2019-03-24

package Scope;

import Symbol.Symbol;

import java.util.List;
import java.util.Map;

abstract public class Scope{
    public List<LocalScope> children;
    public Map<String, Symbol> symbol;

    public boolean insert(String k, Symbol v){
        if (symbol.containsKey(k))
            return false;
        else{
            symbol.put(k, v);
            return true;
        }
    }
}
