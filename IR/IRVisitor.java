// 2019-04-23

package IR;

public interface IRVisitor {
    public void visit(IRRoot ir);
    public void visit(BasicBlock b);
    public void visit(Function f);

    public void visit(ALLOC a);
    public void visit(BINOP bo);
    public void visit(CALL c);
    public void visit(CMP cm);
    public void visit(CONST cn);
    public void visit(LOAD l);
    public void visit(MOVE m);
    public void visit(PhiInst p);
    public void visit(POP pp);
    public void visit(PUSH ps);
    public void visit(STORE s);
    public void visit(UNOP uo);

    public void visit(CJUMP cj);
    public void visit(JUMP j);
    public void visit(RETURN r);

    public void visit(VirtualReg vr);
    public void visit(PhysicalReg pr);
    public void visit(StaticString ss);
    public void visit(StackSlot sl);
    public void visit(StaticSpace sp);
}
