// 2019-05-07

package BE;

import Err.SomeError;
import IR.*;

import java.io.*;
import java.util.*;

public class CodePrinter implements IRVisitor {
    private StringBuilder code;
    private String indent;
    private PrintStream out;

    public CodePrinter(PrintStream out) {
        indent = "";
        this.out = out;
    }

    public CodePrinter(OutputStream os) {
        indent = "";
        out = new PrintStream(os);
    }

    private Set<BasicBlock> visited = new HashSet<>();
    private Map<BasicBlock, String> labels = new HashMap<>();
    private Map<StaticData, String> sdata = new HashMap<>();
    private Map<String, Integer> bbCount = new HashMap<>();
    private boolean inStatic;
    private boolean isDest;
    private boolean hasBracket;
    private PhysicalReg pr1, pr2;

    @Override
    public void visit(IRRoot ir) {
        code = new StringBuilder();
        pr1 = ir.pr1;
        pr2 = ir.pr2;

        // head
        addLine("global main");
        addLine("extern malloc");
        addLine("");

        // SECTION .text
        addLine("SECTION .text");
        for (Function f : ir.funcs.values()) {
            f.accept(this);
            addLine("");
        }
        addLine("");

        // SECTION .data
        if (!ir.strs.isEmpty()) {
            addLine("SECTION .data\t\talign=8");
            moreIndent();
            inStatic = true;
            for (StaticString ss : ir.strs.values()) {
                ss.accept(this);
            }
            inStatic = false;
            lessIndent();
            addLine("");
        }

        // SECTION .bss
        if (!ir.data.isEmpty()) {
            addLine("SECTION .bss\t\talign=8");
            moreIndent();
            inStatic = true;
            for (StaticData sd : ir.data) {
                sd.accept(this);
            }
            inStatic = false;
            lessIndent();
            addLine("");
        }

        addLine("");

        // lib
        addLib();

        out.print(code.toString());
    }

