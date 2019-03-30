// 2019-03-24

package Symbol;

import Absyn.FunctionDef;
import Absyn.VarDef;
import Types.Type;

import java.util.ArrayList;
import java.util.List;

public class FuncSymbol extends Symbol {
    public Type type;
    public List<VarSymbol> params;
    public String pname;
    public boolean isConstructor;

    public FuncSymbol(String n, Type t){
        super(n);
        type = t;
        pname = null;
        isConstructor = false;
    }

    public FuncSymbol(FunctionDef f){
        super(f.name);
        if (f.result == null)
            type = null;
        else
            type = f.result.type;
        params = new ArrayList<>();
        if (f.params != null){
            for (VarDef v : f.params){
                params.add(new VarSymbol(v));
            }
        }
        pname = null;
        isConstructor = f.isConstructor;
    }

    public FuncSymbol(FunctionDef f, String p){
        super(f.name);
        if (f.result == null)
            type = null;
        else
            type = f.result.type;
        params = new ArrayList<>();
        for (VarDef v : f.params){
            params.add(new VarSymbol(v));
        }
        pname = p;
        isConstructor = f.isConstructor;
    }
}
