// 2019-05-15

package BE;

import IR.*;

public class ConstProper implements IRVisitor {

    private boolean retrace = false;

    @Override
    public void visit(IRRoot ir) {
        retrace = false;
        for (Function f : ir.funcs.values()) {
            f.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock b) {
        /*if (!b.phi.isEmpty()) {
            for (PhiInst pi : b.phi.values()) {
                pi.accept(this);
            }
        }*/
        for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
            i.accept(this);
        }
        retrace = true;
        for (Inst i = b.getHead(); i != null; i = i.getSucc()) {
            i.accept(this);
        }
    }

    @Override
    public void visit(Function f) {
        for (BasicBlock b : f.getrPostOrder()) {
            b.accept(this);
        }
    }

    @Override
    public void visit(ALLOC a) {
        if (a.getSize() instanceof CommonReg && ((CommonReg)a.getSize()).constValue != null) {
            a.setSize(new CONST(((CommonReg)a.getSize()).constValue));
        }
        a.getDest().constValue = null;
    }

    @Override
    public void visit(BINOP bo) {
        if (!retrace) {
            Reg lhs = bo.getLhs();
            Reg rhs = bo.getRhs();
            if (lhs instanceof CommonReg && ((CommonReg) lhs).constValue != null) {
                bo.setLhs(new CONST(((CommonReg) lhs).constValue));
            }
            if (rhs instanceof CommonReg && ((CommonReg) rhs).constValue != null) {
                bo.setRhs(new CONST(((CommonReg) rhs).constValue));
            }
            lhs = bo.getLhs();
            rhs = bo.getRhs();
            if (lhs instanceof CONST && rhs instanceof CONST) {
                int li = ((CONST) lhs).getVal();
                int ri = ((CONST) rhs).getVal();
                Integer res = null;
                switch (bo.getOp()) {
                    case BOR:
                        res = li | ri;
                        break;
                    case BXOR:
                        res = li ^ ri;
                        break;
                    case BAND:
                        res = li & ri;
                        break;
                    case ADD:
                        res = li + ri;
                        break;
                    case MUL:
                        res = li * ri;
                        break;
                    case SUB:
                        res = li - ri;
                        break;
                    case SRA:
                        res = li >> ri;
                        break;
                    case MOD:
                        assert ri != 0;
                        res = li % ri;
                        break;
                    case SLA:
                        res = li << ri;
                        break;
                    case DIV:
                        assert ri != 0;
                        res = li / ri;
                        break;
                }
                bo.getDest().constValue = res;
                bo.insertPred(new MOVE(bo.getBB(), bo.getDest(), new CONST(res)));
                bo.remove();
            } else if (lhs instanceof CONST && !(rhs instanceof CONST)) {
                switch (bo.getOp()) {
                    case MUL:
                    case ADD:
                    case BAND:
                    case BXOR:
                    case BOR:
                        bo.setLhs(rhs);
                        bo.setRhs(lhs);
                        break;
                }
            } else {
                bo.getDest().constValue = null;
            }
        }
        else {
            bo.getDest().constValue = null;
        }
    }

    @Override
    public void visit(CALL c) {
        if (c.getDest() != null) {
            c.getDest().constValue = null;
        }
    }

