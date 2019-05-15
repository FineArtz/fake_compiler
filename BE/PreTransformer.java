// 2019-05-09

package BE;

import IR.*;
import Types.Type;

import java.util.*;

public class PreTransformer {
    private IRRoot root;
    private Function f;

    private static final int MAX_INST = 40;
    private static final int MAX_CALLER_INST = 65536;

    public PreTransformer(IRRoot r) {
        root = r;
    }

    private class FuncInfo {
        Map<StaticData, VirtualReg> staticReg = new HashMap<>();
        Set<StaticData> ruseStatic = new HashSet<>();
        Set<StaticData> rdefStatic = new HashSet<>();
        Set<StaticData> writtenStatic = new HashSet<>();
        int instCnt = 0;
        int calledCnt = 0;
        boolean isSelfR = false;
    }

    private Map<Function, FuncInfo> funcInfo = new HashMap<>();

    private VirtualReg getVirtualReg(StaticData sd, Map<StaticData, VirtualReg> map) {
        VirtualReg vr = map.get(sd);
        if (vr == null) {
            vr = new VirtualReg(sd.getName());
            map.put(sd, vr);
        }
        return vr;
    }

    private void countInst() {
        for (Function f : root.funcs.values()) {
            FuncInfo fi = funcInfo.get(f);
            for (BasicBlock b : f.getrPostOrder()) {
                for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                    ++fi.instCnt;
                    if (i instanceof CALL) {
                        FuncInfo calleeI = funcInfo.get(((CALL)i).getFunc());
                        if (calleeI != null && !((CALL)i).getFunc().isBuiltIn) {
                            ++calleeI.calledCnt;
                        }
                    }
                }
            }
        }
    }

    private void dataInFunction() {
        //FuncInfo fi = new FuncInfo();
        //funcInfo.put(f, fi);
        FuncInfo fi = funcInfo.get(f);
        Map<CommonReg, CommonReg> rename = new HashMap<>();

        for (BasicBlock b : f.getrPostOrder()) {
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                if (i instanceof LOAD && ((LOAD)i).isStatic()
                        || i instanceof STORE && ((STORE)i).isStatic()) {
                    continue;
                }
                List<CommonReg> used = i.getUsedReg();
                if (!used.isEmpty()) {
                    rename.clear();
                    for (CommonReg r : used) {
                        rename.put(r, r);
                    }
                    for (CommonReg r : used) {
                        if (r instanceof StaticData && !(r instanceof StaticString)) {
                            rename.put(r, getVirtualReg((StaticData)r, fi.staticReg));
                        }
                    }
                    i.renameUsedReg(rename);
                }
                CommonReg d = i.getDefinedReg();
                if (d instanceof StaticData) {
                    VirtualReg vr = getVirtualReg((StaticData)d, fi.staticReg);
                    i.setDefinedReg(vr);
                    fi.writtenStatic.add((StaticData)d);
                }
            }
        }

        // load static data at entry
        BasicBlock b = f.getHead();
        Inst i = b.getHead();
        for (Map.Entry<StaticData, VirtualReg> e : fi.staticReg.entrySet()) {
            e.getValue().isGlobal = true;
            i.insertPred(new LOAD(b, e.getValue(), Type.POINTER_SIZE, e.getKey(), e.getKey() instanceof StaticString));
        }
    }

    private void putBuiltinFunc() {
        for (Function f : root.builtinFuncs.values()) {
            funcInfo.put(f, new FuncInfo());
        }
    }

    private void calcrUse() {
        FuncInfo fi = funcInfo.get(f);
        fi.ruseStatic.addAll(fi.staticReg.keySet());
        fi.rdefStatic.addAll(fi.writtenStatic);
        for (Function rc : f.rcallee) {
            fi.ruseStatic.addAll(funcInfo.get(rc).staticReg.keySet());
            fi.rdefStatic.addAll(funcInfo.get(rc).writtenStatic);
        }
    }

    private void copyReg(Reg r, Map<Object, Object> map) {
        if (!map.containsKey(r)) {
            map.put(r, r.copy());
        }
    }

    private void argsInFunction(Function f) {
        BasicBlock b = f.getHead();
        Inst i = b.getHead();
        Map<CommonReg, CommonReg> argsToTmp = new HashMap<>();
        for (int j = 0; j < Math.min(f.args.size(), 6); ++j) {
            f.args.get(j).preg = NASMRegSet.paramRegs.get(j);
            VirtualReg tmpvr = new VirtualReg("arg_" + j);
            argsToTmp.put(f.args.get(j), tmpvr);
        }
        /*for (BasicBlock bb : f.getrPostOrder()) {
            for (Inst ii = bb.getHead(); ii != null; ii = ii.getSucc()) {
                Map<CommonReg, CommonReg> rename = new HashMap<>(argsToTmp);
                List<CommonReg> used = ii.getUsedReg();
                for (CommonReg reg : used) {
                    if (!rename.containsKey(reg)) {
                        rename.put(reg, reg);
                    }
                }
                ii.renameUsedReg(rename);

                CommonReg d = ii.getDefinedReg();
                if (rename.containsKey(d)) {
                    ii.setDefinedReg(rename.get(d));
                }
            }
        }*/
        for (int j = 0; j < Math.min(f.args.size(), 6); ++j) {
            i.insertPred(new MOVE(b, argsToTmp.get(f.args.get(j)), f.args.get(j)));
        }
        for (int j = 6; j < f.args.size(); ++j) {
            VirtualReg vr = f.args.get(j);
            StackSlot sl = new StackSlot(f, "arg_" + i, true);
            f.argSlots.put(vr, sl);
            i.insertPred(new LOAD(b, vr, Type.POINTER_SIZE, sl, 0));
        }
    }

    private void binopInFunction(Function f) {
        for (BasicBlock b : f.getrPostOrder()) {
            Inst succ = null;
            for (Inst i = b.getHead(); i != null; i = succ) {
                succ = i.getSucc();
                if (i instanceof BINOP) {
                    BINOP binop = (BINOP)i;
                    if (binop.getLhs() == binop.getDest()) {
                        continue;
                    }
                    if (binop.getRhs() == binop.getDest()) {
                        switch (binop.getOp()) {
                            case MUL:
                            case ADD:
                            case BAND:
                            case BXOR:
                            case BOR:
                                binop.setRhs(binop.getLhs());
                                binop.setLhs(binop.getDest());
                                break;
                            default:
                                // swap
                                VirtualReg vr = new VirtualReg("rhs_tmp");
                                binop.insertPred(new MOVE(b, vr, binop.getRhs()));
                                binop.insertPred(new MOVE(b, binop.getDest(), binop.getLhs()));
                                binop.setLhs(binop.getDest());
                                binop.setRhs(vr);
                        }
                    }
                    else if (!(binop.getOp() == BINOP.OP.DIV || binop.getOp() == BINOP.OP.MOD)) {
                        binop.insertPred(new MOVE(b, binop.getDest(), binop.getLhs()));
                        binop.setLhs(binop.getDest());
                    }
                }
            }
        }
    }

    private Inst callInFunction(CALL c) {
        Function caller = c.getBB().getParent();
        Function callee = c.getFunc();
        Map<Object, Object> rename = new HashMap<>();
        List<BasicBlock> blocks = callee.getrPostOrder();

        BasicBlock exitBB = callee.getTail();
        BasicBlock newExitBB = new BasicBlock(caller, exitBB.getName());
        rename.put(exitBB, newExitBB);
        rename.put(callee.getHead(), c.getBB());
        if (caller.getTail() == c.getBB()) {
            caller.setTail(newExitBB);
        }

        // move instructions of caller
        Map<Object, Object> move = new HashMap<>();
        move.put(c.getBB(), newExitBB);
        for (Inst i = c.getSucc(); i != null; i = i.getSucc()) {
            if (i instanceof JumpInst) {
                newExitBB.addJumpInst(((JumpInst)i).copy(move));
                c.getBB().rmvJumpInst();
            }
            else {
                newExitBB.addInst(i.copy(move));
                i.remove();
            }
        }
        assert newExitBB.isEnd();
        Inst nef = newExitBB.getHead();

        // set arguments
        for (int i = 0; i < callee.args.size(); ++i) {
            VirtualReg a = callee.args.get(i);
            VirtualReg b = a.copy();
            c.insertPred(new MOVE(c.getBB(), b, c.getArgs().get(i)));
            rename.put(a, b);
        }

        c.remove();
        for (BasicBlock b : blocks) {
            if (!rename.containsKey(b)) {
                rename.put(b, new BasicBlock(caller, b.getName()));
            }
        }
        for (BasicBlock b : blocks) {
            BasicBlock nb = (BasicBlock)rename.get(b);
            for (IRRoot.ForStruct fs : root.fors) {
                if (fs.f == b) {
                    fs.f = nb;
                }
                if (fs.s == b) {
                    fs.s = nb;
                }
                if (fs.b == b) {
                    fs.b = nb;
                }
                if (fs.c == b) {
                    fs.b = nb;
                }
            }
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                for (Reg used : i.getUsedReg()) {
                    copyReg(used, rename);
                }
                CommonReg d = i.getDefinedReg();
                if (d != null) {
                    copyReg(d, rename);
                }
                if (newExitBB != nb) {
                    if (i instanceof JumpInst) {
                        if (!(i instanceof RETURN)) {
                            nb.addJumpInst(((JumpInst)i).copy(rename));
                        }
                    }
                    else {
                        nb.addInst(i.copy(rename));
                    }
                }
                else {
                    if (!(i instanceof RETURN)) {
                        nef.insertPred(i.copy(rename));
                    }
                }
            }
        }
        if (!c.getBB().isEnd()) {
            c.getBB().addJumpInst(new JUMP(c.getBB(), newExitBB));
        }

        // copy return
        RETURN ret = callee.returnList.get(0);
        if (ret.getRetVal() != null) {
            nef.insertPred(new MOVE(newExitBB, c.getDest(), (Reg)rename.get(ret.getRetVal())));
        }

        return newExitBB.getHead();
    }

    private void initialize() {
        for (Function f : root.funcs.values()) {
            funcInfo.put(f, new FuncInfo());
        }
    }

    private void transformStaticData() {
        for (Function ff : root.funcs.values()) {
            f = ff;
            dataInFunction();
        }
        putBuiltinFunc();
        for (Function ff : root.funcs.values()) {
            f = ff;
            calcrUse();
        }

        Set<StaticData> reload = new HashSet<>();
        for (Function f : root.funcs.values()) {
            FuncInfo fi = funcInfo.get(f);
            Set<StaticData> used = fi.staticReg.keySet();
            if (!used.isEmpty()) {
                for (BasicBlock b : f.getrPostOrder()) {
                    for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                        if (!(i instanceof CALL)) {
                            continue;
                        }
                        CALL call = (CALL)i;
                        Function ce = call.getFunc();
                        FuncInfo cei = funcInfo.get(ce);

                        // static data should be stored before function call
                        for (StaticData sd : fi.writtenStatic) {
                            if (!(sd instanceof StaticString) && cei.ruseStatic.contains(sd)) {
                                call.insertPred(new STORE(b, fi.staticReg.get(sd), Type.POINTER_SIZE, sd));
                            }
                        }

                        // and then reloaded after function call
                        if (cei.rdefStatic.isEmpty()) {
                            continue;
                        }
                        reload.clear();
                        reload.addAll(cei.rdefStatic);
                        reload.retainAll(used);
                        for (StaticData sd : reload) {
                            if (!(sd instanceof StaticString)) {
                                i.insertSucc(new LOAD(b, fi.staticReg.get(sd), Type.POINTER_SIZE, sd, false));
                            }
                        }
                    }
                }
            }
        }

        // write back static data at exit
        for (Function f : root.funcs.values()) {
            FuncInfo fi = funcInfo.get(f);
            RETURN ret = f.returnList.get(0);
            for (StaticData sd : fi.writtenStatic) {
                ret.insertPred(new STORE(ret.getBB(), fi.staticReg.get(sd), Type.POINTER_SIZE, sd));
            }
        }
    }

    private void transformArguments() {
        for (Function f : root.funcs.values()) {
            argsInFunction(f);
        }
    }

    private void transformBinop() {
        for (Function f : root.funcs.values()) {
            binopInFunction(f);
        }
    }

    private void transformInline() {
        for (Function f : root.funcs.values()) {
            FuncInfo fi = funcInfo.get(f);
            if (f.rcallee.contains(f)) {
                fi.isSelfR = true;
            }
        }
        countInst();
        List<BasicBlock> blocks = new ArrayList<>();
        List<String> useless = new ArrayList<>();
        boolean flag = true;
        while (flag) {
            flag = false;
            useless.clear();
            for (Function f : root.funcs.values()) {
                FuncInfo fi = funcInfo.get(f);
                blocks.clear();
                blocks.addAll(f.getrPostOrder());
                for (BasicBlock b : blocks) {
                    Inst succ = null;
                    for (Inst i = b.getHead(); i != null; i = succ) {
                        succ = i.getSucc();
                        if (i instanceof CALL) {
                            CALL call = (CALL)i;
                            Function callee = call.getFunc();
                            FuncInfo calleeI = funcInfo.get(callee);
                            if (callee.isBuiltIn || callee.getFunc().pname != null || calleeI.isSelfR) {
                                continue;
                            }
                            if (calleeI.instCnt <= MAX_INST && calleeI.instCnt + fi.instCnt <= MAX_CALLER_INST) {
                                succ = callInFunction(call);
                                flag = true;
                                fi.instCnt += calleeI.instCnt;
                                --calleeI.calledCnt;
                                if (calleeI.calledCnt == 0) {
                                    useless.add(callee.getName());
                                }
                            }
                        }
                    }
                    f.clearOrder();
                    f.getrPostOrder();
                }
            }
            for (String n : useless) {
                root.funcs.remove(n);
            }
        }
        for (Function f : root.funcs.values()) {
            f.updateCallee(root);
        }
        root.updateRCallee();
    }

    private void removeNullInit() {
        if (root.data.isEmpty()) {
            Function m = root.funcs.get("main");
            Inst i = m.getHead().getHead();
            while (!(i instanceof CALL && ((CALL)i).getFunc().getName().equals("__init__"))) {
                i = i.getSucc();
            }
            i.remove();
            root.funcs.remove("__init__");
        }
    }

    public void run() {
        removeNullInit();
        initialize();
        transformBinop();
        transformInline();
        transformStaticData();
        transformArguments();
    }
}
