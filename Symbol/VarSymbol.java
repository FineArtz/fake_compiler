// 2019-03-24

package Symbol;

import Absyn.Position;
import Absyn.VarDef;
import IR.Reg;
import Types.Type;

public class VarSymbol extends Symbol {
    public Type type;
    public String pname;
    public Position pos;
    public boolean isGlobal;
    public Reg reg;
    public int offset;

    public VarSymbol(String n, Type t, Position p){
        super(n);
        type = t;
        pos = p;
        pname = null;
        isGlobal = false;
    }

    public VarSymbol(String n, Type t, String p, Position pp){
        super(n);
        type = t;
        pname = p;
        pos = pp;
        isGlobal = false;
    }

    public VarSymbol(VarDef v){
        super(v.name);
        type = v.type.type;
        pos = v.pos;
        pname = null;
        isGlobal = false;
    }
}
