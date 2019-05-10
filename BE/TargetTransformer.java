// 2019-05-07

package BE;

import IR.*;
import Types.Type;
import Types.VOID;

import java.util.*;

public class TargetTransformer {
    private IRRoot root;

    public TargetTransformer(IRRoot r) {
        root = r;
    }

    private static class FuncInfo {
        List<PhysicalReg> usedCallerSaveReg = new ArrayList<>();
        List<PhysicalReg> usedCalleeSaveReg = new ArrayList<>();
        Set<PhysicalReg> rusedReg = new HashSet<>();
        Map<StackSlot, Integer> slotOffset = new HashMap<>();
        int totalReg = 0;
        int exargs = 0;
    }

    private Map<Function, FuncInfo> funcInfo = new HashMap<>();

    private void calcFrame(Function f) {
        FuncInfo fi = new FuncInfo();
        for (PhysicalReg pr : f.pregs) {
            if (pr.isCalleeSave()) {
                fi.usedCalleeSaveReg.add(pr);
            }
            if (pr.isCallerSave()) {
                fi.usedCallerSaveReg.add(pr);
            }
        }
        fi.usedCalleeSaveReg.add(NASMRegSet.RBP);
        //fi.usedCalleeSaveReg.add(NASMRegSet.RBX);
        for (int i = 0; i < f.slots.size(); ++i) {
            fi.slotOffset.put(f.slots.get(i), i * Type.POINTER_SIZE);
        }
        fi.totalReg = fi.usedCalleeSaveReg.size() + f.slots.size();
        fi.totalReg = fi.totalReg - fi.totalReg % 2 + 1;
        fi.exargs = Math.max(f.args.size() - 6, 0);
        int offset = (fi.totalReg + 1) * Type.POINTER_SIZE;
        for (int i = 6; i < f.args.size(); ++i) {
            fi.slotOffset.put(f.args.get(i).slot, offset);
            offset += Type.POINTER_SIZE;
        }
        funcInfo.put(f, fi);
    }

    private void putBuiltinFuncs() {
        for (Function f : root.builtinFuncs.values()) {
            funcInfo.put(f, new FuncInfo());
        }
    }

    private void calcrRegs() {
        for (Map.Entry<Function, FuncInfo> e : funcInfo.entrySet()) {
            Function f = e.getKey();
            FuncInfo fi = e.getValue();
            fi.rusedReg.addAll(f.pregs);
            for (Function ff : f.rcallee) {
                fi.rusedReg.addAll(ff.pregs);
            }
        }
    }

    private void transformEntry(Function f) {
        FuncInfo fi = funcInfo.get(f);
        BasicBlock entry = f.getHead();
        Inst inst = entry.getHead();

        // push callee-save registers
        /*if (!f.getName().equals("main")) {
            for (PhysicalReg pr : fi.usedCalleeSaveReg) {
                inst.insertPred(new PUSH(entry, pr));
            }
        }
        else {
            inst.insertPred(new PUSH(entry, NASMRegSet.RBP));
        }*/
        inst.insertPred(new PUSH(entry, NASMRegSet.RBP));
        // RBP = RSP
        inst.insertPred(new MOVE(entry, NASMRegSet.RBP, NASMRegSet.RSP));
        // RSP = RSP - offset
        int offset = Math.max(fi.totalReg - fi.usedCalleeSaveReg.size(), 0);
        offset += offset % 2;
        if (offset > 0) {
            inst.insertPred(new BINOP(entry, BINOP.OP.SUB, NASMRegSet.RSP, new CONST(offset * Type.POINTER_SIZE), NASMRegSet.RSP));
        }
    }

    private void rmvSelfMove(Inst i) {
        if (i instanceof MOVE) {
            if (((MOVE)i).getDest() == ((MOVE)i).getSrc()) {
                i.remove();
            }
        }
    }

    private void transformSlot(FuncInfo fi, Inst i) {
        if (i instanceof LOAD) {
            Reg reg = ((LOAD)i).getAddr();
            if (reg instanceof StackSlot || reg instanceof VirtualReg && ((VirtualReg)reg).slot != null) {
                ((LOAD)i).setAddr(NASMRegSet.RBP);
                ((LOAD)i).setOffset(fi.slotOffset.get((reg instanceof StackSlot ? (StackSlot)reg : ((VirtualReg)reg).slot)));
            }
        }
        else if (i instanceof STORE) {
            Reg reg = ((STORE)i).getAddr();
            if (reg instanceof StackSlot || reg instanceof VirtualReg && ((VirtualReg)reg).slot != null) {
                ((STORE)i).setAddr(NASMRegSet.RBP);
                ((STORE)i).setOffset(fi.slotOffset.get((reg instanceof StackSlot ? (StackSlot)reg : ((VirtualReg)reg).slot)));
            }
        }
    }

