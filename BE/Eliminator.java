// 2019-05-04

package BE;

import IR.*;

import java.util.*;

public class Eliminator {
    private IRRoot root;
    private LivenessAnalysis la;
    private boolean changed;
    private Map<BasicBlock, BasicBlock> jumpTarget = new HashMap<>();

    public Eliminator(IRRoot r) {
        root = r;
        la = new LivenessAnalysis();
    }

    private void eliminate(Function f) {
        List<BasicBlock> blocks = f.getrPrevOrder();
        for (BasicBlock b : blocks) {
            for (Inst i = b.getTail(); i != null; i = i.getPred()) {
                if (i instanceof ALLOC
                || i instanceof BINOP
                || i instanceof CMP
                || i instanceof LOAD
                || i instanceof MOVE
                || i instanceof UNOP) {
                    CommonReg d = i.getDefinedReg();
                    if (!i.liveOut.contains(d)) {
                        i.remove();
                        changed = true;
                    }
                }
            }
        }
    }

    // If a loop is meaningless,
    // change the first BasicBlock of the loop
    // to BasicBlock(JUMP(thisBB, loop_finalBB)).
    // Then it will be eliminated by BBEliminate.
    private void loopEliminate(Function f) {
        if (f.isBuiltIn) {
            return;
        }

        // get all loops in f
        List<BasicBlock> blocks = f.getrPrevOrder();
        List<Integer> stIndex = new ArrayList<>();
        List<Integer> edIndex = new ArrayList<>();
        Set<BasicBlock> visited = new HashSet<>();
        for (BasicBlock b : blocks) {
            visited.add(b);
            if (!(b.getName().startsWith("for") || b.getName().startsWith("while"))) {
                continue;
            }
            if (b.getTail() instanceof CJUMP) {
                CJUMP cj = (CJUMP)b.getTail();
                Integer st = 0;
                Integer ed;
                if (visited.contains(cj.getThenBB())) {
                    st = blocks.indexOf(cj.getThenBB());
                }
                else if (visited.contains(cj.getElseBB())) {
                    st = blocks.indexOf(cj.getElseBB());
                }
                if (st != 0 && (blocks.get(st).getName().startsWith("for") || blocks.get(st).getName().startsWith("while"))) {
                    ed = blocks.indexOf(b);
                    if (ed - st <= 1) {
                        stIndex.add(st);
                        edIndex.add(ed);
                    }
                }
            }
        }

        Set<VirtualReg> out;
        for (int i = 0; i < stIndex.size(); ++i) {
            Integer st = stIndex.get(i);
            Integer ed = edIndex.get(i);
            Integer fn = (blocks.get(ed).getName().startsWith("for") ? st - 2 : st - 1);
            out = blocks.get(fn).getHead().liveIn;
            boolean elm = true;
            for (Integer j = st; j <= ed; ++j) {
                for (Inst ii = blocks.get(j).getHead(); ii != null; ii = ii.getSucc()) {
                    if (ii instanceof RETURN
                    || ii instanceof CALL
                    || ii instanceof STORE) {
                        elm = false;
                        break;
                    }
                    if (ii.getDefinedReg() != null) {
                        if (out.contains(ii.getDefinedReg())) {
                            elm = false;
                            break;
                        }
                    }
                }
                if (!elm) {
                    break;
                }
            }
            if (elm) {
                BasicBlock now = blocks.get(ed);
                now.rmvJumpInst();
                now.setHead(null);
                now.setTail(null);
                now.clearSucc();
                now.addJumpInst(new JUMP(now, blocks.get((fn))));
                changed = true;
            }
        }
    }

    private BasicBlock getFinalTarget(BasicBlock b) {
        BasicBlock ret = b;
        BasicBlock t = jumpTarget.get(b);
        while (t != null) {
            ret = t;
            t = jumpTarget.get(t);
        }
        return ret;
    }

    private void BBEliminate(Function f) {
        jumpTarget.clear();
        for (BasicBlock b : f.getrPostOrder()) {
            if (b.getHead() == b.getTail() && b.getHead() instanceof JUMP) {
                jumpTarget.put(b, ((JUMP)b.getHead()).getTarget());
            }
        }
        for (BasicBlock b : f.getrPostOrder()) {
            if (b.getTail() instanceof JUMP) {
                JUMP j = (JUMP)b.getTail();
                if (j.getTarget() != getFinalTarget(j.getTarget())) {
                    changed = true;
                    b.getSucc().remove(j.getTarget());
                    j.setTarget(getFinalTarget(j.getTarget()));
                    b.getSucc().add(j.getTarget());
                }
            }
            else if (b.getTail() instanceof CJUMP) {
                CJUMP cj = (CJUMP)b.getTail();
                if (cj.getThenBB() != getFinalTarget(cj.getThenBB())) {
                    changed = true;
                    b.getSucc().remove(cj.getThenBB());
                    cj.setThenBB(getFinalTarget(cj.getThenBB()));
                    b.getSucc().add(cj.getThenBB());
                }
                if (cj.getElseBB() != getFinalTarget(cj.getElseBB())) {
                    changed = true;
                    b.getSucc().remove(cj.getElseBB());
                    cj.setElseBB(getFinalTarget(cj.getElseBB()));
                    b.getSucc().add(cj.getElseBB());
                }
                if (cj.getThenBB() == cj.getElseBB()) {
                    b.rmvJumpInst();
                    b.addJumpInst(new JUMP(b, cj.getThenBB()));
                    changed = true;
                }
            }
        }
    }

    private void dupInstEliminate(Function f) {
        for (BasicBlock b : f.getrPostOrder()) {
            Inst pred = null;
            for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
                boolean flag = false;
                if (i instanceof LOAD && pred instanceof LOAD) {
                    if (((LOAD)i).isSame((LOAD)pred)) {
                        flag = true;
                    }
                }
                else if (i instanceof STORE && pred instanceof STORE) {
                    if (((STORE)i).isSame((STORE)pred)) {
                        flag = true;
                    }
                }
                else if (i instanceof MOVE) {
                    if (((MOVE)i).getDest() == ((MOVE)i).getSrc()) {
                        flag = true;
                    }
                    else if (pred instanceof MOVE && ((MOVE)i).isSame((MOVE)pred)) {
                        flag = true;
                    }
                }
                if (flag) {
                    i.remove();
                }
                else {
                    pred = i;
                }
            }
        }
    }

    public void run() {
        for (Function f : root.funcs.values()) {
            la.analyseFunction(f);
        }
        changed = true;
        while (changed) {
            changed = false;
            for (Function f : root.funcs.values()) {
                if (f.isBuiltIn || f.getName().equals("__init__")) {
                    continue;
                }
                eliminate(f);
                loopEliminate(f);
                BBEliminate(f);
                if (changed) {
                    f.clearOrder();
                }
                la.analyseFunction(f);
            }
        }
    }

    public void run2() {
        for (Function f : root.funcs.values()) {
            dupInstEliminate(f);
        }
    }
}
