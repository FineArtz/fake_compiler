// 2019-03-24

package Symbol;

import Absyn.FunctionDef;
import Absyn.Position;
import Absyn.VarDef;
import Types.INT;
import Types.STRING;
import Types.Type;
import Types.VOID;

import java.util.ArrayList;
import java.util.Arrays;
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

    private FuncSymbol(String n, Type t, String pn, VarSymbol... p) {
        super(n);
        type = t;
        pname = pn;
        params = new ArrayList<>(Arrays.asList(p));
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

    // built-in functions
    private static final Position virtualPos = new Position(0, 0);
    public static final FuncSymbol PRINT = new FuncSymbol("print", new VOID(), null, new VarSymbol("s", new STRING(), virtualPos));
    public static final FuncSymbol PRINTLN = new FuncSymbol("println", new VOID(), null, new VarSymbol("s", new STRING(), virtualPos));
    public static final FuncSymbol PRINT_INT = new FuncSymbol("printInt", new VOID(), null, new VarSymbol("i", new INT(), virtualPos));
    public static final FuncSymbol PRINTLN_INT = new FuncSymbol("printlnInt", new VOID(), null, new VarSymbol("i", new INT(), virtualPos));
    public static final FuncSymbol GET_STRING = new FuncSymbol("getString", new STRING(), null);
    public static final FuncSymbol GET_INT = new FuncSymbol("getInt", new INT(), null);
    public static final FuncSymbol TO_STRING = new FuncSymbol("toString", new STRING(), null, new VarSymbol("i", new INT(), virtualPos));
    public static final FuncSymbol STR_CONCAT = new FuncSymbol("str_concat", new STRING(), null, new VarSymbol("s1", new STRING(), virtualPos), new VarSymbol("s2", new STRING(), virtualPos));
    public static final FuncSymbol STR_EQUAL = new FuncSymbol("str_equal", new STRING(), null, new VarSymbol("s1", new STRING(), virtualPos), new VarSymbol("s2", new STRING(), virtualPos));
    public static final FuncSymbol STR_NOT_EQUAL = new FuncSymbol("str_not_equal", new STRING(), null, new VarSymbol("s1", new STRING(), virtualPos), new VarSymbol("s2", new STRING(), virtualPos));
    public static final FuncSymbol STR_LESS = new FuncSymbol("str_less", new STRING(), null, new VarSymbol("s1", new STRING(), virtualPos), new VarSymbol("s1", new STRING(), virtualPos));
    public static final FuncSymbol STR_LTE = new FuncSymbol("str_lte", new STRING(), null, new VarSymbol("s1", new STRING(), virtualPos), new VarSymbol("s2", new STRING(), virtualPos));

    public static final FuncSymbol LENGTH = new FuncSymbol("length", new INT(), "$String$");
    public static final FuncSymbol PARSE_INT = new FuncSymbol("parseInt", new INT(), "$String$");
    public static final FuncSymbol ORD = new FuncSymbol("ord", new INT(), "$String$", new VarSymbol("i", new INT(), virtualPos));
    public static final FuncSymbol SUBSTRING = new FuncSymbol("substring", new STRING(), "$String$", new VarSymbol("i", new INT(), virtualPos), new VarSymbol("j", new INT(), virtualPos));

    public static final FuncSymbol SIZE = new FuncSymbol("size", new INT(), "$Array$");
}
