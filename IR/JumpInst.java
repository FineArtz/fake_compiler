// 2019-04-23

package IR;

public abstract class JumpInst extends Inst {
    public JumpInst(BasicBlock b) {
        super(b);
    }
    public JumpInst(BasicBlock b, Inst p, Inst s) {
        super(b, p, s);
    }
}
