// 2019-05-02
// modified from abcdabcd987's LLIRInterpreter

package FE;

import Err.SomeError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class IRInterpreter {
    private static class Inst {
        String op;
        String dest;
        String opx1;
        String opx2;
        int size;
        int offset;
        List<String> args;
        int line;
        String content;
    }

    private static class PhiNode extends Inst {
        HashMap<String, String> paths = new HashMap<>();

        PhiNode(Inst i) {
            op = i.op;
            dest = i.dest;
            opx1 = i.opx1;
            opx2 = i.opx2;
            size = i.size;
            offset = i.offset;
            args = i.args;
            line = i.line;
            content = i.content;
        }
    }

    private static class BasicBlock {
        String name;
        List<Inst> inst = new ArrayList<>();
        List<PhiNode> phiInst = new ArrayList<>();
    }

    private static class Function {
        String name;
        BasicBlock head;
        List<String> args;
        Map<String, BasicBlock> blocks = new HashMap<>();
        boolean hasReturnValue;
    }

    private static class Reg {
        int value;
        int stamp;
    }

    private static class StaticInt {
        int addr;
        int size;
    }

    private static class StaticString {
        String value;
    }

    private Map<String, Function> func = new HashMap<>();
    private BasicBlock nowBB = null;
    private Function nowFunc = null;
    private Inst nowInst = null;
    private boolean isSSA = true;

    // read instructions from IR

    static private final Set<String> noDestOp = new HashSet<>(Arrays.asList("store", "br", "jmp", "ret", "call"));
    static private final Set<String> hasDestOp = new HashSet<>(Arrays.asList("load", "MOV", "alloc", "phi", "ADD", "SUB", "MUL", "DIV", "MOD", "LSFT", "RSFT", "AND", "OR", "XOR", "NEG", "NOT", "LT", "GT", "LTE", "GTE", "EQ", "NEQ", "call"));
    static private final Set<String> oneOpxOp = new HashSet<>(Arrays.asList("ret", "jmp", "MOV", "NEG", "NOT", "alloc"));

    private int line = 0;
    private String content;
    private BufferedReader br;
    private boolean allowPhi;

    private String readline() throws IOException {
        do {
            content = br.readLine();
            if (content == null) {
                break;
            }
            ++line;
            content = content.trim();
        }
        while (content.equals(""));
        return content;
    }

    private List<String> splitSpace(String content) {
        return Arrays.asList(content.trim().split(" +"));
    }

    private void readInst() {
        if (content.startsWith("%")) {
            if (!content.endsWith(":")) {
                throw new SomeError(String.format("In IRI readInst: line %d, expected a \":\"", line));
            }
            nowBB = new BasicBlock();
            nowBB.name = content.substring(0, content.length() - 1);
            if (nowFunc.blocks.containsKey(nowBB.name)) {
                throw new SomeError(String.format("In IRI readInst: line %d, label \"%s\" has been defined", line, nowBB.name));
            }
            nowFunc.blocks.put(nowBB.name, nowBB);
            if (nowFunc.head == null) {
                nowFunc.head = nowBB;
            }
            allowPhi = isSSA;
            return;
        }

        String[] split = content.split("=");
        Inst inst = new Inst();
        List<String> words = splitSpace(split[split.length - 1]);
        inst.op = words.get(0);
        if (split.length == 1) {
            if (!noDestOp.contains(inst.op))
                throw new SomeError(String.format("In IRI readInst: line %d, illegal operator", line));
        }
        else if (split.length == 2) {
            if (!hasDestOp.contains(inst.op))
                throw new SomeError(String.format("In IRI readInst: line %d, illegal operator", line));
        }
        else {
            throw new SomeError(String.format("In IRI readInst: line %d, illegal operator", line));
        }
        inst.line = line;
        inst.content = content;
        if (!inst.op.equals("phi")) {
            allowPhi = false;
            nowBB.inst.add(inst);
        }
        switch (inst.op) {
            case "store":
                inst.opx1 = words.get(2);
                inst.opx2 = words.get(3);
                inst.size = Integer.valueOf(words.get(1));
                inst.offset = Integer.valueOf(words.get(4));
                break;
            case "load":
                inst.dest = split[0].trim();
                inst.opx1 = words.get(2);
                inst.size = Integer.valueOf(words.get(1));
                inst.offset = Integer.valueOf(words.get(3));
                break;
            case "alloc":
                inst.dest = split[0].trim();
                inst.opx1 = words.get(1);
                break;
            case "call":
                if (split.length == 2) {
                    inst.dest = split[0].trim();
                }
                inst.opx1 = words.get(1);
                inst.args = words.subList(2, words.size());
                break;
            case "br":
                inst.dest = words.get(1);
                inst.opx1 = words.get(2);
                inst.opx2 = words.get(3);
                break;
            case "phi":
                if (!allowPhi) {
                    throw new SomeError(String.format("In IRI readInst: line %d, phi is not allowed", line));
                }
                if ((words.size() & 1) == 0) {
                    throw new SomeError(String.format("In IRI readInst: line %d, phi must be even", line));
                }
                PhiNode phi = new PhiNode(inst);
                phi.dest = split[0].trim();
                for (int i = 1; i < words.size(); i += 2) {
                    String label = words.get(i);
                    String reg = words.get(i + 1);
                    if (!label.startsWith("%"))
                        throw new SomeError(String.format("In IRI readInst: line %d, label should starts with \"%%\"", line));
                    if (!reg.startsWith("$") && !reg.equals("undef"))
                        throw new SomeError(String.format("In IRI readInst: line %d, source should be register or \"undef\"", line));
                    phi.paths.put(label, reg);
                }
                nowBB.phiInst.add(phi);
                break;
            default:
                if (words.size() == 1) {
                    break;
                }
                if (split.length == 2) {
                    inst.dest = split[0].trim();
                }
                inst.opx1 = words.get(1);
                if (oneOpxOp.contains(inst.op)) {
                    break;
                }
                inst.opx2 = words.get(2);
                break;
        }
    }

    private void readFunction() throws IOException {
        List<String> words = splitSpace(content);
        if (!words.get(words.size() - 1).equals("{")) {
            throw new SomeError(String.format("In IRI readFunction: line %d, \"{\" expected", line));
        }
        nowFunc = new Function();
        nowFunc.hasReturnValue = content.startsWith("func ");
        nowFunc.name = words.get(1);
        nowFunc.args = words.subList(2, words.size() - 1);
        if (func.containsKey(nowFunc.name)) {
            throw new SomeError(String.format("In IRI readFunction: line %d, func %s has been defined", line, nowFunc.name));
        }
        func.put(nowFunc.name, nowFunc);
        allowPhi = isSSA;
        while (!readline().equals("}")) {
            readInst();
        }
    }

    private void SSACheck() {
        Set<String> regDef = new HashSet<>();
        for (Function f : func.values()) {
            regDef.clear();
            for (BasicBlock bb : f.blocks.values()) {
                for (Inst i : bb.inst) {
                    if (i.dest != null && !i.op.equals("br")) {
                        if (regDef.contains(i.dest)) {
                            line = i.line;
                            content = i.content;
                            throw new SomeError(String.format("In IRI SSACheck: line %d, register redefined", line));
                        }
                        else {
                            regDef.add(i.dest);
                        }
                    }
                }
            }
        }
    }

    // run IR

    private Map<String, Reg> regMap;
    private Map<String, Integer> tmpReg = new HashMap<>();
    private Map<Integer, Byte> memory = new HashMap<>();
    private int heapTop = (int) (Math.random() * 4096);
    private int retValue;
    private boolean ret;
    private int instCnt = 0;
    private BasicBlock lastBB = null;
    private final Set<String> jumpOp = new HashSet<>(Arrays.asList("br", "jmp", "ret"));

    private int readMemory(int addr) {
        Byte data = memory.get(addr);
        if (data == null) {
            throw new SomeError(String.format("In IRI readMemory: line %d, memory read error", nowInst.line));
        }
        return data & 0xFF;
    }

    private void writeMemory(int addr, Byte val) {
        if (!memory.containsKey(addr)) {
            throw new SomeError(String.format("In IRI readMemory: line %d, memory read error", nowInst.line));
        }
        memory.put(addr, val);
    }

    private int readReg(String name) {
        Reg reg = regMap.get(name);
        if (reg == null) {
            throw new SomeError(String.format("In IRI readReg: line %d, register not found", nowInst.line));
        }
        return reg.value;
    }

    private void writeReg(String name, Integer val) {
        if (!name.startsWith("$")) {
            throw new SomeError(String.format("In IRI writeReg: line %d, not a register", nowInst.line));
        }
        Reg reg = regMap.get(name);
        if (reg == null) {
            reg = new Reg();
            regMap.put(name, reg);
        }
        reg.value = val;
        reg.stamp = instCnt;
    }

    private int readSrc(String name) {
        if (name.startsWith("$")) {
            return readReg(name);
        }
        else {
            return Integer.valueOf(name);
        }
    }

    private void jump(String name) {
        BasicBlock bb = nowFunc.blocks.get(name);
        if (bb == null) {
            throw new SomeError(String.format("In IRI jump: line %d, block not found", nowInst.line));
        }
        lastBB = nowBB;
        nowBB = bb;
    }

    private void runInst() {
        switch (nowInst.op) {
            case "load": {
                int addr = readSrc(nowInst.opx1) + nowInst.offset;
                int data = 0;
                for (int i = 0; i < nowInst.size; ++i) {
                    data = (data << 8) | readMemory(addr + i);
                }
                writeReg(nowInst.dest, data);
                break;
            }
            case "store": {
                int addr = readSrc(nowInst.opx1) + nowInst.offset;
                int data = readSrc(nowInst.opx2);
                for (int i = nowInst.size - 1; i >= 0; --i) {
                    writeMemory(addr + i, (byte)(data & 0xFF));
                    data >>= 8;
                }
                break;
            }
            case "alloc": {
                int size = readSrc(nowInst.opx1);
                writeReg(nowInst.dest, heapTop);
                for (int i = 0; i < size; ++i) {
                    memory.put(heapTop + i, (byte)(Math.random() * 256));
                }
                heapTop += (int)(Math.random() * 4096);
                break;
            }
            case "ret": {
                retValue = (nowInst.opx1 == null ? 0 : readSrc(nowInst.opx1));
                ret = true;
                break;
            }
            case "br": {
                int cond = readSrc(nowInst.dest);
                jump(cond != 0 ? nowInst.opx1 : nowInst.opx2);
                break;
            }
            case "jmp": {
                jump(nowInst.opx1);
                break;
            }
            case "call": {
                Function f = func.get(nowInst.opx1);
                if (f == null) {
                    throw new SomeError(String.format("In IRI runInst: line %d, function not found", nowInst.line));
                }
                if (nowInst.dest != null && !f.hasReturnValue) {
                    throw new SomeError(String.format("In IRI runInst: line %d, function has no return value", nowInst.line));
                }
                Map<String, Reg> args = new HashMap<>();
                if (nowInst.args.size() != f.args.size()) {
                    throw new SomeError(String.format("In IRI runInst: line %d, argument number does not match", nowInst.line));
                }
                for (int i = 0; i < nowInst.args.size(); ++i) {
                    String name = f.args.get(i);
                    Reg reg = args.get(name);
                    if (reg == null) {
                        reg = new Reg();
                        args.put(name, reg);
                    }
                    reg.value = readSrc(nowInst.args.get(i));
                    reg.stamp = instCnt;
                }
                Map<String, Reg> tmpRegMap = regMap;
                BasicBlock tmpNowBB = nowBB;
                BasicBlock tmpLastBB = lastBB;
                Inst tmpNowInst = nowInst;
                Function tmpNowFunc = nowFunc;
                regMap = args;
                runFunction(f);
                ret = false;
                nowFunc = tmpNowFunc;
                nowInst = tmpNowInst;
                lastBB = tmpLastBB;
                nowBB = tmpNowBB;
                regMap = tmpRegMap;
                if (nowInst.dest != null) {
                    writeReg(nowInst.dest, retValue);
                }
                break;
            }
            case "DIV": {
                if (readSrc(nowInst.opx2) == 0) {
                    throw new SomeError(String.format("In IRI runInst: line %d, try to divide by zero", nowInst.line));
                }
                writeReg(nowInst.dest, readSrc(nowInst.opx1) / readSrc(nowInst.opx2));
                break;
            }
            case "MOD": {
                if (readSrc(nowInst.opx2) == 0) {
                    throw new SomeError(String.format("In IRI runInst: line %d, try to mod by zero", nowInst.line));
                }
                writeReg(nowInst.dest, readSrc(nowInst.opx1) % readSrc(nowInst.opx2));
                break;
            }
            case "MOV": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1));
                break;
            }
            case "NEG": {
                writeReg(nowInst.dest, -readSrc(nowInst.opx1));
                break;
            }
            case "NOT": {
                writeReg(nowInst.dest, ~readSrc(nowInst.opx1));
                break;
            }
            case "ADD": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) + readSrc(nowInst.opx2));
                break;
            }
            case "SUB": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) - readSrc(nowInst.opx2));
                break;
            }
            case "MUL": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) * readSrc(nowInst.opx2));
                break;
            }
            case "LSFT": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) << readSrc(nowInst.opx2));
                break;
            }
            case "RSFT": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) >> readSrc(nowInst.opx2));
                break;
            }
            case "AND": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) & readSrc(nowInst.opx2));
                break;
            }
            case "OR": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) | readSrc(nowInst.opx2));
                break;
            }
            case "XOR": {
                writeReg(nowInst.dest, readSrc(nowInst.opx1) ^ readSrc(nowInst.opx2));
                break;
            }
            case "LT": {
                writeReg(nowInst.dest, (readSrc(nowInst.opx1) < readSrc(nowInst.opx2) ? 1 : 0));
                break;
            }
            case "GT": {
                writeReg(nowInst.dest, (readSrc(nowInst.opx1) > readSrc(nowInst.opx2) ? 1 : 0));
                break;
            }
            case "LTE": {
                writeReg(nowInst.dest, (readSrc(nowInst.opx1) <= readSrc(nowInst.opx2) ? 1 : 0));
                break;
            }
            case "GTE": {
                writeReg(nowInst.dest, (readSrc(nowInst.opx1) >= readSrc(nowInst.opx2) ? 1 : 0));
                break;
            }
            case "EQ": {
                writeReg(nowInst.dest, (readSrc(nowInst.opx1) == readSrc(nowInst.opx2) ? 1 : 0));
                break;
            }
            case "NEQ": {
                writeReg(nowInst.dest, (readSrc(nowInst.opx1) != readSrc(nowInst.opx2) ? 1 : 0));
                break;
            }
            default:
                throw new SomeError(String.format("In IRI runInst: line %d, unexpected operator", nowInst.line));
        }
    }

    private void runFunction(Function f) {
        nowFunc = f;
        nowBB = f.head;
        if (nowBB == null) {
            throw new SomeError(String.format("In IRI runFunction: line %d, head block not found", nowInst.line));
        }
        while (true) {
            BasicBlock bb = nowBB;
            if (!jumpOp.contains(bb.inst.get(bb.inst.size() - 1).op)) {
                throw new SomeError(String.format("In IRI runFunction: line %d, the block has no end instruction", nowInst.line));
            }
            if (!nowBB.phiInst.isEmpty()) {
                ++instCnt;
                tmpReg.clear();
                for (PhiNode p : nowBB.phiInst) {
                    nowInst = p;
                    String regName = p.paths.get(lastBB.name);
                    if (regName == null) {
                        throw new SomeError(String.format("In IRI runFunction: line %d, no value from incoming block %s", nowInst.line, lastBB.name));
                    }
                    else {
                        int value = (regName.equals("undef") ? (int)(Math.random() * Integer.MAX_VALUE) : readSrc(regName));
                        tmpReg.put(p.dest, value);
                    }
                }
                for (Map.Entry<String, Integer> e : tmpReg.entrySet()) {
                    writeReg(e.getKey(), e.getValue());
                }
            }
            for (Inst i : bb.inst) {
                nowInst = i;
                runInst();
                if (ret) {
                    return;
                }
                if (nowBB != bb) {
                    break;
                }
            }
        }
    }

    public IRInterpreter(InputStream in, boolean SSA) throws IOException {
        isSSA = SSA;
        br = new BufferedReader(new InputStreamReader(in));
        while (readline() != null) {
            if (content.startsWith("func ") || content.startsWith("void ")) {
                readFunction();
            }
        }
        br.close();
        if (isSSA) {
            SSACheck();
        }
    }

    private int exitCode = -1;

    public int getExitCode() {
        return exitCode;
    }

    public void run() {
        Function main = func.get("main");
        if (main == null) {
            throw new SomeError("In IRI run: main not found");
        }
        regMap = new HashMap<>();
        runFunction(main);
        exitCode = retValue;
    }
}
