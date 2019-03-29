// 2019-03-29

package FE;

import Absyn.*;
import Err.SomeError;
import Scope.*;
import Symbol.*;
import Types.*;

public class Scanner2 implements ASTVisitor {
    public TopScope topScope = new TopScope();
    private Scope nowScope;
    private int loop;
    private boolean checkParam;
    private CLASS nowClass;
    private Type nowFuncType;
    private FuncSymbol nowFunc;

    @Override
    public void visit(Program p){
        nowScope = topScope;
        loop = 0;
        checkParam = false;
        nowClass = null;
        nowFuncType = null;
        nowFunc = null;

        for (Definitions d : p.defs)
            d.accept(this);
    }

    @Override
    public void visit(FunctionDef fd){
        FuncSymbol fs = (FuncSymbol)nowScope.get(fd.name);
        if (fs == null)
            throw new SomeError(fd.pos, "unknown error");
        // check return type
        if (fs.type instanceof CLASS){
            topScope.afind(((CLASS)fs.type).name);
        }
        // check parameters
        checkParam = true;
        nowScope = fd.body.scope;
        for (VarDef vd : fd.params)
            vd.accept(this);
        nowScope = ((LocalScope)nowScope).parent;
        checkParam = false;
        // check constructor
        if (fs.isConstructor){
            if (nowClass == null || !fs.pname.equals(nowClass.name))
                throw new SomeError(fd.pos,"Constructor must have the same name as class");
        }
        else if (fs.type == null)
            throw new SomeError(fd.pos,"Constructor must have the same name as class");
        // visit body
        nowFuncType = fs.type;
        fd.body.accept(this);
        nowFuncType = null;
    }

    @Override
    public void visit(VarDef vd){
        VarSymbol vs = (VarSymbol)nowScope.get(vd.name);
        if (vs.type instanceof CLASS){
            topScope.afind(((CLASS)vs.type).name);
        }
        if (vs.type instanceof VOID)
            throw new SomeError(vd.pos, "variable cannot be void type");
        if (checkParam){
            if (vd.init != null)
                throw new SomeError(vd.pos, "params cannot be default initialized");
        }
        else{
            vd.init.accept(this);
            if (!vs.type.coerceTo(vd.init.rtype.type))
                throw new SomeError(vd.pos, "invalid init expression");
        }
    }

    @Override
    public void visit(VarDefList vl){
        for (VarDef vd : vl.varList)
            vd.accept(this);
    }

    @Override
    public void visit(ClassDef cd){
        ClassSymbol cs = (ClassSymbol)topScope.get(cd.name);
        if (cs == null)
            throw new SomeError(cd.pos, "unknown error");
        nowScope = cs.scope;
        nowClass = (CLASS)cs.type;
        for (VarDef vd : cd.varMem)
            vd.accept(this);
        for (FunctionDef fd : cd.funMem)
            fd.accept(this);
        nowClass = null;
        nowScope = cs.scope.parent;
    }

    @Override
    public void visit(BlockStmt bs){
        nowScope = bs.scope;
        for (Absyn a : bs.stmts){
            a.accept(this);
        }
        nowScope = bs.scope.parent;
    }

    @Override
    public void visit(IfStmt is){
        is.test.accept(this);
        if (!(is.test.rtype.type instanceof BOOL))
            throw new SomeError(is.test.pos, "condition must be bool type");
        if (is.thenClause != null){
            is.thenClause.accept(this);
        }
        if (is.elseClause != null) {
            is.elseClause.accept(this);
        }
    }

    @Override
    public void visit(ForStmt fs){
        ++loop;
        if (fs.init != null)
            fs.init.accept(this);
        if (fs.cond != null){
            fs.cond.accept(this);
            if (!(fs.cond.rtype.type instanceof BOOL))
                throw new SomeError(fs.cond.pos, "condition must be bool type");
        }
        if (fs.step != null)
            fs.step.accept(this);
        if (fs.body != null)
            fs.body.accept(this);
        --loop;
    }

    @Override
    public void visit(WhileStmt ws){
        ++loop;
        ws.test.accept(this);
        if (!(ws.test.rtype.type instanceof BOOL))
            throw new SomeError(ws.test.pos, "condition must be bool type");
        if (ws.body != null)
            ws.body.accept(this);
        --loop;
    }

