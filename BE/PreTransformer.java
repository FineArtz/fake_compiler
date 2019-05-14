// 2019-05-09

package BE;

import IR.*;
import Types.Type;

import java.util.*;

public class PreTransformer {
    private IRRoot root;
    private Function f;

    public PreTransformer(IRRoot r) {
        root = r;
    }

    private class FuncInfo {
        Map<StaticData, VirtualReg> staticReg = new HashMap<>();
        Set<StaticData> ruseStatic = new HashSet<>();
        Set<StaticData> rdefStatic = new HashSet<>();
        Set<StaticData> writtenStatic = new HashSet<>();
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

    private void dataInFunction() {
        FuncInfo fi = new FuncInfo();
        funcInfo.put(f, fi);
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

    public void run() {
        transformBinop();
        transformStaticData();
        transformArguments();
    }
}
