// 2019-04-27

package IR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IRRoot {
    public Map<String, Function> funcs = new HashMap<>();
    public Map<String, Function> builtinFuncs = new HashMap<>();
    public Map<String, StaticString> strs = new HashMap<>();
    public List<StaticData> data = new ArrayList<>();
    public PhysicalReg pr1 = null, pr2 = null;

    private void initBuiltinFuncs() {
        for (Function f : Function.BUILTIN_FUNC) {
            if (f.getFunc().pname != null) {
                f.setName(f.getFunc().pname + "." + f.getName());
            }
            builtinFuncs.put(f.getName(), f);
        }
    }

    public IRRoot() {
        initBuiltinFuncs();
    }

    public void updateRCallee() {
        for (Function f : funcs.values())
            f.rcallee.clear();

        Set<Function> rcs = new HashSet<>();
        boolean a = false;
        boolean flag = true;

        while (flag) {
            flag = false;
            for (Function f : funcs.values()) {
                rcs.clear();
                a = false;
                rcs.addAll(f.callee);
                for (Function ff : f.callee) {
                    rcs.addAll(ff.rcallee);
                    if (ff.hasALLOC) {
                        a = true;
                    }
                }
                if (!rcs.equals(f.rcallee)) {
                    flag = true;
                    f.rcallee.clear();
                    f.rcallee.addAll(rcs);
                }
                if (!f.hasALLOC && a) {
                    flag = true;
                    f.hasALLOC = true;
                }
            }
        }
    }

    public void accept(IRVisitor v) {
        v.visit(this);
    }

    public static class ForStruct {
        public BasicBlock c, s, b, f;

        public ForStruct(BasicBlock cc, BasicBlock ss, BasicBlock bb, BasicBlock ff) {
            c = cc;
            s = ss;
            b = bb;
            f = ff;
        }
    }
    public Set<ForStruct> fors = new HashSet<>();

    public boolean canUseRBX = true;
}
