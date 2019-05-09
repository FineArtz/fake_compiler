// 2019-05-07

package BE;

import IR.*;
import Types.Type;

import java.util.*;

public class NaiveAllocator {
    private IRRoot root;
    private List<PhysicalReg> availableRegs;

    public NaiveAllocator(IRRoot r) {
        root = r;
        availableRegs = new ArrayList<>();
        for (PhysicalReg pr : NASMRegSet.allRegs) {
            if (pr.isGeneral()) {
                availableRegs.add(pr);
            }
        }
        availableRegs.remove(NASMRegSet.R10);
        availableRegs.remove(NASMRegSet.R11);
    }

    private Map<VirtualReg, StackSlot> slotMap = new HashMap<>();

    private StackSlot getSlot(Function f, VirtualReg vr) {
        StackSlot slot = slotMap.get(vr);
        if (slot == null) {
            slot = new StackSlot(f, vr.name);
            slotMap.put(vr, slot);
        }
        return slot;
    }

    private void allocateFunction(Function f) {
        if (f.isBuiltIn) {
            return;
        }

    /*  This LRU Allocator has collapsed!

        Set<VirtualReg> regs = new HashSet<>(f.args);
        Map<VirtualReg, Integer> usedTimes = new HashMap<>();
        for (BasicBlock b : f.getrPostOrder()) {
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                for (CommonReg cr : i.getUsedReg()) {
                    if (cr instanceof VirtualReg) {
                        regs.add((VirtualReg)cr);
                        Integer cnt = usedTimes.get(cr);
                        if (cnt == null) {
                            cnt = 0;
                        }
                        ++cnt;
                        usedTimes.put((VirtualReg)cr, cnt);
                    }
                }
            }
        }
        for (VirtualReg vr : regs) {
            if (!usedTimes.containsKey(vr)) {
                usedTimes.put(vr, 0);
            }
        }
        List<Map.Entry<VirtualReg, Integer>> sortedMap = new ArrayList<>(usedTimes.entrySet());
        sortedMap.sort(Comparator.comparing((Map.Entry<VirtualReg, Integer> e) -> e.getValue()).reversed());
        List<VirtualReg> rank = new ArrayList<>();
        for (Map.Entry<VirtualReg, Integer> e : sortedMap) {
            rank.add(e.getKey());
        }

        List<PhysicalReg> pregs = new ArrayList<>(NASMRegSet.calleeSaveRegs);
        pregs.remove(NASMRegSet.RBP);
        f.pregs.add(NASMRegSet.RBP);
        int prCnt = 0;
        for (VirtualReg vr : rank) {
            if (vr.preg == null) {
                vr.preg = pregs.get(prCnt++);
                f.pregs.add(vr.preg);
                if (vr.preg.isGeneral()) {
                    f.gpregs.add(vr.preg);
                }
                if (prCnt == pregs.size()) {
                    break;
                }
            }
        }

        // more params are pushed from right to left
        int stackTop = (f.pregs.size() + 1) * Type.POINTER_SIZE;
        List<VirtualReg> args = f.args;
        for (int i = 6; i < args.size(); ++i) {
            StackSlot sl = new StackSlot(f, args.get(i).name);
            args.get(i).slot = sl;
            args.get(i).stackPos = -stackTop;
            stackTop += Type.POINTER_SIZE;
            f.slots.add(sl);
        }
        // local variables
        stackTop = Type.POINTER_SIZE;
        for (VirtualReg vr : rank) {
            if (vr.preg == null && vr.stackPos == 0) {
                stackTop = alignStack(stackTop + Type.POINTER_SIZE, Type.POINTER_SIZE);
                StackSlot sl = new StackSlot(f, vr.name);
                vr.slot = sl;
                vr.stackPos = -stackTop;
                f.slots.add(sl);
            }
        }
*/

        slotMap.clear();
        slotMap.putAll(f.argSlots);
        Map<CommonReg, CommonReg> rename = new HashMap<>();

        for (BasicBlock b : f.getrPostOrder()) {
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                int cnt = 0;
                if (i instanceof CALL) {
                    // CALL instructions are dealt in detail in Transformer
                    List<Reg> args = ((CALL)i).getArgs();
                    for (int j = 0; j < args.size(); ++j) {
                        Reg reg = args.get(j);
                        if (reg instanceof VirtualReg) {
                            CommonReg addr = getSlot(f, (VirtualReg)reg);
                            args.set(j, addr);
                        }
                    }
                }
                else {
                    List<CommonReg> regs = i.getUsedReg();
                    if (!regs.isEmpty()) {
                        rename.clear();
                        for (CommonReg reg : regs) {
                            rename.put(reg, reg);
                        }
                        for (CommonReg reg : regs) {
                            if (reg instanceof VirtualReg) {
                                PhysicalReg preg = ((VirtualReg)reg).preg;
                                if (preg == null) {
                                    preg = availableRegs.get(cnt++);
                                }
                                rename.put(reg, preg);
                                f.pregs.add(preg);
                                ((VirtualReg)reg).preg = preg;
                                CommonReg addr = getSlot(f, (VirtualReg)reg);
                                i.insertPred(new LOAD(b, preg, Type.POINTER_SIZE, addr, 0));
                            }
                        }
                        i.renameUsedReg(rename);
                    }
                }

                CommonReg defined = i.getDefinedReg();
                if (defined instanceof VirtualReg) {
                    PhysicalReg preg = ((VirtualReg)defined).preg;
                    if (preg == null) {
                        preg = availableRegs.get(cnt++);
                    }
                    f.pregs.add(preg);
                    i.setDefinedReg(preg);
                    CommonReg addr = getSlot(f, (VirtualReg)defined);
                    i.insertSucc(new STORE(b, preg, Type.POINTER_SIZE, addr, 0));
                    i = i.getSucc();
                }
            }
        }
    }

    private int alignStack(int sp, int align) {
        return (sp + align - 1) / align * align;
    }

    public void run() {
        for (Function f : root.funcs.values()) {
            allocateFunction(f);
        }
    }
}
