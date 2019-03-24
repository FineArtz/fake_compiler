// 2019-03-24

package Symbol;

import Absyn.VarDef;
import Types.Type;

public class VarSymbol extends Symbol {
    public Type type;
    public String pname;
    boolean isGlobal;

    public VarSymbol(String n, Type t){
        super(n);
        type = t;
        pname = null;
        isGlobal = false;
    }

    public VarSymbol(String n, Type t, String p){
        super(n);
        type = t;
        pname = p;
        isGlobal = false;
    }

    public VarSymbol(VarDef v){
        super(v.name);
        type = v.type.type;
        pname = null;
        isGlobal = false;
    }
}
