// 2019-04-23

package IR;

import Symbol.FuncSymbol;
import Types.STRING;

import java.util.*;

public class Function {
    private FuncSymbol func;
    private BasicBlock head, tail;
    private String name;
    public List<VirtualReg> args = new ArrayList<>();
    public Set<PhysicalReg> pregs = new HashSet<>();
    public Set<PhysicalReg> gpregs = new HashSet<>();
    public List<StackSlot> slots = new ArrayList<>();
    public boolean isBuiltIn;

    // CFG info
    private List<BasicBlock> rPostOrder = null;
    private List<BasicBlock> rPrevOrder = null;
    private List<BasicBlock> prevOrder = null;
    private List<BasicBlock> domTreePrevOrder = null;
    private Set<BasicBlock> vis = null;

    public List<RETURN> returnList = new ArrayList<>();
    public Set<Function> callee = new HashSet<>();
    public Set<Function> rcallee = new HashSet<>();


    public Function(FuncSymbol f) {
        func = f;
        name = f.name;
        head = new BasicBlock(this, name + ".Entry");
        tail = null;
        isBuiltIn = false;
    }

    public Function(FuncSymbol f, int foo) {
        func = f;
        name = f.name;
        head = null;
        tail = null;
        isBuiltIn = true;
    }

    /*
     * The following algorithms are from
     *      https://blog.csdn.net/dashuniuniu/article/details/52224882,
     * which refers a paper written by Keith D. Cooper.
     *
     * The link of paper:
     *      https://www.cs.rice.edu/~keith/EMBED/dom.pdf
     *
     * There exits a much more optimized algorithm named
     * Lengauer-Tarjan algorithm, which uses Semi-Dominator
     * and Union-Find to reach nearly-linear complexity.
     *
     * A blog describing this algorithm:
     *      https://www.cnblogs.com/meowww/archive/2017/02/27/6475952.html
     *
     * Tarjan's paper:
     *      http://delivery.acm.org/10.1145/360000/357071/p121-lengauer.pdf?ip=202.120.19.214&id=357071&acc=ACTIVE%20SERVICE&key=BF85BBA5741FDC6E%2E17676C47DFB149BF%2E4D4702B0C3E38B35%2E4D4702B0C3E38B35&__acm__=1556372105_27a85b4bd61ee0fc6de260b4ef792f04
     */

    private void _dfsPost(BasicBlock b) {
        if (vis.contains(b))
            return;
        vis.add(b);
        for (BasicBlock bb : b.getSucc())
            _dfsPost(bb);
        rPostOrder.add(b);
    }

    private void _dfsPrev(BasicBlock b) {
        if (vis.contains(b))
            return;
        vis.add(b);
        rPrevOrder.add(b);
        prevOrder.add(b);
        if (b.getSucc().size() == 2) {
            List<BasicBlock> bs = new ArrayList<>(b.getSucc());
            if (bs.get(0).getName().endsWith("body") || bs.get(0).getName().endsWith("true")) {
                _dfsPrev(bs.get(0));
                _dfsPrev(bs.get(1));
            }
            else {
                _dfsPrev(bs.get(1));
                _dfsPrev(bs.get(0));
            }
        }
        else {
            for (BasicBlock bb : b.getSucc())
                _dfsPrev(bb);
        }
    }

    private void dfsPost() {
        rPostOrder = new ArrayList<>();
        vis = new HashSet<>();
        _dfsPost(head);
        for (int i = 0; i < rPostOrder.size(); ++i)
            rPostOrder.get(i).setDfsStamp(i);
        Collections.reverse(rPostOrder);
        vis = null;
    }

    private void dfsPrev() {
        rPrevOrder = new ArrayList<>();
        prevOrder = new ArrayList<>();
        vis = new HashSet<>();
        _dfsPrev(head);
        Collections.reverse(rPrevOrder);
        vis = null;
    }

    public void calcDT() {
        // from top to bottom
        List<BasicBlock> allBlocks = getrPostOrder();
        for (BasicBlock b : allBlocks) {
            b.doms.clear();
        }
        head.doms.add(head);

        boolean flag = true;
        while (flag) {
            flag = false;
            for (BasicBlock b : allBlocks) {
                Set<BasicBlock> tmp = new HashSet<>(allBlocks);
                if (!b.getPred().isEmpty()) {
                    for (BasicBlock bb : b.getPred()) {
                        flag = tmp.retainAll(bb.doms);
                    }
                }
                tmp.add(b);
                if (!b.doms.equals(tmp)) {
                    b.doms = tmp;
                    flag = true;
                }
            }
        }
    }

    private BasicBlock _intersect(BasicBlock b1, BasicBlock b2) {
        BasicBlock f1 = b1, f2 = b2;
        while (f1 != f2) {
            while (f1.getDfsStamp() < f2.getDfsStamp())
                f1 = f1.IDom;
            while (f1.getDfsStamp() > f2.getDfsStamp())
                f2 = f2.IDom;
        }
        return f1;
    }

