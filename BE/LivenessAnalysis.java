// 2019-05-04

package BE;

import IR.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LivenessAnalysis {

    LivenessAnalysis() {}

    private void initBlock(BasicBlock b) {
        for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
            if (i.liveIn == null) {
                i.liveIn = new HashSet<>();
            }
            else {
                i.liveIn.clear();
            }
            if (i.liveOut == null) {
                i.liveOut = new HashSet<>();
            }
            else {
                i.liveOut.clear();
            }
        }
    }

    void analyseFunction(Function f) {
        List<BasicBlock> blocks = f.getrPrevOrder();
        for (BasicBlock b : blocks) {
            initBlock(b);
        }

        Set<VirtualReg> in = new HashSet<>();
        Set<VirtualReg> out = new HashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (BasicBlock b : blocks) {
                for (Inst i = b.getTail(); i != null; i = i.getPred()) {
                    in.clear();
                    out.clear();
                    if (i instanceof JUMP) {
                        out.addAll(((JUMP)i).getTarget().getHead().liveIn);
                    }
                    else if (i instanceof CJUMP) {
                        out.addAll(((CJUMP)i).getThenBB().getHead().liveIn);
                        out.addAll(((CJUMP)i).getElseBB().getHead().liveIn);
                    }
                    else if (!(i instanceof RETURN)) {
                        out.addAll(i.getSucc().liveIn);
                    }
                    in.addAll(out);
                    CommonReg definedReg = i.getDefinedReg();
                    if (definedReg instanceof VirtualReg) {
                        in.remove(definedReg);
                    }
                    for (CommonReg r : i.getUsedReg()) {
                        if (r instanceof VirtualReg) {
                            in.add((VirtualReg)r);
                        }
                    }
                    if (!(i.liveIn.equals(in) && i.liveOut.equals(out))) {
                        changed = true;
                        i.liveIn.clear();
                        i.liveOut.clear();
                        i.liveIn.addAll(in);
                        i.liveOut.addAll(out);
                    }
                }
            }
        }
    }
}
