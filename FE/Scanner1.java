// 2019-03-28

package FE;

import Absyn.*;
import Err.SomeError;
import Scope.*;
import Symbol.*;
import Types.*;

import java.util.ArrayList;
import java.util.List;

public class Scanner1 implements ASTVisitor {
    public TopScope topScope = new TopScope();
    private Scope nowScope;
    private CLASS nowClass;

    private void initBuiltinFunc(String n, Scope s, List<VarSymbol> p, Type t){
        FuncSymbol f = new FuncSymbol(n, t);
        f.params = p;
        if (!(s instanceof TopScope)){
            f.pname = "String";
        }
        s.insert(n, f);
    }

    @Override
    public void visit(Program p){
        List<VarSymbol> param1 = new ArrayList<>();
        param1.add(new VarSymbol("s", new STRING(), new Position(0, 0)));
        List<VarSymbol> param2 = new ArrayList<>();
        param2.add(new VarSymbol("i", new INT(), new Position(0, 0)));
        List<VarSymbol> param3 = new ArrayList<>();
        param3.add(new VarSymbol("i", new INT(), new Position(0, 0)));
        param3.add(new VarSymbol("j", new INT(), new Position(0, 0)));

        initBuiltinFunc("print", topScope, param1, new VOID());
        initBuiltinFunc("println", topScope, param1, new VOID());
        initBuiltinFunc("getString", topScope, new ArrayList<>(), new STRING());
        initBuiltinFunc("getInt", topScope, new ArrayList<>(), new INT());
        initBuiltinFunc("toString", topScope, param2, new STRING());

        ClassSymbol str = new ClassSymbol("String", new CLASS("String"), topScope);
        topScope.insert("String", str);
        initBuiltinFunc("length", str.scope, new ArrayList<>(), new INT());
        initBuiltinFunc("parseInt", str.scope, new ArrayList<>(), new INT());
        initBuiltinFunc("ord", str.scope, param2, new INT());
        initBuiltinFunc("substring", str.scope, param3, new STRING());

        ClassSymbol arr = new ClassSymbol("Array", new CLASS("Array"), topScope);
        topScope.insert("Array", arr);
        initBuiltinFunc("size", arr.scope, new ArrayList<>(), new INT());

        if (p.defs != null) {
            for (Definitions d : p.defs) {
                if (d instanceof ClassDef) {
                    topScope.insert(((ClassDef) d).name, new ClassSymbol((ClassDef) d, topScope));
                }
            }
        }

        nowScope = topScope;
        nowClass = null;
        if (p.defs != null) {
            for (Definitions d : p.defs) {
                d.accept(this);
            }
        }

        FuncSymbol m = (FuncSymbol)topScope.get("main");
        if (m == null)
            throw new SomeError("main not found");
        else if (!(m.type instanceof INT))
            throw new SomeError("main must return int");
        else if (!(m.params == null))
            if (!m.params.isEmpty())
                throw new SomeError("main must have no params");
    }

    @Override
    public void visit(FunctionDef fd){
        FuncSymbol fs = new FuncSymbol(fd);
        if (nowClass != null)
            fs.pname = nowClass.name;
        nowScope.insert(fd.name, fs);
        fd.body.accept(this);
        nowScope = fd.body.scope;
        if (fd.params != null) {
            for (VarDef vd : fd.params)
                vd.accept(this);
        }
        nowScope = ((LocalScope)nowScope).parent;
    }

    @Override
    public void visit(VarDef vd){
        VarSymbol vs = new VarSymbol(vd);
        if (nowScope instanceof TopScope)
            vs.isGlobal = true;
        if (nowClass != null)
            vs.pname = nowClass.name;
        assert (vs.isGlobal != (vs.pname == null));
        nowScope.insert(vd.name, vs);
    }

    @Override
    public void visit(VarDefList vl){
        if (vl.varList != null) {
            for (VarDef vd : vl.varList)
                vd.accept(this);
        }
    }

    @Override
    public void visit(ClassDef cd){
        ClassSymbol cs = (ClassSymbol)topScope.get(cd.name);
        VarSymbol vs = new VarSymbol("this", new CLASS(cd.name), new Position(0, 0));
        cs.scope.insert("this", vs);
        nowScope = cs.scope;
        nowClass = (CLASS)cs.type;
        if (cd.varMem != null) {
            for (VarDef vd : cd.varMem)
                vd.accept(this);
        }
        if (cd.funMem != null) {
            for (FunctionDef fd : cd.funMem)
                fd.accept(this);
        }
        nowClass = null;
        nowScope = cs.scope.parent;
    }

    @Override
    public void visit(BlockStmt bs){
        bs.setNewScope(nowScope);
        nowScope = bs.scope;
        if (bs.stmts != null) {
            for (Absyn a : bs.stmts) {
                assert (a instanceof VarDef || a instanceof Stmt);
                a.accept(this);
            }
        }
        nowScope = bs.scope.parent;
    }

    @Override
    public void visit(IfStmt is){
        if (is.thenClause != null){
            is.thenClause.accept(this);
        }
        if (is.elseClause != null) {
            is.elseClause.accept(this);
        }
    }

    @Override
    public void visit(ExprStmt es){}

    @Override
    public void visit(ForStmt fs){
        if (fs.body != null)
            fs.body.accept(this);
    }

    @Override
    public void visit(WhileStmt ws){
        if (ws.body != null)
            ws.body.accept(this);
    }

    @Override
    public void visit(ContinueStmt cs){}

    @Override
    public void visit(RetStmt rs){}

    @Override
    public void visit(BreakStmt brs){}

    @Override
    public void visit(FunCallExpr fce){}

    @Override
    public void visit(MemAccessExpr mae){}

    @Override
    public void visit(SufIncDecExpr side){}

    @Override
    public void visit(ArrayIndexExpr aie){}

    @Override
    public void visit(NewExpr ne){}

    @Override
    public void visit(UnaryExpr ue){}

    @Override
    public void visit(BinaryExpr be){}

    @Override
    public void visit(IdExpr ie){}

    @Override
    public void visit(ThisExpr te){}

    @Override
    public void visit(IntExpr lie){}

    @Override
    public void visit(StringExpr lse){}

    @Override
    public void visit(BoolExpr lbe){}

    @Override
    public void visit(NullExpr lne){}

    @Override
    public void visit(Ty t){}
}