    public void calcIDom() {
        List<BasicBlock> allBlocks = getrPostOrder();
        for (BasicBlock b : allBlocks) {
            b.IDom = null;
            b.children = new HashSet<>();
        }
        head.IDom = head;

        boolean flag = true;
        while (flag) {
            flag = false;
            for (BasicBlock b : allBlocks) {
                if (b == head)
                    continue;
                BasicBlock newIdom = null;
                for (BasicBlock bb : b.getPred()) {
                    if (bb.IDom != null) {
                        newIdom = bb;
                        break;
                    }
                }
                for (BasicBlock bb : b.getPred()) {
                    if (bb.IDom != null && bb != newIdom) {
                        newIdom = _intersect(newIdom, bb);
                    }
                }
                if (b.IDom != newIdom) {
                    b.IDom = newIdom;
                    flag = true;
                }
            }
        }
        for (BasicBlock b : allBlocks) {
            if (b == head)
                continue;
            b.IDom.children.add(b);
        }
    }

    public void calcDF() {
        List<BasicBlock> allBlocks = getrPostOrder();
        for (BasicBlock b : allBlocks) {
            b.DF = new HashSet<>();
        }

        for (BasicBlock b : allBlocks) {
            if (b.getPred().size() >= 2) {
                for (BasicBlock bb : b.getPred()) {
                    BasicBlock runner = bb;
                    while (runner != b.IDom) {
                        runner.DF.add(b);
                        runner = runner.IDom;
                    }
                }
            }
        }
    }

    public void updateCallee() {
        callee.clear();
        for (BasicBlock bb : getrPostOrder()) {
            for (Inst i = bb.getHead(); i != null; i = i.getSucc()) {
                if (i instanceof CALL) {
                    callee.add(((CALL)i).getFunc());
                }
            }
        }
    }

    public void addArg(VirtualReg a) {
        args.add(a);
    }

    public String getName() {
        return name;
    }

    public BasicBlock getHead() {
        return head;
    }

    public BasicBlock getTail() {
        return tail;
    }

    public FuncSymbol getFunc() {
        return func;
    }

    public List<BasicBlock> getrPostOrder() {
        if (rPostOrder == null)
            dfsPost();
        return rPostOrder;
    }

    public List<BasicBlock> getrPrevOrder() {
        if (rPrevOrder == null)
            dfsPrev();
        return rPrevOrder;
    }

    public void clearOrder() {
        rPostOrder.clear();
        rPrevOrder.clear();
        prevOrder.clear();
        rPostOrder = null;
        rPrevOrder = null;
        prevOrder = null;
    }

    public List<BasicBlock> getPrevOrder() {
        if (prevOrder == null)
            dfsPrev();
        return prevOrder;
    }

    public void setHead(BasicBlock head) {
        this.head = head;
    }

    public void setTail(BasicBlock tail) {
        this.tail = tail;
    }

    public void accept(IRVisitor v) {
        v.visit(this);
    }

    // built-in functions
    private static final Function PRINT = new Function(FuncSymbol.PRINT, 0);
    private static final Function PRINTLN = new Function(FuncSymbol.PRINTLN, 0);
    private static final Function PRINT_INT = new Function(FuncSymbol.PRINT_INT, 0);
    private static final Function PRINTLN_INT = new Function(FuncSymbol.PRINTLN_INT, 0);
    private static final Function GET_STRING = new Function(FuncSymbol.GET_STRING, 0);
    private static final Function GET_INT = new Function(FuncSymbol.GET_INT, 0);
    private static final Function TO_STRING = new Function(FuncSymbol.TO_STRING, 0);
    private static final Function STR_CONCAT = new Function(FuncSymbol.STR_CONCAT, 0);
    private static final Function STR_EQUAL = new Function(FuncSymbol.STR_EQUAL, 0);
    private static final Function STR_NOT_EQUAL = new Function(FuncSymbol.STR_NOT_EQUAL, 0);
    private static final Function STR_LESS = new Function(FuncSymbol.STR_LESS, 0);
    private static final Function STR_LTE = new Function(FuncSymbol.STR_LTE, 0);

    private static final Function LENGTH = new Function(FuncSymbol.LENGTH, 0);
    private static final Function PARSE_INT = new Function(FuncSymbol.PARSE_INT, 0);
    private static final Function ORD = new Function(FuncSymbol.ORD, 0);
    private static final Function SUBSTRING = new Function(FuncSymbol.SUBSTRING, 0);

    private static final Function SIZE = new Function(FuncSymbol.SIZE, 0);

    public static final List<Function> BUILTIN_FUNC = new ArrayList<>(Arrays.asList(PRINT, PRINTLN, PRINT_INT, PRINTLN_INT, GET_STRING, GET_INT, TO_STRING, STR_CONCAT, STR_EQUAL, STR_NOT_EQUAL, STR_LESS, STR_LTE, LENGTH, PARSE_INT, ORD, SUBSTRING, SIZE));
}
