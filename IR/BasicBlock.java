// 2019-04-23

package IR;

import Err.SomeError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BasicBlock {
    private Inst head, tail;
    private String name;
    private Boolean isEnd = false;
    private Function parent;
    public Map<VirtualReg, PhiInst> phi = new HashMap<>();

    // CFG info
    private Set<BasicBlock> pred = new HashSet<>();
    private Set<BasicBlock> succ = new HashSet<>();
    private int dfsStamp;

    public Set<BasicBlock> doms;
    public BasicBlock IDom;
    public Set<BasicBlock> children;
    public Set<BasicBlock> DF;

    public BasicBlock(Function p, String n) {
        parent = p;
        name = (n == null ? "Block" : n);
    }

    public void addInst(Inst i) {
        if (isEnd)
            throw new SomeError("Block has ended!");
        if (head == null) {
            head = i;
            tail = i;
        }
        else {
            tail.insertSucc(i);
            tail = i;
        }
    }

    private void addSucc(BasicBlock b) {
        if (b == null)
            return;
        succ.add(b);
        b.pred.add(this);
    }

    private void rmvSucc(BasicBlock b) {
        if (b == null)
            return;
        succ.remove(b);
        b.pred.remove(this);
    }

    public void clearSucc() {
        for (BasicBlock b : succ) {
            rmvSucc(b);
        }
    }

    public void addJumpInst(JumpInst j) {
        addInst(j);
        isEnd = true;
        if (j instanceof CJUMP) {
            addSucc(((CJUMP)j).getThenBB());
            addSucc(((CJUMP)j).getElseBB());
        }
        else if (j instanceof JUMP) {
            addSucc(((JUMP)j).getTarget());
        }
        else if (j instanceof RETURN) {
            parent.returnList.add((RETURN)j);
        }
        else
            throw new SomeError("In addJumpInst: unexpected JumpInst");
    }

    public void rmvJumpInst() {
        isEnd = false;
        if (tail instanceof CJUMP) {
            rmvSucc(((CJUMP)tail).getThenBB());
            rmvSucc(((CJUMP)tail).getElseBB());
        }
        else if (tail instanceof JUMP) {
            rmvSucc(((JUMP)tail).getTarget());
        }
        else if (tail instanceof RETURN) {
            parent.returnList.remove((RETURN)tail);
        }
        else
            throw new SomeError("In rmvJumpInst: unexpected JumpInst");
        tail.remove();
    }

    public void accept(IRVisitor v) {
        v.visit(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHead(Inst head) {
        this.head = head;
    }

    public void setTail(Inst tail) {
        this.tail = tail;
    }

    public void setDfsStamp(int dfsStamp) {
        this.dfsStamp = dfsStamp;
    }

    public String getName() {
        return name;
    }

    public Inst getHead() {
        return head;
    }

    public Inst getTail() {
        return tail;
    }

    public Set<BasicBlock> getPred() {
        return pred;
    }

    public Set<BasicBlock> getSucc() {
        return succ;
    }

    public int getDfsStamp() {
        return dfsStamp;
    }

    public Function getParent() {
        return parent;
    }

    public Boolean isEnd() {
        return isEnd;
    }
}