    @Override
    public void visit(ContinueStmt cs){
        if (loop <= 0)
            throw new SomeError(cs.pos, "\"continue\" must be in loop");
    }

    @Override
    public void visit(RetStmt rs){
        assert (nowFuncType != null);
        if (rs.expr == null){
            if (!(nowFuncType instanceof VOID))
                throw new SomeError(rs.pos, "non-void function must have return value");
        }
        else{
            rs.expr.accept(this);
            if ((rs.expr.rtype.type == null)
                    || (rs.expr.rtype.type instanceof VOID)
                    || !rs.expr.rtype.type.coerceTo(nowFuncType))
                throw new SomeError(rs.pos, "unexpected return type");
        }
    }

    @Override
    public void visit(BreakStmt brs){
        if (loop <= 0)
            throw new SomeError(brs.pos, "\"break\" must be in loop");
    }

    @Override
    public void visit(FunCallExpr fce){
        fce.func.accept(this);
        if (nowFunc == null)
            throw new SomeError(fce.pos, "non-callable object");
        fce.funcSymbol = nowFunc;
        // check args number
        if (nowFunc.params.size() != fce.args.size())
            throw new SomeError(fce.pos, "inconsistent argument number");
        // check args type
        for (int i = 0; i < nowFunc.params.size(); ++i){
            fce.args.get(i).accept(this);
            if (fce.args.get(i).rtype.type == null
            || fce.args.get(i).rtype.type instanceof VOID
            || !fce.args.get(i).rtype.type.coerceTo(nowFunc.params.get(i).type))
                throw new SomeError(fce.args.get(i).pos, "invalid argument type");
        }
        fce.rtype.type = nowFunc.type;
    }

    @Override
    public void visit(MemAccessExpr mae){
        mae.expr.accept(this);
        String name;
        if (mae.expr.rtype.type instanceof CLASS)
            name = ((CLASS)mae.expr.rtype.type).name;
        else if (mae.expr.rtype.type instanceof ARRAY)
            name = "Array";
        else if (mae.expr.rtype.type instanceof STRING)
            name = "String";
        else
            throw new SomeError(mae.pos, "unexpected class name");
        topScope.afind(name, new CLASS(name));
        ClassSymbol cs = (ClassSymbol)topScope.get(name);
        Symbol m = cs.scope.get(mae.mem);
        if (m == null)
            throw new SomeError(mae.pos, "member not found");
        else{
            if (m instanceof FuncSymbol) {
                nowFunc = (FuncSymbol)m;
                mae.rtype.type = ((FuncSymbol)m).type;
            }
            else if (m instanceof VarSymbol)
                mae.rtype.type = ((VarSymbol)m).type;
            else if (m instanceof ClassSymbol)
                mae.rtype.type = ((ClassSymbol)m).type;
            else
                throw new SomeError(mae.pos, "unknown member type");
        }
        mae.lvalue = true;
    }

    @Override
    public void visit(SufIncDecExpr side){
        side.expr.accept(this);
        if (!(side.expr.rtype.type instanceof INT))
            throw new SomeError(side.pos, "operand must be INT type");
        if (!side.expr.lvalue)
            throw new SomeError(side.pos, "operand must be lvalue");
        side.expr.rtype.type = new INT();
    }

    @Override
    public void visit(ArrayIndexExpr aie){
        aie.arr.accept(this);
        if (!(aie.arr.rtype.type instanceof ARRAY))
            throw new SomeError(aie.pos, "non-index object");
        aie.index.accept(this);
        if (!(aie.index.rtype.type instanceof INT))
            throw new SomeError(aie.pos, "index must be INT type");
        aie.rtype.type = ((ARRAY)aie.arr.rtype.type).element;
        aie.lvalue = true;
    }

    @Override
    public void visit(NewExpr ne){
        if (ne.dim != 0){
            for (Expr e : ne.dimExpr){
                e.accept(this);
                if (!(e.rtype.type instanceof INT))
                    throw new SomeError(e.pos, "dimension must be INT type");
            }
        }
        ne.rtype.type = ne.type.type;
    }