    private void transformCall(Function f) {
        FuncInfo fi = funcInfo.get(f);
        for (BasicBlock b : f.getrPostOrder()) {
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                if (i instanceof ALLOC) {
                    // call of c-function "malloc"

                    // push caller-save registers
                    int callerSave = 0;
                    for (PhysicalReg pr : fi.usedCallerSaveReg) {
                        ++callerSave;
                        i.insertPred(new PUSH(b, pr));
                    }

                    // the only argument of "malloc" is size
                    i.insertPred(new MOVE(b, NASMRegSet.RDI, ((ALLOC)i).getSize()));

                    // align RSP
                    if (callerSave % 2 == 1) {
                        i.insertPred(new PUSH(b, new CONST(0)));
                    }

                    // === CALL malloc

                    // return
                    i.insertSucc(new MOVE(b, ((ALLOC)i).getDest(), NASMRegSet.RAX));

                    // restore caller-save registers
                    for (PhysicalReg pr : fi.usedCallerSaveReg) {
                        i.insertSucc(new POP(b, pr));
                    }

                    // RSP = RSP + POINTER_SIZE if RSP was aligned
                    if (callerSave % 2 == 1) {
                        i.insertSucc(new BINOP(b, BINOP.OP.ADD, NASMRegSet.RSP, new CONST(Type.POINTER_SIZE), NASMRegSet.RSP));
                    }
                }
                else if (i instanceof CALL) {
                    Function callee = ((CALL) i).getFunc();
                    FuncInfo calleeI = funcInfo.get(callee);

                    // push caller-save registers
                    int callerSave = 0;
                    for (PhysicalReg pr : fi.usedCallerSaveReg) {
                        if (NASMRegSet.paramRegs.contains(pr) && NASMRegSet.paramRegs.indexOf(pr) < f.args.size()) {
                            continue;
                        }
                        if (calleeI.rusedReg.contains(pr) || callee.isBuiltIn || callee.hasALLOC) {
                            ++callerSave;
                            i.insertPred(new PUSH(b, pr));
                        }
                    }

                    // push argument registers
                    int argc = Math.min(f.args.size(), 6);
                    for (int j = 0; j < argc; ++j) {
                        i.insertPred(new PUSH(b, NASMRegSet.paramRegs.get(j)));
                    }
                    callerSave += argc;

                    // align rsp
                    if ((callerSave + fi.exargs) % 2 == 1) {
                        i.insertPred(new PUSH(b, new CONST(0)));
                    }

                    // push extra arguments
                    for (int j = f.args.size() - 1; j > 5; --j) {
                        Reg arg = ((CALL) i).getArgs().get(j);
                        if (arg instanceof StackSlot || arg instanceof VirtualReg && ((VirtualReg) arg).slot != null) {
                            i.insertPred(new LOAD(b, NASMRegSet.RAX, Type.POINTER_SIZE, NASMRegSet.RBP, fi.slotOffset.get((arg instanceof StackSlot ? (StackSlot)arg : ((VirtualReg)arg).slot))));
                            i.insertPred(new PUSH(b, NASMRegSet.RAX));
                        } else {
                            i.insertPred(new PUSH(b, arg));
                        }
                    }

                    // deal with the first 6 arguments
                    Map<PhysicalReg, Integer> pregOffset = new HashMap<>();
                    List<Integer> argsOffset = new ArrayList<>();
                    int offset = 0;
                    for (int j = 0; j < Math.min(((CALL) i).getArgs().size(), 6); ++j) {
                        Reg arg = ((CALL) i).getArgs().get(j);
                        if (arg instanceof VirtualReg
                                && ((VirtualReg) arg).preg != null
                                && NASMRegSet.paramRegs.contains(((VirtualReg) arg).preg)
                                && NASMRegSet.paramRegs.indexOf(((VirtualReg) arg).preg) < ((CALL) i).getArgs().size()) {
                            PhysicalReg preg = ((VirtualReg) arg).preg;
                            if (pregOffset.containsKey(preg)) {
                                argsOffset.add(pregOffset.get(preg));
                            } else {
                                pregOffset.put(preg, offset);
                                argsOffset.add(offset);
                                i.insertPred(new PUSH(b, preg));
                                ++offset;
                            }
                        } else {
                            argsOffset.add(-1);
                        }
                    }

                    for (int j = 0; j < Math.min(((CALL) i).getArgs().size(), 6); ++j) {
                        if (argsOffset.get(j) == -1) {
                            Reg arg = ((CALL) i).getArgs().get(j);
                            if (arg instanceof StackSlot || arg instanceof VirtualReg && ((VirtualReg) arg).slot != null) {
                                i.insertPred(new LOAD(b, NASMRegSet.RAX, Type.POINTER_SIZE, NASMRegSet.RBP, fi.slotOffset.get((arg instanceof StackSlot ? (StackSlot)arg : ((VirtualReg)arg).slot))));
                                i.insertPred(new MOVE(b, NASMRegSet.paramRegs.get(j), NASMRegSet.RAX));
                            } else {
                                i.insertPred(new MOVE(b, NASMRegSet.paramRegs.get(j), arg));
                            }
                        } else {
                            i.insertPred(new LOAD(b, NASMRegSet.paramRegs.get(j), Type.POINTER_SIZE, NASMRegSet.RSP, (offset - argsOffset.get(j) - 1) * Type.POINTER_SIZE));
                        }
                    }

                    // RSP = RSP + offset
                    if (offset > 0) {
                        i.insertPred(new BINOP(b, BINOP.OP.ADD, NASMRegSet.RSP, new CONST(offset * Type.POINTER_SIZE), NASMRegSet.RSP));
                    }

                    // === CALL is executed ===
                    // insertPred -> insertSucc

                    // return value is in RAX
                    if (((CALL) i).getDest() != null) {
                        i.insertSucc(new MOVE(b, ((CALL) i).getDest(), NASMRegSet.RAX));
                    }

                    // restore caller-save registers
                    for (PhysicalReg pr : fi.usedCallerSaveReg) {
                        if (NASMRegSet.paramRegs.contains(pr) && NASMRegSet.paramRegs.indexOf(pr) < f.args.size()) {
                            continue;
                        }
                        if (calleeI.rusedReg.contains(pr) || callee.isBuiltIn || callee.hasALLOC) {
                            i.insertSucc(new POP(b, pr));
                        }
                    }

                    // restore arguments
                    for (int j = 0; j < argc; ++j) {
                        i.insertSucc(new POP(b, NASMRegSet.paramRegs.get(j)));
                    }

                    // RSP = RSP + offset
                    if (fi.exargs > 0 || (callerSave + fi.exargs) % 2 == 1) {
                        int exargs = fi.exargs + (callerSave + fi.exargs) % 2;
                        i.insertSucc(new BINOP(b, BINOP.OP.ADD, NASMRegSet.RSP, new CONST(exargs * Type.POINTER_SIZE), NASMRegSet.RSP));
                    }
                }
                transformSlot(fi, i);
                rmvSelfMove(i);
            }
        }
    }

    private void transformReturn(Function f) {
        if (f.getFunc().type == null || f.getFunc().type instanceof VOID) {
            return;
        }
        // move return value to RAX
        for (RETURN r : f.returnList) {
            r.insertPred(new MOVE(r.getBB(), NASMRegSet.RAX, r.getRetVal()));
        }
    }

    private void transformExit(Function f) {
        FuncInfo fi = funcInfo.get(f);
        BasicBlock exit = f.getTail();
        Inst last = exit.getTail();

        /*// RSP = RSP + offset
        if (fi.totalReg > fi.usedCalleeSaveReg.size()) {
            last.insertPred(new BINOP(exit, BINOP.OP.ADD, NASMRegSet.RSP, new CONST((fi.totalReg - fi.usedCalleeSaveReg.size()) * Type.POINTER_SIZE), NASMRegSet.RSP));
        }*/

        // pop callee-save registers
        /*if (!f.getName().equals("main")) {
            for (int i = fi.usedCalleeSaveReg.size() - 1; i >= 0; --i) {
                if (fi.usedCalleeSaveReg.get(i) != NASMRegSet.RBP) {
                    last.insertPred(new POP(exit, fi.usedCalleeSaveReg.get(i)));
                }
            }
        }*/
    }

    public void run() {
        for (Function f : root.funcs.values()) {
            calcFrame(f);
        }
        putBuiltinFuncs();
        calcrRegs();
        for (Function f : root.funcs.values()) {
            transformEntry(f);
            transformCall(f);
            transformReturn(f);
            transformExit(f);
        }
    }
}
