// 2019-04-27

package FE;

import Absyn.*;
import Err.SomeError;
import IR.*;
import Scope.Scope;
import Scope.TopScope;
import Scope.LocalScope;
import Symbol.ClassSymbol;
import Symbol.FuncSymbol;
import Symbol.VarSymbol;
import Types.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IRBuilder implements ASTVisitor {
    private static final int REG_SIZE = 8;
    private static final Position virtualPos = new Position(0, 0);

    private IRRoot root = new IRRoot();
    private TopScope topScope;
    private Scope nowScope;
    private boolean checkParam;
    private BasicBlock nowBB;
    private Function nowFunc;
    private CLASS nowClass;

    // for break and continue statements
    private BasicBlock nowLoopStepBB;
    private BasicBlock nowLoopFinalBB;

    // for memory access instructions
    private boolean needAddr;

    // Global vars are stored in .data section
    // Their initializations are moved to a new function "__init__"
    // which is called as the first instruction in "main"
    private List<GlobalInit> globalInitList = new ArrayList<>();

    public IRBuilder(TopScope s) {
        topScope = s;
    }

    public IRRoot getRoot() {
        return root;
    }

    @Override
    public void visit(Program p) {
        nowScope = topScope;
        checkParam = false;
        nowBB = null;
        nowFunc = null;
        nowClass = null;
        nowLoopStepBB = null;
        nowLoopFinalBB = null;
        needAddr = false;

        // global variables
        // globalInit() returns "__init__" function
        for (Definitions d : p.defs) {
            if (d instanceof VarDef) {
                d.accept(this);
            }
        }
        FunctionDef fd = globalInit();
        fd.accept(this);

        for (Definitions d : p.defs) {
            if (!(d instanceof VarDef)) {
                d.accept(this);
            }
        }

        for (Function f : root.funcs.values()) {
            f.updateCallee();
        }
        root.updateRCallee();
    }

    @Override
    public void visit(FunctionDef fd) {
        nowFunc = new Function(new FuncSymbol(fd));
        if (nowClass == null) {
            root.funcs.put(fd.name, nowFunc);
        }
        else {
            root.funcs.put(nowClass.name + "." + fd.name, nowFunc);
        }
        nowBB = nowFunc.getHead();

        checkParam = true;
        nowScope = fd.body.scope;
        if (nowClass != null) {
            VarSymbol vs = (VarSymbol)nowScope.get("this");
            vs.reg = new VirtualReg(nowClass.name + ".this");
            nowFunc.addArg((VirtualReg)vs.reg);
        }
        if (fd.params != null) {
            for (VarDef v : fd.params) {
                v.accept(this);
            }
        }
        nowScope = ((LocalScope) nowScope).parent;
        checkParam = false;

        // global variables initializer
        if (fd.name.equals("main")) {
            nowBB.addInst(new CALL(nowBB, root.funcs.get("__init__"), new ArrayList<>(), null));
        }

        fd.body.accept(this);
        if (!nowBB.isEnd()) {
            if (nowFunc.getFunc().type instanceof VOID) {
                nowBB.addJumpInst(new RETURN(nowBB, null));
            }
            else {
                nowBB.addJumpInst(new RETURN(nowBB, new CONST(0)));
            }
        }

        // merge return statement
        if (nowFunc.returnList.size() > 1) {
            BasicBlock exitBB = new BasicBlock(nowFunc, nowFunc.getName() + ".Exit");
            VirtualReg reg = (nowFunc.getFunc().type instanceof VOID ? null : new VirtualReg("ret_value"));
            List<RETURN> retList = new ArrayList<>(nowFunc.returnList);
            for (RETURN r : retList) {
                BasicBlock bb = r.getBB();
                if (r.getRetVal() != null) {
                    r.insertPred(new MOVE(bb, reg, r.getRetVal()));
                }
                bb.rmvJumpInst();
                bb.addJumpInst(new JUMP(bb, exitBB));
            }
            exitBB.addJumpInst(new RETURN(exitBB, reg));
            nowFunc.setTail(exitBB);
        }
        else {
            nowFunc.setTail(nowFunc.returnList.get(0).getBB());
        }

        // remove unreachable blocks
        nowFunc.getTail().getPred().retainAll(nowFunc.getrPostOrder());

        nowBB = null;
        nowFunc = null;
    }

    @Override
    public void visit(VarDef vd) {
        VarSymbol var = (VarSymbol) nowScope.get(vd.name, vd.pos);
        if (nowScope instanceof TopScope) {
            StaticData sd = new StaticSpace(vd.name, REG_SIZE);
            root.data.add(sd);
            var.reg = sd;
            if (vd.init != null) {
                GlobalInit init = new GlobalInit(vd.name, vd.init);
                globalInitList.add(init);
            }
        }
        else {
            VirtualReg reg = new VirtualReg(vd.name);
            var.reg = reg;
            if (checkParam) {
                nowFunc.addArg(reg);
            }
            if (vd.init != null) {
                if (isCondExpr(vd.init)) {
                    vd.init.trueBB = new BasicBlock(nowFunc, null);
                    vd.init.falseBB = new BasicBlock(nowFunc, null);
                }
                vd.init.accept(this);
                assign(vd.init.rtype.type.getSize(), reg, 0, vd.init, false);
            }
            else {
                // do nothing
                // should not set uninitialized local variable with 0
            }
        }
    }

    @Override
    public void visit(VarDefList vl) {
        if (!vl.varList.isEmpty()) {
            for (VarDef vd : vl.varList) {
                vd.accept(this);
            }
        }
    }

    @Override
    public void visit(ClassDef cd) {
        ClassSymbol cs = (ClassSymbol) (topScope.get(cd.name));
        nowScope = cs.scope;
        nowClass = (CLASS) (cs.type);
        if (cd.funMem != null) {
            for (FunctionDef fd : cd.funMem) {
                fd.accept(this);
            }
        }
        nowClass = null;
        nowScope = ((LocalScope) nowScope).parent;
    }

    @Override
    public void visit(BlockStmt bs) {
        nowScope = bs.scope;
        if (bs.stmts != null) {
            for (Absyn a : bs.stmts) {
                a.accept(this);
            }
        }
        nowScope = ((LocalScope) nowScope).parent;
    }

    @Override
    public void visit(IfStmt is) {
        BasicBlock thenBB = new BasicBlock(nowFunc, "if_true");
        BasicBlock elseBB = (is.elseClause == null ? null : new BasicBlock(nowFunc, "if_false"));
        BasicBlock finalBB = new BasicBlock(nowFunc, "if_final");
        is.test.trueBB = thenBB;
        is.test.falseBB = (is.elseClause == null ? finalBB : elseBB);
        is.test.accept(this);
        if (is.test instanceof BoolExpr) {
            nowBB.addJumpInst(new JUMP(nowBB, ((CONST)is.test.value).getVal() == 1 ? is.test.trueBB : is.test.falseBB));
        }

        nowBB = thenBB;
        is.thenClause.accept(this);
        if (!nowBB.isEnd()) {
            nowBB.addJumpInst(new JUMP(nowBB, finalBB));
        }

        if (is.elseClause != null) {
            nowBB = elseBB;
            is.elseClause.accept(this);
            if (!nowBB.isEnd()) {
                nowBB.addJumpInst(new JUMP(nowBB, finalBB));
            }
        }

        nowBB = finalBB;
    }

    @Override
    public void visit(ExprStmt es) {
        es.expr.accept(this);
    }

    @Override
    public void visit(ForStmt fs) {
        BasicBlock bodyBB = new BasicBlock(nowFunc, "for_body");
        BasicBlock condBB = (fs.cond == null ? bodyBB : new BasicBlock(nowFunc, "for_cond"));
        BasicBlock stepBB = (fs.step == null ? condBB : new BasicBlock(nowFunc, "for_step"));
        BasicBlock finalBB = new BasicBlock(nowFunc, "for_final");
        root.fors.add(new IRRoot.ForStruct(condBB, stepBB, bodyBB, finalBB));
        BasicBlock tmpLoopStepBB = nowLoopStepBB;
        BasicBlock tmpLoopFinalBB = nowLoopFinalBB;
        nowLoopStepBB = stepBB;
        nowLoopFinalBB = finalBB;

        // init
        if (fs.init != null) {
            fs.init.accept(this);
        }
        nowBB.addJumpInst(new JUMP(nowBB, condBB));

        // cond
        if (fs.cond != null) {
            nowBB = condBB;
            fs.cond.trueBB = bodyBB;
            fs.cond.falseBB = finalBB;
            fs.cond.accept(this);
        }

        // step
        if (fs.step != null) {
            nowBB = stepBB;
            fs.step.accept(this);
            nowBB.addJumpInst(new JUMP(nowBB, condBB));
        }

        // body
        nowBB = bodyBB;
        if (fs.body != null) {
            fs.body.accept(this);
        }
        if (!nowBB.isEnd()) {
            nowBB.addJumpInst(new JUMP(nowBB, stepBB));
        }

        // exit
        nowLoopStepBB = tmpLoopStepBB;
        nowLoopFinalBB = tmpLoopFinalBB;
        nowBB = finalBB;
    }

    @Override
    public void visit(WhileStmt ws) {
        BasicBlock testBB = new BasicBlock(nowFunc, "while_cond");
        BasicBlock bodyBB = new BasicBlock(nowFunc, "while_body");
        BasicBlock finalBB = new BasicBlock(nowFunc, "while_final");
        BasicBlock tmpLoopStepBB = nowLoopStepBB;
        BasicBlock tmpLoopFinalBB = nowLoopFinalBB;
        nowLoopStepBB = testBB;
        nowLoopFinalBB = finalBB;

        // cond
        nowBB.addJumpInst(new JUMP(nowBB, testBB));
        nowBB = testBB;
        ws.test.trueBB = bodyBB;
        ws.test.falseBB = finalBB;
        ws.test.accept(this);

        // body
        nowBB = bodyBB;
        ws.body.accept(this);
        nowBB.addJumpInst(new JUMP(nowBB, testBB));

        // exit
        nowLoopStepBB = tmpLoopStepBB;
        nowLoopFinalBB = tmpLoopFinalBB;
        nowBB = finalBB;
    }

    @Override
    public void visit(ContinueStmt cs) {
        nowBB.addJumpInst(new JUMP(nowBB, nowLoopStepBB));
    }

    @Override
    public void visit(RetStmt rs) {
        if (nowFunc.getFunc().type == null || nowFunc.getFunc().type instanceof VOID) {
            nowBB.addJumpInst(new RETURN(nowBB, null));
        }
        else {
            if (isCondExpr(rs.expr)) {
                rs.expr.trueBB = new BasicBlock(nowFunc, null);
                rs.expr.falseBB = new BasicBlock(nowFunc, null);
                rs.expr.accept(this);

                VirtualReg reg = new VirtualReg("ret_bool_val");
                assign((new INT()).getSize(), reg, 0, rs.expr, false);
                nowBB.addJumpInst(new RETURN(nowBB, rs.expr.value));
            }
            else {
                rs.expr.accept(this);
                nowBB.addJumpInst(new RETURN(nowBB, rs.expr.value));
            }
        }
    }

    @Override
    public void visit(BreakStmt brs) {
        nowBB.addJumpInst(new JUMP(nowBB, nowLoopFinalBB));
    }

    @Override
    public void visit(FunCallExpr fce) {
        FuncSymbol fs = fce.funcSymbol;
        String funcName = fs.name;
        List<Reg> args = new ArrayList<>();
        Expr thisExp = null;

        // member function
        // regard "this" as an argument
        if (fs.pname != null) {
            thisExp = (fce.func instanceof MemAccessExpr ? ((MemAccessExpr) (fce.func)).expr : new ThisExpr(null));
            if (thisExp.rtype.type instanceof NULL) {
                thisExp.rtype.type = nowClass;
            }
            thisExp.accept(this);
            args.add(thisExp.value);
        }

        if (processBuiltInFuncCall(fce, thisExp)) {
            return;
        }

        // non built-in functions
        for (Expr e : fce.args) {
            e.accept(this);
            args.add(e.value);
        }
        Function f = root.funcs.get(funcName);
        if (f == null) {
            f = new Function(fs);
        }
        VirtualReg reg = new VirtualReg(null);
        nowBB.addInst(new CALL(nowBB, f, args, reg));
        fce.value = reg;
        if (fce.trueBB != null) {
            nowBB.addInst(new CJUMP(nowBB, fce.value, fce.trueBB, fce.falseBB));
        }
    }

    @Override
    public void visit(MemAccessExpr mae) {
        boolean tmpNeedAddr = needAddr;
        needAddr = false;
        mae.expr.accept(this);
        needAddr = tmpNeedAddr;

        Reg classAddr = mae.expr.value;
        String className = ((CLASS)(mae.expr.rtype.type)).name;
        ClassSymbol c = (ClassSymbol)(topScope.get(className));
        VarSymbol v = (VarSymbol)c.scope.get(mae.mem);

        if (needAddr) {
            mae.addr = classAddr;
            mae.offset = v.offset;
        }
        else {
            VirtualReg reg = new VirtualReg(null);
            mae.value = reg;
            nowBB.addInst(new LOAD(nowBB, reg, v.type.getSize(), classAddr, v.offset));
            if (mae.trueBB != null) {
                nowBB.addJumpInst(new CJUMP(nowBB, mae.value, mae.trueBB, mae.falseBB));
            }
        }
    }

    @Override
    public void visit(SufIncDecExpr side) {
        processIncDec(side.expr, side, side.op == 0, true);
    }

    @Override
    public void visit(ArrayIndexExpr aie) {
        boolean tmpNeedAddr = needAddr;
        needAddr = false;
        aie.arr.accept(this);
        aie.index.accept(this);
        needAddr = tmpNeedAddr;

        // calc memory address
        VirtualReg reg = new VirtualReg(null);
        CONST size = new CONST(aie.rtype.type.getSize());
        nowBB.addInst(new BINOP(nowBB, BINOP.OP.MUL, aie.index.value, size, reg));
        nowBB.addInst(new BINOP(nowBB, BINOP.OP.ADD, aie.arr.value, reg, reg));
        if (needAddr) {
            aie.addr = reg;
            aie.offset = (new INT()).getSize();
        }
        else {
            nowBB.addInst(new LOAD(nowBB, reg, aie.rtype.type.getSize(), reg, (new INT()).getSize()));
            aie.value = reg;
            if (aie.trueBB != null) {
                nowBB.addJumpInst(new CJUMP(nowBB, aie.value, aie.trueBB, aie.falseBB));
            }
        }
    }

    @Override
    public void visit(NewExpr ne) {
        Type type = ne.type.type;
        VirtualReg reg = new VirtualReg(null);
        if (type instanceof ARRAY) {
            processNewArray(ne, 0, reg, null);
        }
        else {
            // new CLASS
            ClassSymbol c = (ClassSymbol)topScope.get(((CLASS)type).name);
            nowBB.addInst(new ALLOC(nowBB, reg, new CONST(c.memorySize)));
            // constructor
            Function func = root.funcs.get(c.name + "." + c.name);
            if (func != null) {
                List<Reg> args = new ArrayList<>();
                // add "this" to argument list
                args.add(reg);
                nowBB.addInst(new CALL(nowBB, func, args, null));
            }
        }
        ne.value = reg;
    }

    @Override
    public void visit(UnaryExpr ue) {
        VirtualReg reg = new VirtualReg(null);
        if (ue.op == 4) {
            // logical not
            ue.expr.trueBB = ue.falseBB;
            ue.expr.falseBB = ue.trueBB;
        }
        ue.expr.accept(this);
        switch (ue.op) {
            case 0: // prefixInc
            case 1: // prefixDec
                processIncDec(ue.expr, ue, ue.op == 0, false);
                break;
            case 2: // positive
                ue.value = ue.expr.value;
                break;
            case 3: // negative
                ue.value = reg;
                nowBB.addInst(new UNOP(nowBB, UNOP.OP.NEG, ue.expr.value, reg));
                break;
            case 4: // logical not
                break;
            case 5: // binary not
                ue.value = reg;
                nowBB.addInst(new UNOP(nowBB, UNOP.OP.BNOT, ue.expr.value, reg));
                break;
            default:
                throw new SomeError("in IRVisit UnaryExpr: unexpected operator.");
        }
    }

    @Override
    public void visit(BinaryExpr be) {
        switch (be.op) {
            case MUL:
            case DIV:
            case MOD:
            case ADD:
            case SUB:
            case SLA:
            case SRA:
            case BAND:
            case BXOR:
            case BOR:
                if (be.lhs.rtype.type instanceof STRING) {
                    processStringBinary(be);
                }
                else {
                    processIntBinary(be);
                }
                break;
            case LES:
            case GRT:
            case LTE:
            case GTE:
            case EQL:
            case NEQ:
                if (be.lhs.rtype.type instanceof STRING) {
                    processStringBinary(be);
                }
                else {
                    processIntBinary(be);
                }
                break;
            case LAND:
            case LOR:
                processLogicalBinary(be);
                break;
            case ASS:
                processAssign(be);
                break;
            default:
                throw new SomeError("In IRVisitor BinaryExpr: unexpected operator.");
        }
    }

    @Override
    public void visit(IdExpr ie) {
        VarSymbol var = ie.var;
        // id[] or id;
//        if (var.type instanceof ARRAY || var.isGlobal) {
//            return;
//        }

        if (var.reg == null) {
            // this.id
            ThisExpr te = new ThisExpr(null);
            te.rtype.type = nowClass;
            MemAccessExpr mae = new MemAccessExpr(null, te, ie.id);
            mae.accept(this);
            if (needAddr) {
                ie.addr = mae.addr;
                ie.offset = mae.offset;
            }
            else {
                ie.value = mae.value;
                if (ie.trueBB != null) {
                    nowBB.addJumpInst(new CJUMP(nowBB, ie.value, ie.trueBB, ie.falseBB));
                }
            }
        }
        else {
            ie.value = var.reg;
            if (ie.trueBB != null) {
                nowBB.addJumpInst(new CJUMP(nowBB, ie.value, ie.trueBB, ie.falseBB));
            }
        }
    }

    @Override
    public void visit(ThisExpr te) {
        VarSymbol var = (VarSymbol)nowScope.get("this");
        te.value = var.reg;
        if (te.trueBB != null) {
            nowBB.addJumpInst(new CJUMP(nowBB, te.value, te.trueBB, te.falseBB));
        }
    }

    @Override
    public void visit(IntExpr lie) {
        lie.value = new CONST(lie.val);
    }

    @Override
    public void visit(StringExpr lse) {
        StaticString ss = root.strs.get(lse.val);
        if (ss == null) {
            ss = new StaticString(lse.val);
            root.strs.put(lse.val, ss);
        }
        lse.value = ss;
    }

    @Override
    public void visit(BoolExpr lbe) {
        lbe.value = new CONST(lbe.val ? 1 : 0);
    }

    @Override
    public void visit(NullExpr lne) {
        lne.value = new CONST(0);
    }

    @Override
    public void visit(Ty t) {
        // do nothing
    }

    // global variables initializer
    private FunctionDef globalInit() {
        List<Absyn> stmt = new ArrayList<>();
        for (GlobalInit g : globalInitList) {
            IdExpr id = new IdExpr(virtualPos, g.getName());
            id.var = (VarSymbol)topScope.get(g.getName());
            BinaryExpr be = new BinaryExpr(virtualPos, BinaryExpr.OP.ASS, id, g.getExpr());
            stmt.add(be);
        }
        BlockStmt body = new BlockStmt(virtualPos, stmt, new ArrayList<>());
        body.setNewScope(topScope);
        Ty rType = new Ty(virtualPos, new VOID());
        FunctionDef fd = new FunctionDef(virtualPos, "__init__", new ArrayList<>(), rType, body);
        FuncSymbol fs = new FuncSymbol(fd);
        topScope.insert("__init__", fs);
        Function f = new Function(fs);
        root.funcs.put("__init__", f);
        return fd;
    }

    private boolean isCondExpr(Expr expr) {
        if (expr instanceof UnaryExpr) {
            return ((UnaryExpr) expr).op == 4;
        }
        else if (expr instanceof BinaryExpr) {
            BinaryExpr.OP op = ((BinaryExpr) expr).op;
            return op == BinaryExpr.OP.LAND || op == BinaryExpr.OP.LOR;
        }
        else
            return false;
    }

    private boolean isMemoryAccess(Expr expr) {
        if (expr instanceof MemAccessExpr || expr instanceof ArrayIndexExpr)
            return true;

        if (expr instanceof IdExpr) {
            if (nowClass != null) {
                // class.id
                VarSymbol var = (VarSymbol)nowScope.get(((IdExpr)expr).id);
                ((IdExpr)expr).needMem = (var.reg == null);
            }
            else {
                ((IdExpr)expr).needMem = false;
            }
            return ((IdExpr)expr).needMem;
        }

        return false;
    }

    private void assign(int size, Reg addr, int offset, Expr rhs, boolean isMemOp) {
        if (rhs.trueBB == null) {
            if (isMemOp) {
                nowBB.addInst(new STORE(nowBB, rhs.value, size, addr, offset));
            }
            else {
                nowBB.addInst(new MOVE(nowBB, (CommonReg) addr, rhs.value));
            }
        }
        else {
            // short-circuit
            BasicBlock mbb = new BasicBlock(nowFunc, null);
            if (isMemOp) {
                rhs.trueBB.addInst(new STORE(nowBB, new CONST(1), size, addr, offset));
                rhs.falseBB.addInst(new STORE(nowBB, new CONST(0), size, addr, offset));
            }
            else {
                rhs.trueBB.addInst(new MOVE(nowBB, (VirtualReg) addr, new CONST(1)));
                rhs.falseBB.addInst(new MOVE(nowBB, (VirtualReg) addr, new CONST(0)));
            }
            rhs.trueBB.addJumpInst(new JUMP(nowBB, mbb));
            rhs.falseBB.addJumpInst(new JUMP(nowBB, mbb));
            nowBB = mbb;
        }
    }

    // recursively print binary expression
    // print(toString(int)) -> print(int)
    private void processPrint(Expr expr, boolean needNL) {
        if (expr instanceof BinaryExpr) {
            processPrint(((BinaryExpr) expr).lhs, false);
            processPrint(((BinaryExpr) expr).rhs, needNL);
        }
        else if (expr instanceof FunCallExpr && ((FunCallExpr) expr).funcSymbol.name.equals("toString")) {
            Expr inner = ((FunCallExpr) expr).args.get(0);
            List<Reg> args = new ArrayList<>();
            inner.accept(this);
            args.add(inner.value);
            CALL call = new CALL(nowBB, needNL ? root.builtinFuncs.get("printlnInt") : root.builtinFuncs.get("printInt"), args, null);
            nowBB.addInst(call);
        }
        else {
            List<Reg> args = new ArrayList<>();
            expr.accept(this);
            args.add(expr.value);
            CALL call = new CALL(nowBB, needNL ? root.builtinFuncs.get("println") : root.builtinFuncs.get("print"), args, null);
            nowBB.addInst(call);
        }
    }

    private boolean processBuiltInFuncCall(FunCallExpr fce, Expr thisExp) {
        boolean tmpNeedAddr = needAddr;
        needAddr = false;
        switch (fce.funcSymbol.name) {
            case "print":
            case "println": {
                processPrint(fce.args.get(0), fce.funcSymbol == root.builtinFuncs.get("println").getFunc());
                break;
            }
            case "getString": {
                VirtualReg reg = new VirtualReg("getString");
                CALL call = new CALL(nowBB, root.builtinFuncs.get("getString"), new ArrayList<>(), reg);
                nowBB.addInst(call);
                fce.value = reg;
                break;
            }
            case "getInt": {
                VirtualReg reg = new VirtualReg("getInt");
                CALL call = new CALL(nowBB, root.builtinFuncs.get("getInt"), new ArrayList<>(), reg);
                nowBB.addInst(call);
                fce.value = reg;
                break;
            }
            case "toString": {
                VirtualReg reg = new VirtualReg("toString");
                List<Reg> args = new ArrayList<>();
                fce.args.get(0).accept(this);
                args.add(fce.args.get(0).value);
                CALL call = new CALL(nowBB, root.builtinFuncs.get("toString"), args, reg);
                nowBB.addInst(call);
                fce.value = reg;
                break;
            }
            case "length":
            case "size": {
                VirtualReg reg = new VirtualReg("length");
                nowBB.addInst(new LOAD(nowBB, reg, REG_SIZE, thisExp.value, 0));
                fce.value = reg;
                break;
            }
            case "parseInt": {
                VirtualReg reg = new VirtualReg("parseInt");
                List<Reg> args = new ArrayList<>();
                args.add(thisExp.value);
                CALL call = new CALL(nowBB, root.builtinFuncs.get("parseInt"), args, reg);
                nowBB.addInst(call);
                fce.value = reg;
                break;
            }
            case "ord": {
//                VirtualReg reg = new VirtualReg("ord");
//                List<Reg> args = new ArrayList<>();
//                fce.args.get(0).accept(this);
//                args.add(thisExp.value);;
//                args.add(fce.args.get(0).value);
//                CALL call = new CALL(nowBB, root.builtinFuncs.get("ord"), args, reg);
//                nowBB.addInst(call);
//                fce.value = reg;
//                break;

                // an optimization trick
                VirtualReg reg = new VirtualReg("ord");
                fce.args.get(0).accept(this);
                BINOP binop = new BINOP(nowBB, BINOP.OP.ADD, thisExp.value, fce.args.get(0).value, reg);
                LOAD load = new LOAD(nowBB, reg, 1, reg, 4);
                nowBB.addInst(binop);
                nowBB.addInst(load);
                fce.value = reg;
                break;
            }
            case "subString": {
                VirtualReg reg = new VirtualReg("subString");
                List<Reg> args = new ArrayList<>();
                fce.args.get(0).accept(this);
                fce.args.get(1).accept(this);
                args.add(thisExp.value);
                args.add(fce.args.get(0).value);
                args.add(fce.args.get(1).value);
                CALL call = new CALL(nowBB, root.builtinFuncs.get("subString"), args, reg);
                nowBB.addInst(call);
                fce.value = reg;
                break;
            }
            default:
                return false;
        }
        needAddr = tmpNeedAddr;
        return true;
    }

    private void processIncDec(Expr expr, Expr side, boolean isInc, boolean isSuf) {
        boolean needMem = isMemoryAccess(expr);

        // get addr
        boolean tmpNeedAddr = needAddr;
        needAddr = needMem;
        expr.accept(this);
        Reg addr = expr.addr;
        int offset = expr.offset;

        // get value
        needAddr = false;
        expr.accept(this);
        needMem = tmpNeedAddr;

        BINOP.OP op = (isInc ? BINOP.OP.ADD : BINOP.OP.SUB);
        CONST one = new CONST(1);
        VirtualReg reg;
        if (isSuf) {
            // suffixInc and suffixDec return the old value
            reg = new VirtualReg(null);
            nowBB.addInst(new MOVE(nowBB, reg, expr.value));
            side.value = reg;
        }
        else {
            // prefixInc and prefixDec return the new value
            side.value = expr.value;
        }

        if (needMem) {
            // use temporary register
            reg = new VirtualReg(null);
            nowBB.addInst(new BINOP(nowBB, op, expr.value, one, reg));
            nowBB.addInst(new STORE(nowBB, reg, expr.rtype.type.getSize(), addr, offset));
            if (!isSuf) {
                side.value = reg;
            }
        }
        else {
            // direct add
            nowBB.addInst(new BINOP(nowBB, op, expr.value, one, (CommonReg) expr.value));
        }
    }

    private void processNewArray(NewExpr ne, int index, VirtualReg result, Reg tmpAddr) {
        Type type = ne.type.type;
        VirtualReg reg = new VirtualReg(null);

        Expr dim = ne.dimExpr.get(index);
        boolean tmpNeedAddr = needAddr;
        needAddr = false;
        dim.accept(this);
        needAddr = tmpNeedAddr;

        ARRAY n = (ARRAY)type;
        // calculate address
        nowBB.addInst(new BINOP(nowBB, BINOP.OP.MUL, dim.value, new CONST(n.element.getSize()), reg));
        nowBB.addInst(new BINOP(nowBB, BINOP.OP.ADD, reg, new CONST((new INT()).getSize()), reg));
        // allocate memory
        nowBB.addInst(new ALLOC(nowBB, reg, reg));
        nowBB.addInst(new STORE(nowBB, dim.value, (new INT()).getSize(), reg, 0));

        // more dimensions
        boolean isLastDim = (index == ne.dimExpr.size() - 1);
        if (!isLastDim) {
            // create a while loop
            VirtualReg loop = new VirtualReg(null);
            nowBB.addInst(new MOVE(nowBB, loop, new CONST(0)));
            VirtualReg addr = new VirtualReg(null);
            nowBB.addInst(new MOVE(nowBB, addr, reg));
            BasicBlock testBB = new BasicBlock(nowFunc, "while_cond");
            BasicBlock bodyBB = new BasicBlock(nowFunc, "while_body");
            BasicBlock finalBB = new BasicBlock(nowFunc, "while_final");
            nowBB.addJumpInst(new JUMP(nowBB, testBB));

            // test
            nowBB = testBB;
            VirtualReg cmp = new VirtualReg(null);
            nowBB.addInst(new CMP(nowBB, CMP.OP.LES, loop, dim.value, cmp));
            nowBB.addJumpInst(new CJUMP(nowBB, cmp, bodyBB, finalBB));

            // body
            nowBB = bodyBB;
            nowBB.addInst(new BINOP(nowBB, BINOP.OP.ADD, addr, new CONST((new INT()).getSize()), addr));
            processNewArray(ne, index + 1, null, addr);
            nowBB.addInst(new BINOP(nowBB, BINOP.OP.ADD, loop, new CONST(1), loop));
            nowBB.addJumpInst(new JUMP(nowBB, testBB));

            nowBB = finalBB;
        }
        if (index != 0) {
            // store the result in tmpAddr
            nowBB.addInst(new STORE(nowBB, reg, REG_SIZE, tmpAddr, 0));
        }
        else {
            // move the result to result register
            nowBB.addInst(new MOVE(nowBB, result, reg));
        }
    }

    private void processStringBinary(BinaryExpr be) {
        be.lhs.accept(this);
        be.rhs.accept(this);
        VirtualReg reg = new VirtualReg(null);
        be.value = reg;
        CALL call = null;
        List<Reg> args = new ArrayList<>();
        args.add(be.lhs.value);
        args.add(be.rhs.value);
        switch (be.op) {
            case ADD:
                call = new CALL(nowBB, root.builtinFuncs.get("str_concat"), args, reg);
                break;
            case LES:
                call = new CALL(nowBB, root.builtinFuncs.get("str_less"), args, reg);
                break;
            case GRT:
                args.clear();
                args.add(be.rhs.value);
                args.add(be.lhs.value);
                call = new CALL(nowBB, root.builtinFuncs.get("str_less"), args, reg);
                break;
            case LTE:
                call = new CALL(nowBB, root.builtinFuncs.get("str_lte"), args, reg);
                break;
            case GTE:
                args.clear();
                args.add(be.rhs.value);
                args.add(be.lhs.value);
                call = new CALL(nowBB, root.builtinFuncs.get("str_lte"), args, reg);
                break;
            case EQL:
                call = new CALL(nowBB, root.builtinFuncs.get("str_equal"), args, reg);
                break;
            case NEQ:
                call = new CALL(nowBB, root.builtinFuncs.get("str_not_equal"), args, reg);
                break;
        }
        nowBB.addInst(call);

        if (be.trueBB != null) {
            nowBB.addInst(new CJUMP(nowBB, reg, be.trueBB, be.falseBB));
        }
        else {
            be.value = reg;
        }
    }

    private void swap(Reg x, Reg y) {
        Reg tmp = x;
        x = y;
        y = tmp;
    }

    private void processIntBinary(BinaryExpr be) {
        be.lhs.accept(this);
        be.rhs.accept(this);
        BINOP.OP bop = null;
        CMP.OP cop = null;
        VirtualReg reg = new VirtualReg(null);
        Integer li = (be.lhs.value instanceof CONST ? ((CONST)be.lhs.value).getVal() : null);
        Integer ri = (be.rhs.value instanceof CONST ? ((CONST)be.rhs.value).getVal() : null);
        boolean consts = (li != null && ri != null);

        switch (be.op) {
            case MUL:
                if (consts) {
                    be.value = new CONST(li * ri);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    Integer tmp = li;
                    li = ri;
                    ri = tmp;
                }
                if (ri != null) {
                    if (ri == 1) {
                        be.value = be.lhs.value;
                        return;
                    }
                    else if (ri == 0) {
                        be.value = new CONST(0);
                        return;
                    }
                    else if (ri == 2) {
                        bop = BINOP.OP.SLA;
                        be.value = reg;
                        nowBB.addInst(new BINOP(nowBB, bop, be.lhs.value, new CONST(1), reg));
                        return;
                    }
                }
                bop = BINOP.OP.MUL;
                break;
            case DIV:
                if (consts) {
                    be.value = new CONST(li / ri);
                    return;
                }
                if (ri != null) {
                    if (ri == 1) {
                        be.value = be.lhs.value;
                        return;
                    }
                    else if (ri == 2) {
                        bop = BINOP.OP.SRA;
                        be.value = reg;
                        nowBB.addInst(new BINOP(nowBB, bop, be.lhs.value, new CONST(1), reg));
                        return;
                    }
                }
                bop = BINOP.OP.DIV;
                root.canUseRBX = false;
                break;
            case MOD:
                if (consts) {
                    be.value = new CONST(li % ri);
                    return;
                }
                if (ri != null && ri == 1) {
                    be.value = new CONST(0);
                    return;
                }
                bop = BINOP.OP.MOD;
                root.canUseRBX = false;
                break;
            case ADD:
                if (consts) {
                    be.value = new CONST(li + ri);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    Integer tmp = li;
                    li = ri;
                    ri = tmp;
                }
                if (ri != null && ri == 0) {
                    be.value = be.lhs.value;
                    return;
                }
                bop = BINOP.OP.ADD;
                break;
            case SUB:
                if (consts) {
                    be.value = new CONST(li - ri);
                    return;
                }
                if (li != null && li == 0) {
                    be.value = reg;
                    nowBB.addInst(new UNOP(nowBB, UNOP.OP.NEG, be.rhs.value, reg));
                    return;
                }
                if (ri != null && ri == 0) {
                    be.value = be.lhs.value;
                    return;
                }
                bop = BINOP.OP.SUB;
                break;
            case SLA:
                if (consts) {
                    be.value = new CONST(li << ri);
                    return;
                }
                if (ri != null && ri == 0) {
                    be.value = be.lhs.value;
                    return;
                }
                bop = BINOP.OP.SLA;
                root.canUseRBX = false;
                break;
            case SRA:
                if (consts) {
                    be.value = new CONST(li >> ri);
                    return;
                }
                if (ri != null && ri == 0) {
                    be.value = be.lhs.value;
                    return;
                }
                bop = BINOP.OP.SRA;
                root.canUseRBX = false;
                break;
            case LES:
                if (consts) {
                    be.value = new CONST(li < ri ? 1 : 0);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    cop = CMP.OP.GRT;
                }
                else {
                    cop = CMP.OP.LES;
                }
                break;
            case GRT:
                if (consts) {
                    be.value = new CONST(li > ri ? 1 : 0);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    cop = CMP.OP.LES;
                }
                else {
                    cop = CMP.OP.GRT;
                }
                break;
            case LTE:
                if (consts) {
                    be.value = new CONST(li <= ri ? 1 : 0);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    cop = CMP.OP.GTE;
                }
                else {
                    cop = CMP.OP.LTE;
                }
                break;
            case GTE:
                if (consts) {
                    be.value = new CONST(li >= ri ? 1 : 0);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    cop = CMP.OP.LTE;
                }
                else {
                    cop = CMP.OP.GTE;
                }
                break;
            case EQL:
                if (consts) {
                    be.value = new CONST(li.equals(ri) ? 1 : 0);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                }
                cop = CMP.OP.EQL;
                break;
            case NEQ:
                if (consts) {
                    be.value = new CONST(li.equals(ri) ? 0 : 1);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                }
                cop = CMP.OP.NEQ;
                break;
            case BAND:
                if (consts) {
                    be.value = new CONST(li & ri);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    Integer tmp = li;
                    li = ri;
                    ri = tmp;
                }
                bop = BINOP.OP.BAND;
                break;
            case BXOR:
                if (consts) {
                    be.value = new CONST(li ^ ri);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    Integer tmp = li;
                    li = ri;
                    ri = tmp;
                }
                bop = BINOP.OP.BXOR;
                break;
            case BOR:
                if (consts) {
                    be.value = new CONST(li | ri);
                    return;
                }
                if (li != null) {
                    swap(be.lhs.value, be.rhs.value);
                    Integer tmp = li;
                    li = ri;
                    ri = tmp;
                }
                bop = BINOP.OP.BOR;
                break;
        }

        if (bop != null) {
            be.value = reg;
            nowBB.addInst(new BINOP(nowBB, bop, be.lhs.value, be.rhs.value, reg));
        }
        else {
            nowBB.addInst(new CMP(nowBB, cop, be.lhs.value, be.rhs.value, reg));
            if (be.trueBB != null) {
                nowBB.addJumpInst(new CJUMP(nowBB, reg, be.trueBB, be.falseBB));
            }
            else {
                be.value = reg;
            }
        }
    }

    private void processLogicalBinary(BinaryExpr be) {
        // must do short circuit
        // lhs
        if (be.op == BinaryExpr.OP.LAND) {
            be.lhs.trueBB = new BasicBlock(nowFunc, "lhs_if_true");
            be.lhs.falseBB = be.falseBB;
            be.lhs.accept(this);
            nowBB = be.lhs.trueBB;
        }
        else {
            be.lhs.trueBB = be.trueBB;
            be.lhs.falseBB = new BasicBlock(nowFunc, "lhs_if_false");
            be.lhs.accept(this);
            nowBB = be.lhs.falseBB;
        }

        // rhs
        be.rhs.trueBB = be.trueBB;
        be.rhs.falseBB = be.falseBB;
        be.rhs.accept(this);
    }

    private void processAssign(BinaryExpr be) {
        // calculate rhs
        if (isCondExpr(be.rhs)) {
            be.rhs.trueBB = new BasicBlock(nowFunc, null);
            be.rhs.falseBB = new BasicBlock(nowFunc, null);
        }
        be.rhs.accept(this);

        // calculate address
        boolean needMem = isMemoryAccess(be.lhs);
        needAddr = needMem;
        be.lhs.accept(this);
        needAddr = false;

        // assign
        Reg addr = needMem ? be.lhs.addr : be.lhs.value;
        int offset = needMem ? be.lhs.offset : 0;
        assign(be.rhs.rtype.type.getSize(), addr, offset, be.rhs, needMem);
        be.value = be.rhs.value;
    }
}