    @Override
    public void visit(BasicBlock b) {
        if (visited.contains(b)) {
            return;
        }
        visited.add(b);
        addRawLine(getID(b).concat(":"));
        if (!b.phi.isEmpty()) {
            for (PhiInst p : b.phi.values()) {
                p.accept(this);
            }
        }
        for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
            i.accept(this);
        }
        addLine("");
    }

    @Override
    public void visit(Function f) {
        addRawLine("# function ".concat(f.getName()));
        if (f.getName().equals("main")) {
            addRawLine("main:");
        }
        for (BasicBlock b : f.getrPostOrder()) {
            moreIndent();
            b.accept(this);
            lessIndent();
        }
    }

    @Override
    public void visit(ALLOC a) {
        addLine("call malloc");
    }

    @Override
    public void visit(BINOP bo) {
        String opname = null;
        BINOP.OP op = bo.getOp();
        Reg lhs = bo.getLhs();
        Reg rhs = bo.getRhs();
        if (lhs instanceof CONST && rhs instanceof CONST) {
            return;
        }
        CommonReg dest = bo.getDest();
        switch (op) {
            case SLA:
            case SRA:
                addLine("mov rbx, rcx");
                addWithIndent("mov rcx, ");
                rhs.accept(this);
                addLine("");
                if (bo.getOp() == BINOP.OP.SLA) {
                    addWithIndent("sal ");
                }
                else {
                    addWithIndent("sar ");
                }
                lhs.accept(this);
                add(", c1\n");
                addLine("mov rcx, rbx");
                addWithIndent("and ");
                dest.accept(this);
                add(", -1\n");
                return;
            case DIV:
            case MOD:
                addWithIndent("mov rbx, ");
                rhs.accept(this);
                addLine("");
                addWithIndent("mov rax, ");
                lhs.accept(this);
                addLine("");
                addLine("mov ".concat(pr1.getName()).concat(", rdx"));
                addLine("cdq");
                addLine("idiv rbx");
                addWithIndent("mov ");
                dest.accept(this);
                if (op == BINOP.OP.DIV) {
                    add(", rax\n");
                }
                else {
                    add(", rdx\n");
                }
                addLine("mov rdx, ".concat(pr1.getName()));
                return;
            case ADD:
                if (rhs instanceof CONST && ((CONST)rhs).getVal() == 1) {
                    addWithIndent("inc ");
                    lhs.accept(this);
                    addLine("");
                    return;
                }
                opname = "add";
                break;
            case SUB:
                if (rhs instanceof CONST && ((CONST)rhs).getVal() == 1) {
                    addWithIndent("dec ");
                    lhs.accept(this);
                    addLine("");
                    return;
                }
                opname = "sub";
                break;
            case MUL:
                opname = "imul";
                break;
            case BXOR:
                opname = "xor";
                break;
            case BAND:
                opname = "and";
                break;
            case BOR:
                opname = "or";
                break;
        }
        addWithIndent(opname.concat(" "));
        lhs.accept(this);
        add(", ");
        rhs.accept(this);
        addLine("");
    }

    @Override
    public void visit(CALL c) {
        if (c.getFunc().isBuiltIn) {
            addLine("call ".concat(c.getFunc().getName()));
        }
        else {
            addLine("call ".concat(getID(c.getFunc().getHead())));
        }
    }

    @Override
    public void visit(CMP cm) {
        String opname = null;
        CMP.OP op = cm.getOp();
        Reg lhs = cm.getLhs();
        Reg rhs = cm.getRhs();
        CommonReg dest = cm.getDest();
        if (lhs instanceof PhysicalReg) {
            addWithIndent("and ");
            lhs.accept(this);
            add(", -1\n");
        }
        if (rhs instanceof PhysicalReg) {
            addWithIndent("and ");
            rhs.accept(this);
            add(", -1\n");
        }
        addLine("xor rax, rax");
        addWithIndent("cmp ");
        lhs.accept(this);
        add(", ");
        rhs.accept(this);
        addLine("");
        switch (op) {
            case LES:
                opname = "setl";
                break;
            case GRT:
                opname = "setg";
                break;
            case LTE:
                opname = "setle";
                break;
            case GTE:
                opname = "setge";
                break;
            case EQL:
                opname = "sete";
                break;
            case NEQ:
                opname = "setne";
                break;
        }
        addLine(opname.concat(" al"));
        addWithIndent("mov ");
        isDest = true;
        dest.accept(this);
        isDest = false;
        add(", rax\n");
    }

    @Override
    public void visit(CONST cn) {
        add(String.valueOf(cn.getVal()));
    }

    @Override
    public void visit(LOAD l) {
        if (l.getAddr() instanceof StaticString) {
            addWithIndent("mov ");
            l.getDest().accept(this);
            add(", ".concat(size(l.getSize())).concat(" "));
            l.getAddr().accept(this);
            addLine("");
        }
        else {
            addWithIndent("mov ");
            isDest = true;
            l.getDest().accept(this);
            isDest = false;
            add(", ".concat(size(l.getSize())).concat("["));
            hasBracket = true;
            l.getAddr().accept(this);
            if (l.getOffset() < 0) {
                add(String.valueOf(l.getOffset()));
            }
            else if (l.getOffset() > 0) {
                add("+".concat(String.valueOf(l.getOffset())));
            }
            hasBracket = false;
            add("]\n");
        }
    }

    @Override
    public void visit(MOVE m) {
        addWithIndent("mov ");
        isDest = true;
        m.getDest().accept(this);
        isDest = false;
        add(", ");
        m.getSrc().accept(this);
        addLine("");
    }

    @Override
    public void visit(PhiInst p) {
        /*
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
        */
    }

    @Override
    public void visit(POP pp) {
        addWithIndent("pop ");
        pp.getPreg().accept(this);
        addLine("");
    }

    @Override
    public void visit(PUSH ps) {
        addWithIndent("push ");
        ps.getReg().accept(this);
        addLine("");
    }

    @Override
    public void visit(STORE s) {
        if (s.getAddr() instanceof StaticString) {
            addWithIndent("mov ".concat(size(s.getSize())).concat(" "));
            s.getAddr().accept(this);
            add(" ");
            s.getValue().accept(this);
            addLine("");
        }
        else {
            addWithIndent("mov ".concat(size(s.getSize())).concat("["));
            hasBracket = true;
            s.getAddr().accept(this);
            if (s.getOffset() < 0) {
                add(String.valueOf(s.getOffset()));
            }
            else if (s.getOffset() > 0) {
                add("+".concat(String.valueOf(s.getOffset())));
            }
            hasBracket = false;
            add("], ");
            s.getValue().accept(this);
            addLine("");
        }
    }

    @Override
    public void visit(UNOP uo) {
        String op = null;
        switch (uo.getOp()) {
            case NEG:
                op = "neg";
                break;
            case BNOT:
                op = "not";
                break;
        }
        addWithIndent("mov ");
        isDest = true;
        uo.getDest().accept(this);
        isDest = false;
        add(", ");
        uo.getOperand().accept(this);
        addLine("");
        addWithIndent(op.concat(" "));
        uo.getDest().accept(this);
        addLine("");
    }

    @Override
    public void visit(CJUMP cj) {
        if (cj.getCond() instanceof CONST) {
            addLine("jmp ".concat(((CONST)cj.getCond()).getVal() == 1 ? getID(cj.getThenBB()) : getID(cj.getElseBB())));
        }
        else {
            addWithIndent("cmp ");
            cj.getCond().accept(this);
            add(", 1\n");
            addLine("je ".concat(getID(cj.getThenBB())));
            if (cj.getBB().getDfsStamp() == cj.getElseBB().getDfsStamp() + 1) {
                return;
            }
            addLine("jmp ".concat(getID(cj.getElseBB())));
        }
    }

    @Override
    public void visit(JUMP j) {
        if (j.getBB().getDfsStamp() == j.getTarget().getDfsStamp() + 1) {
            return;
        }
        addLine("jmp ".concat(getID(j.getTarget())));
    }

    @Override
    public void visit(RETURN r) {
        addLine("ret");
    }

    @Override
    public void visit(VirtualReg vr) {
        if (vr.preg == null) {
            throw new SomeError("In CodePrinter, visit VirtualReg: virtual register should have been allocated for a physical register");
        }
        vr.preg.accept(this);
    }

    @Override
    public void visit(PhysicalReg pr) {
        add(pr.getName());
    }

    @Override
    public void visit(StaticString ss) {
        if (inStatic) {
            addLine(getID(ss).concat(":"));
            addLine("dq ".concat(String.valueOf(ss.getVal().length())));
            addLine("db ".concat(ss.getHextech()));
        }
        else {
            add(getID(ss));
        }
    }

    @Override
    public void visit(StackSlot sl) {
        assert false;
    }

    @Override
    public void visit(StaticSpace sp) {
        if (inStatic) {
            addLine(getID(sp).concat(": ").concat(size(sp)).concat(" 1"));
        }
        else {
            if (!hasBracket) {
                add("[".concat(getID(sp)).concat("]"));
            }
            else {
                add(getID(sp));
            }
        }
    }

    private void moreIndent() {
        indent = indent + "\t";
    }

    private void lessIndent() {
        indent = indent.substring(1);
    }

    private void addLine(String s) {
        code.append(indent.concat(s).concat("\n"));
    }

    private void addRawLine(String s) {
        code.append(s.concat("\n"));
    }

    private void add(String s) {
        code.append(s);
    }

    private void addWithIndent(String s) {
        code.append(indent.concat(s));
    }

    private void addLib() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("./lib/builtin.asm"));
            String s = br.readLine();
            while (s != null) {
                code.append(s.concat("\n"));
                s = br.readLine();
            }
        } catch (IOException e) {
            throw new SomeError("lib file does not exit");
        }
    }

    private String size(int s) {
        if (s == 1) {
            return "byte";
        }
        else if (s == 2) {
            return "word";
        }
        else if (s == 4) {
            return "dword";
        }
        else if (s == 8) {
            return "qword";
        }
        assert false;
        return null;
    }

    private String size(StaticSpace sp) {
        if (sp.getSize() == 1) {
            return "resb";
        }
        else if (sp.getSize() == 2) {
            return "resw";
        }
        else if (sp.getSize() == 4) {
            return "resd";
        }
        else if (sp.getSize() == 8) {
            return "resq";
        }
        assert false;
        return null;
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
