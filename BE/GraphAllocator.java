// 2019-05-09

package BE;

import Err.SomeError;
import IR.*;
import Types.Type;

import java.util.*;

public class GraphAllocator {
    private class VirtualRegInfo {
        int degree = 0;
        boolean isDeleted = false;
        CommonReg colour = null;
        Set<VirtualReg> neighbour = new HashSet<>();
        Set<VirtualReg> suggestSame = new HashSet<>();
    }

    private IRRoot root;
    private List<PhysicalReg> availiableRegs;
    private PhysicalReg tmp1;
    private PhysicalReg tmp2;
    private int maxColour;
    private Map<VirtualReg, VirtualRegInfo> vrInfo = new HashMap<>();
    private Set<VirtualReg> nodeSet = new HashSet<>();
    private Set<VirtualReg> smallNode = new HashSet<>();
    private Stack<VirtualReg> vrStack = new Stack<>();
    private Set<PhysicalReg> colourUsed = new HashSet<>();

    public GraphAllocator(IRRoot r) {
        root = r;
        availiableRegs = new ArrayList<>();
        for (PhysicalReg pr : NASMRegSet.allRegs) {
            if (pr.isGeneral()) {
                availiableRegs.add(pr);
            }
        }
        int maxArgNum = 0;
        for (Function f : root.funcs.values()) {
            maxArgNum = Math.max(maxArgNum, f.args.size());
        }
        if (maxArgNum >= 5) {
            availiableRegs.remove(NASMRegSet.R8);
        }
        if (maxArgNum >= 6) {
            availiableRegs.remove(NASMRegSet.R9);
        }
        if (root.canUseRBX) {
            tmp1 = NASMRegSet.RBX;
            tmp2 = availiableRegs.get(0);
        }
        else {
            tmp1 = availiableRegs.get(0);
            tmp2 = availiableRegs.get(1);
        }
        root.pr1 = tmp1;
        root.pr2 = tmp2;
        availiableRegs.remove(tmp1);
        availiableRegs.remove(tmp2);
        maxColour = availiableRegs.size();
    }

    private VirtualRegInfo getVRInfo(VirtualReg vr) {
        VirtualRegInfo vri = vrInfo.get(vr);
        if (vri == null) {
            vri = new VirtualRegInfo();
            vrInfo.put(vr, vri);
        }
        return vri;
    }

    private void addEdge(VirtualReg x, VirtualReg y) {
        getVRInfo(x).neighbour.add(y);
        getVRInfo(y).neighbour.add(x);
    }

    private void removeNode(VirtualReg vr) {
        VirtualRegInfo vri = getVRInfo(vr);
        vri.isDeleted = true;
        for (VirtualReg n : vri.neighbour) {
            VirtualRegInfo ni = getVRInfo(n);
            if (!ni.isDeleted) {
                if (--ni.degree < maxColour) {
                    smallNode.add(n);
                }
            }
        }
        nodeSet.remove(vr);
        vrStack.push(vr);
    }

