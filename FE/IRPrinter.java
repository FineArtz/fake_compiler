// 2019-05-02

package FE;

import IR.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public class IRPrinter implements IRVisitor {
    private PrintStream out;
    private String indent;

    public IRPrinter(PrintStream out) {
        indent = "";
        this.out = out;
    }

    public IRPrinter(OutputStream os) {
        indent = "";
        out = new PrintStream(os);
    }

    private Set<BasicBlock> visited = new HashSet<>();
    private Map<BasicBlock, String> labels = new HashMap<>();
    private Map<StaticData, String> sdata = new HashMap<>();
    private Map<VirtualReg, String> regMap = new HashMap<>();
    private Map<String, Integer> regCount = new HashMap<>();
    private Map<String, Integer> bbCount = new HashMap<>();
    private boolean inStatic;
    private boolean isDest;

    @Override
    public void visit(IRRoot ir) {
        if (!ir.data.isEmpty()) {
            inStatic = true;
            for (StaticData sd : ir.data) {
                sd.accept(this);
            }
            inStatic = false;
        }
        if (!ir.strs.isEmpty()) {
            for (StaticString ss : ir.strs.values()) {
                ss.accept(this);
            }
        }
        printlnRaw("");
        for (Function f : ir.funcs.values()) {
            f.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock b) {
        if (visited.contains(b)) {
            return;
        }
        visited.add(b);
        printlnRaw("%" + getID(b) + ":");
        if (!b.phi.isEmpty()) {
            for (PhiInst p : b.phi.values()) {
                p.accept(this);
            }
        }
        for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(Function f) {
        regMap = new IdentityHashMap<>();
        regCount = new HashMap<>();
        printRaw(String.format("func %s ", f.getName()));
        for (VirtualReg r : f.args) {
            printRaw(String.format("$%s ", getID(r)));
        }
        printlnRaw(" {");
        for (BasicBlock b : f.getrPostOrder()) {
            moreIndent();
            b.accept(this);
            lessIndent();
        }
        printlnRaw("}\n");
    }

    @Override
    public void visit(ALLOC a) {
        isDest = true;
        a.getDest().accept(this);
        isDest = false;
        printRaw(" = alloc ");
        a.getSize().accept(this);
        printlnRaw("");
    }

    @Override
    public void visit(BINOP bo) {
        String op = null;
        switch (bo.getOp()) {
            case MUL:
                op = "MUL";
                break;
            case DIV:
                op = "DIV";
                break;
            case MOD:
                op = "MOD";
                break;
            case ADD:
                op = "ADD";
                break;
            case SUB:
                op = "SUB";
                break;
            case SLA:
                op = "LSFT";
                break;
            case SRA:
                op = "RSFT";
                break;
            case BAND:
                op = "AND";
                break;
            case BOR:
                op = "OR";
                break;
            case BXOR:
                op = "XOR";
                break;
        }
        isDest = true;
        bo.getDest().accept(this);
        isDest = false;
        printRaw(String.format(" = %s ", op));
        bo.getLhs().accept(this);
        printRaw(" ");
        bo.getRhs().accept(this);
        printlnRaw("");
    }

    @Override
    public void visit(CALL c) {
        if (c.getDest() != null) {
            isDest = true;
            c.getDest().accept(this);
            isDest = false;
            printRaw(" = ");
            printRaw(String.format("call %s ", c.getFunc().getName()));
        }
        else {
            printWithIndent(String.format("call %s ", c.getFunc().getName()));
        }
        if (c.getArgs() != null && !c.getArgs().isEmpty()) {
            for (Reg r : c.getArgs()) {
                r.accept(this);
                printRaw(" ");
            }
        }
        printlnRaw("");
    }

    @Override
    public void visit(CMP cm) {
        String op = null;
        switch (cm.getOp()) {
            case LES:
                op = "LT";
                break;
            case GRT:
                op = "GT";
                break;
            case LTE:
                op = "LTE";
                break;
            case GTE:
                op = "GTE";
                break;
            case EQL:
                op = "EQ";
                break;
            case NEQ:
                op = "NEQ";
                break;
        }
        isDest = true;
        cm.getDest().accept(this);
        isDest = false;
        printRaw(String.format(" = %s ", op));
        cm.getLhs().accept(this);
        printRaw(" ");
        cm.getRhs().accept(this);
        printlnRaw("");
    }

    @Override
    public void visit(CONST cn) {
        printRaw(String.format("%d", cn.getVal()));
    }

    @Override
    public void visit(LOAD l) {
        isDest = true;
        l.getDest().accept(this);
        isDest = false;
        printRaw(String.format(" = load %d ", l.getSize()));
        l.getAddr().accept(this);
        printlnRaw(String.format(" %d", l.getOffset()));
    }

    @Override
    public void visit(MOVE m) {
        isDest = true;
        m.getDest().accept(this);
        isDest = false;
        printRaw(" = MOV ");
        m.getSrc().accept(this);
        printlnRaw("");
    }

    @Override
    public void visit(PhiInst p) {
        isDest = true;
        p.getDest().accept(this);
        isDest = false;
        printRaw(" = phi");
        for (Map.Entry<BasicBlock, Reg> e : p.path.entrySet()) {
            BasicBlock bb = e.getKey();
            Reg r = e.getValue();
            String src = null;
            if (r == null) {
                src = "undef";
            }
            else if (r instanceof VirtualReg) {
                src = "$" + getID((VirtualReg)r);
            }
            else if (r instanceof CONST) {
                src = String.format("%d", ((CONST)r).getVal());
            }
            else {
                assert false;
            }
            printRaw(String.format(" %%%s %s", getID(bb), src));
        }
        printlnRaw("");
    }

    @Override
    public void visit(POP pp) {
        // do nothing
    }

    @Override
    public void visit(PUSH ps) {
        // do nothing
    }

    @Override
    public void visit(STORE s) {
        printWithIndent(String.format("store %d ", s.getSize()));
        s.getAddr().accept(this);
        printRaw(" ");
        s.getValue().accept(this);
        printlnRaw(String.format(" %d", s.getOffset()));
    }

    @Override
    public void visit(UNOP uo) {
        String op = null;
        switch (uo.getOp()) {
            case NEG:
                op = "NEG";
                break;
            case BNOT:
                op = "NOT";
                break;
        }
        isDest = true;
        uo.getDest().accept(this);
        isDest = false;
        printRaw(String.format(" = %s ", op));
        uo.getOperand().accept(this);
        printlnRaw("");
    }

    @Override
    public void visit(CJUMP cj) {
        printWithIndent("br ");
        cj.getCond().accept(this);
        printlnRaw(String.format(" %%%s %%%s", getID(cj.getThenBB()), getID(cj.getElseBB())));
        printlnRaw("");
    }

    @Override
    public void visit(JUMP j) {
        printlnWithIndent(String.format("jmp %%%s", getID(j.getTarget())));
        printlnRaw("");
    }

    @Override
    public void visit(RETURN r) {
        printWithIndent("ret ");
        if (r.getRetVal() != null) {
            r.getRetVal().accept(this);
        }
        printlnRaw("");
    }

    @Override
    public void visit(VirtualReg vr) {
        if (isDest) {
            printWithIndent(String.format("$%s", getID(vr)));
        }
        else {
            printRaw(String.format("$%s", getID(vr)));
        }
    }

    @Override
    public void visit(PhysicalReg pr) {
        // do nothing
    }

    @Override
    public void visit(StaticString ss) {
        if (inStatic) {
            printlnRaw(String.format("asciiz @%s %s", getID(ss), ss.getVal()));
        }
        else {
            printRaw(String.format("@%s", getID(ss)));
        }
    }

    @Override
    public void visit(StackSlot sl) {
        // do nothing
    }

    @Override
    public void visit(StaticSpace sp) {
        if (inStatic) {
            printlnRaw(String.format("space @%s %s", getID(sp), sp.getSize()));
        }
        else {
            printRaw(String.format("@%s", getID(sp)));
        }
    }

    private void moreIndent() {
        indent = indent + "\t";
    }

    private void lessIndent() {
        indent = indent.substring(1);
    }

    private void printWithIndent(String s) {
        out.print(indent + s);
    }

    private void printlnWithIndent(String s) {
        out.println(indent + s);
    }

    private void printRaw(String s) {
        out.print(s);
    }

    private void printlnRaw(String s) {
        out.println(s);
    }

    private String newID(String name, Map<String, Integer> count) {
        Integer cnt = count.get(name);
        if (cnt == null) {
            cnt = 1;
        }
        else {
            ++cnt;
        }
        count.put(name, cnt);
        return name + "_" + cnt;
    }

    private String getID(VirtualReg r) {
        String id = regMap.get(r);
        if (id == null) {
            id = newID(r.name == null ? "t" : r.name, regCount);
            regMap.put(r, id);
        }
        return id;
    }

    private String getID(BasicBlock b) {
        String id = labels.get(b);
        if (id == null) {
            id = newID(b.getName(), bbCount);
            labels.put(b, id);
        }
        return id;
    }

    private String getID(StaticData s) {
        String id = sdata.get(s);
        if (id == null) {
            id = newID(s.getName(), bbCount);
            sdata.put(s, id);
        }
        return id;
    }
}