    @Override
    public void visit(CMP cm) {
        if (!retrace) {
            Reg lhs = cm.getLhs();
            Reg rhs = cm.getRhs();
            if (lhs instanceof CommonReg && ((CommonReg) lhs).constValue != null) {
                cm.setLhs(new CONST(((CommonReg) lhs).constValue));
            }
            if (rhs instanceof CommonReg && ((CommonReg) rhs).constValue != null) {
                cm.setRhs(new CONST(((CommonReg) rhs).constValue));
            }
            lhs = cm.getLhs();
            rhs = cm.getRhs();
            if (lhs instanceof CONST && rhs instanceof CONST) {
                int li = ((CONST) lhs).getVal();
                int ri = ((CONST) rhs).getVal();
                Integer res = null;
                switch (cm.getOp()) {
                    case NEQ:
                        res = (li != ri ? 1 : 0);
                        break;
                    case EQL:
                        res = (li == ri ? 1 : 0);
                        break;
                    case GRT:
                        res = (li > ri ? 1 : 0);
                        break;
                    case LTE:
                        res = (li <= ri ? 1 : 0);
                        break;
                    case GTE:
                        res = (li >= ri ? 1 : 0);
                        break;
                    case LES:
                        res = (li < ri ? 1 : 0);
                        break;
                }
                cm.getDest().constValue = res;
                cm.insertPred(new MOVE(cm.getBB(), cm.getDest(), new CONST(res)));
                cm.remove();
            } else if (lhs instanceof CONST && !(rhs instanceof CONST)) {
                cm.setLhs(rhs);
                cm.setRhs(lhs);
                switch (cm.getOp()) {
                    case LES:
                        cm.setOp(CMP.OP.GRT);
                        break;
                    case GTE:
                        cm.setOp(CMP.OP.LTE);
                        break;
                    case LTE:
                        cm.setOp(CMP.OP.GTE);
                        break;
                    case GRT:
                        cm.setOp(CMP.OP.LES);
                        break;
                }
            } else {
                cm.getDest().constValue = null;
            }
        }
        else {
            cm.getDest().constValue = null;
        }
    }

    @Override
    public void visit(CONST cn) {
        // do nothing
    }

    @Override
    public void visit(LOAD l) {
        l.getDest().constValue = null;
    }

    @Override
    public void visit(MOVE m) {
        if (!retrace) {
            Reg s = m.getSrc();
            if (s instanceof CONST) {
                m.getDest().constValue = ((CONST) s).getVal();
            } else if (s instanceof CommonReg && ((CommonReg) s).constValue != null) {
                m.getDest().constValue = ((CommonReg) s).constValue;
                m.setSrc(new CONST(((CommonReg) s).constValue));
            }
        }
        else {
            m.getDest().constValue = null;
        }
    }

    @Override
    public void visit(PhiInst p) {
        // do nothing
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
        if (s.getValue() instanceof CommonReg && ((CommonReg)s.getValue()).constValue != null) {
            s.setValue(new CONST(((CommonReg)s.getValue()).constValue));
        }
    }

    @Override
    public void visit(UNOP uo) {
        if (!retrace) {
            Reg opr = uo.getOperand();
            CommonReg d = uo.getDest();
            Integer c = null;
            if (opr instanceof CONST) {
                switch (uo.getOp()) {
                    case BNOT:
                        c = ~((CONST)opr).getVal();
                        break;
                    case NEG:
                        c = -((CONST)opr).getVal();
                        break;
                }
            }
            else if (opr instanceof CommonReg && ((CommonReg)opr).constValue != null) {
                switch (uo.getOp()) {
                    case BNOT:
                        c = ~((CommonReg)opr).constValue;
                        break;
                    case NEG:
                        c = -((CommonReg)opr).constValue;
                        break;
                }
                uo.setOprand(new CONST(((CommonReg)opr).constValue));
            }
            else {
                uo.getDest().constValue = null;
            }
            if (c != null) {
                uo.insertPred(new MOVE(uo.getBB(), d, new CONST(c)));
                uo.remove();
            }
        }
        else {
            uo.getDest().constValue = null;
        }
    }

    @Override
    public void visit(CJUMP cj) {
        if (cj.getCond() instanceof CommonReg && ((CommonReg)cj.getCond()).constValue != null) {
            cj.setCond(new CONST(((CommonReg)cj.getCond()).constValue));
        }
    }

    @Override
    public void visit(JUMP j) {
        // do nothing
    }

    @Override
    public void visit(RETURN r) {
        if (r.getRetVal() instanceof CommonReg && ((CommonReg)r.getRetVal()).constValue != null) {
            r.setRetVal(new CONST(((CommonReg)r.getRetVal()).constValue));
        }
    }

    @Override
    public void visit(VirtualReg vr) {
        // do nothing
    }

    @Override
    public void visit(PhysicalReg pr) {
        // do nothing
    }

    @Override
    public void visit(StaticString ss) {
        // do nothing
    }

    @Override
    public void visit(StackSlot sl) {
        // do nothing
    }

    @Override
    public void visit(StaticSpace sp) {
        // do nothing
    }
}