    private void buildGraph(Function f) {
        List<VirtualReg> args = f.args;
        for (VirtualReg vr : args) {
            getVRInfo(vr);
        }
        for (BasicBlock b : f.getrPostOrder()) {
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                CommonReg d = i.getDefinedReg();
                if (!(d instanceof VirtualReg)) {
                    continue;
                }
                VirtualRegInfo vri = getVRInfo((VirtualReg)d);
                if (i instanceof MOVE) {
                    Reg src = ((MOVE)i).getSrc();
                    if (src instanceof VirtualReg) {
                        vri.suggestSame.add((VirtualReg)src);
                        getVRInfo((VirtualReg)src).suggestSame.add((VirtualReg)d);
                    }
                    for (VirtualReg vr : i.liveOut) {
                        if (vr == src || vr == d) {
                            continue;
                        }
                        addEdge(vr, (VirtualReg)d);
                    }
                }
                else {
                    for (VirtualReg vr : i.liveOut) {
                        if (vr == d) {
                            continue;
                        }
                        addEdge(vr, (VirtualReg)d);
                    }
                }
            }
        }
        for (VirtualRegInfo vri : vrInfo.values()) {
            vri.degree = vri.neighbour.size();
        }
    }

    private void colourize(Function f) {
        while (!nodeSet.isEmpty()) {
            // remove nodes whose degrees are less than maxColour
            while (!smallNode.isEmpty()) {
                Iterator<VirtualReg> ivr = smallNode.iterator();
                VirtualReg n = ivr.next();
                ivr.remove();
                removeNode(n);
            }
            if (nodeSet.isEmpty()) {
                break;
            }
            // then all nodes' degree are greater than maxColour
            Iterator<VirtualReg> ivr = nodeSet.iterator();
            VirtualReg n = ivr.next();
            ivr.remove();
            removeNode(n);
        }

        // colourize nodes in stack order
        while (!vrStack.empty()) {
            VirtualReg vr = vrStack.pop();
            VirtualRegInfo vri = getVRInfo(vr);
            colourUsed.clear();
            for (VirtualReg n : vri.neighbour) {
                VirtualRegInfo ni = getVRInfo(n);
                if (!ni.isDeleted && ni.colour instanceof PhysicalReg) {
                    colourUsed.add((PhysicalReg)ni.colour);
                }
            }
            PhysicalReg preg = vr.preg;
            if (preg == null) {
                // suggest same colour
                for (VirtualReg v : vri.suggestSame) {
                    CommonReg reg = getVRInfo(v).colour;
                    if (reg instanceof PhysicalReg && !colourUsed.contains((PhysicalReg)reg)) {
                        vri.colour = reg;
                        break;
                    }
                }
                if (vri.colour == null) {
                    // arbitrarily select a physical register as colour
                    for (PhysicalReg pr : availiableRegs) {
                        if (!colourUsed.contains(pr)) {
                            vri.colour = pr;
                            break;
                        }
                    }
                    // failed to colourize
                    // spill it
                    if (vri.colour == null) {
                        vri.colour = f.argSlots.get(vr);
                        if (vri.colour == null) {
                            vri.colour = new StackSlot(f, vr.name);
                        }
                    }
                }
            }
            else {
                vri.colour = preg;
            }
            vri.isDeleted = false;
        }
    }

    private void updateInstruction(Function f) {
        for (BasicBlock b : f.getrPostOrder()) {
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                List<CommonReg> used = i.getUsedReg();
                if (i instanceof CALL) {
                    List<Reg> args = ((CALL)i).getArgs();
                    for (int j = 0; j < args.size(); ++j) {
                        Reg a = args.get(j);
                        if (a instanceof VirtualReg) {
                            args.set(j, vrInfo.get((VirtualReg)a).colour);
                        }
                    }
                }
                else if (!used.isEmpty()) {
                    boolean tmp1Used = false;
                    Map<CommonReg, CommonReg> rename = new HashMap<>();
                    for (CommonReg reg : used) {
                        rename.put(reg, reg);
                    }
                    for (CommonReg reg : used) {
                        if (reg instanceof VirtualReg) {
                            CommonReg c = vrInfo.get((VirtualReg) reg).colour;
                            // can be directly used
                            if (c instanceof PhysicalReg) {
                                rename.put(reg, c);
                                f.pregs.add((PhysicalReg) c);
                            }
                            // load before use
                            else {
                                if (!(c instanceof StackSlot)) {
                                    throw new SomeError("In GraphAllocator: unexpected colour type");
                                }
                                PhysicalReg pr;
                                if (tmp1Used) {
                                    pr = tmp2;
                                } else {
                                    pr = tmp1;
                                    tmp1Used = true;
                                }
                                i.insertSucc(new LOAD(b, pr, Type.POINTER_SIZE, c, 0));
                                rename.put(reg, pr);
                                f.pregs.add(pr);
                            }
                        }
                    }
                    i.renameUsedReg(rename);
                }

                CommonReg d = i.getDefinedReg();
                if (d instanceof VirtualReg) {
                    CommonReg c = vrInfo.get((VirtualReg)d).colour;
                    // directly substitute
                    if (c instanceof PhysicalReg) {
                        i.setDefinedReg(c);
                        f.pregs.add((PhysicalReg)c);
                    }
                    // store after use
                    else {
                        i.setDefinedReg(tmp1);
                        i.insertSucc(new STORE(b, tmp1, Type.POINTER_SIZE, c, 0));
                        f.pregs.add(tmp1);
                        // skip the new STORE instruction
                        i = i.getSucc();
                    }
                }
            }
        }
    }

    public void run() {
        for (Function f : root.funcs.values()) {
            vrInfo.clear();
            nodeSet.clear();
            smallNode.clear();
            buildGraph(f);
            for (VirtualReg vr : vrInfo.keySet()) {
                nodeSet.add(vr);
                if (getVRInfo(vr).neighbour.size() < maxColour) {
                    smallNode.add(vr);
                }
            }
            colourize(f);
            updateInstruction(f);
        }
    }
}
