// 2019-03-24

package Symbol;

import Absyn.ClassDef;
import Absyn.FunctionDef;
import Scope.*;
import Types.CLASS;
import Types.Type;

public class ClassSymbol extends Symbol {
    public Type type;
    public LocalScope scope;

    public ClassSymbol(String n, Type t, Scope s){
        super(n);
        type = t;
        scope = new LocalScope(s);
    }

    public ClassSymbol(ClassDef c, Scope s){
        super(c.name);
        type = new CLASS(c.name);
        scope = new LocalScope(s);
        /*for (FunctionDef f : c.funMem){
            scope.insert(f.name, new FuncSymbol(f, c.name));
        }*/
    }
}
