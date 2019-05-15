// 2019-04-23

package IR;

import Err.SomeError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Inst {
    private Inst pred;
    private Inst succ;
    protected BasicBlock bb;

    protected List<CommonReg> usedReg = new ArrayList<>();
    protected List<Reg> usedVal = new ArrayList<>();

    private boolean isRemoved = false;

    // for liveness analysis
    public Set<VirtualReg> liveIn = null;
    public Set<VirtualReg> liveOut = null;

    public Inst(BasicBlock b) {
        bb = b;
    }

    public Inst(BasicBlock b, Inst p, Inst s) {
        bb = b;
        pred = p;
        succ = s;
    }

    public void insertPred(Inst i) {
        if (pred != null)
            pred.succ = i;
        else
            bb.setHead(i);
        i.pred = pred;
        i.succ = this;
        pred = i;
    }

    public void insertSucc(Inst i) {
        if (succ != null)
            succ.pred = i;
        else
            bb.setTail(i);
        i.pred = this;
        i.succ = succ;
        succ = i;
    }

    public void remove() {
        if (isRemoved)
            throw new SomeError("In Inst::remove: the inst has been removed!");
        if (pred != null)
            pred.succ = succ;
        if (succ != null)
            succ.pred = pred;
        if (bb.getHead() == this)
            bb.setHead(succ);
        if (bb.getTail() == this)
            bb.setTail(pred);
    }

    public List<CommonReg> getUsedReg() {
        return usedReg;
    }

    public List<Reg> getUsedVal() {
        return usedVal;
    }

    public Inst getPred() {
        return pred;
    }

    public Inst getSucc() {
        return succ;
    }

    public BasicBlock getBB() {
        return bb;
    }

    public abstract void accept(IRVisitor v);
    public abstract void reloadRegs();
    public abstract CommonReg getDefinedReg();
    public abstract void setDefinedReg(CommonReg r);
    public abstract void renameUsedReg(Map<CommonReg, CommonReg> map);
    public abstract Inst copy(Map<Object, Object> map);
}