    @Override
    public void visit(UnaryExpr ue){
        ue.expr.accept(this);
        switch (ue.op){
            case 0: case 1: // Inc, Dec
                if (!(ue.expr.rtype.type instanceof INT))
                    throw new SomeError(ue.pos, "operand must be INT type");
                if (!ue.lvalue)
                    throw new SomeError(ue.pos, "operand must be lvalue");
                ue.rtype.type = new INT();
                ue.lvalue = true;
                break;
            case 2: case 3: // Pos, Neg
                if (!(ue.expr.rtype.type instanceof INT))
                    throw new SomeError(ue.pos, "operand must be INT type");
                ue.rtype.type = new INT();
                break;
            case 4: // LNot
                if (!(ue.expr.rtype.type instanceof BOOL))
                    throw new SomeError(ue.pos, "operand must be BOOL type");
                ue.rtype.type = new BOOL();
                break;
            case 5: // BNot
                if (!(ue.expr.rtype.type instanceof INT))
                    throw new SomeError(ue.pos, "operand must be INT type");
                ue.rtype.type = new INT();
                break;
            default:
                throw new SomeError(ue.pos, "unexpected operand");
        }
    }

    @Override
    public void visit(BinaryExpr be){
        be.lhs.accept(this);
        be.rhs.accept(this);
        switch (be.op){
            case MUL: case DIV: case MOD:
            case SUB:
            case SLA: case SRA:
            case BAND: case BOR: case BXOR:
                if (!(be.lhs.rtype.type instanceof INT && be.rhs.rtype.type instanceof INT))
                    throw new SomeError(be.pos, "operands must be INT type");
                be.rtype.type = new INT();
                break;
            case ADD:
            case LES: case LTE: case GRT: case GTE:
                if (!((be.lhs.rtype.type instanceof INT && be.rhs.rtype.type instanceof INT)
                || (be.lhs.rtype.type instanceof STRING && be.rhs.rtype.type instanceof STRING)))
                    throw new SomeError(be.pos, "operands must be INT or STRING type");
                if (be.lhs.rtype.type instanceof INT)
                    be.rtype.type = new INT();
                else
                    be.rtype.type = new STRING();
                break;
            case EQL: case NEQ:
                if (!be.lhs.rtype.type.coerceTo(be.rhs.rtype.type)
                && !be.rhs.rtype.type.coerceTo(be.lhs.rtype.type))
                    throw new SomeError(be.pos, "operands must be the same type");
                be.rtype.type = new BOOL();
                break;
            case LAND: case LOR:
                if (!(be.lhs.rtype.type instanceof BOOL && be.rhs.rtype.type instanceof BOOL))
                    throw new SomeError(be.pos, "operands must be BOOL type");
                be.rtype.type = new BOOL();
                break;
            case ASS:
                if (!be.lhs.lvalue)
                    throw new SomeError(be.pos, "lhs must be lvalue");
                if (!be.rhs.rtype.type.coerceTo(be.lhs.rtype.type))
                    throw new SomeError(be.pos, "rhs' type cannot be converted to lhs' type");
                be.rtype.type = be.lhs.rtype.type.actual();
                break;
            default:
                throw new SomeError(be.pos, "unexpected operator");
        }
    }

    @Override
    public void visit(IdExpr ie){
        Symbol s = nowScope.get(ie.id);
        if (s instanceof VarSymbol){
            ie.lvalue = true;
            ie.rtype.type = ((VarSymbol)s).type;
        }
        else if (s instanceof FuncSymbol){
            nowFunc = (FuncSymbol)s;
            ie.rtype.type = ((FuncSymbol)s).type;
        }
        else
            throw new SomeError(ie.pos, "unexpected id expression");
    }

    @Override
    public void visit(ThisExpr te){
        Symbol s = nowScope.get("this");
        if (!(s instanceof VarSymbol))
            throw new SomeError(te.pos, "unexpected \"this\" expression");
        te.rtype.type = ((VarSymbol)s).type;
    }
    @Override
    public void visit(IntExpr lie){
        lie.rtype.type = new INT();
    }

    @Override
    public void visit(StringExpr lse){
        lse.rtype.type = new STRING();
    }

    @Override
    public void visit(BoolExpr lbe){
        lbe.rtype.type = new BOOL();
    }

    @Override
    public void visit(NullExpr lne){
        lne.rtype.type = new NULL();
    }

    @Override
    public void visit(Ty t) {}
}
