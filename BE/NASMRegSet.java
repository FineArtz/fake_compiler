// 2019-05-03

package BE;

import java.util.*;

public class NASMRegSet {
    //                                              id,     name,   gr,     er,     ee
    public static final NASMReg RAX = new NASMReg(0,    "rax",  false,  true,   false);
    public static final NASMReg RCX = new NASMReg(1,    "rcx",  false,  true,   false);
    public static final NASMReg RDX = new NASMReg(2,    "rdx",  false,  true,   false);
    public static final NASMReg RBX = new NASMReg(3,    "rbx",  false,  false,  true);
    public static final NASMReg RSI = new NASMReg(4,    "rsi",  false,  true,   false);
    public static final NASMReg RDI = new NASMReg(5,    "rdi",  false,  true,   false);
    public static final NASMReg RSP = new NASMReg(6,    "rsp",  false,  true,   false);
    public static final NASMReg RBP = new NASMReg(7,    "rbp",  false,  false,  true);
    public static final NASMReg R8  = new NASMReg(8,    "r8",   true,   true,   false);
    public static final NASMReg R9  = new NASMReg(9,    "r9",   true,   true,   false);
    public static final NASMReg R10 = new NASMReg(10,   "r10",  true,   true,   false);
    public static final NASMReg R11 = new NASMReg(11,   "r11",  true,   true,   false);
    public static final NASMReg R12 = new NASMReg(12,   "r12",  true,   false,  true);
    public static final NASMReg R13 = new NASMReg(13,   "r13",  true,   false,  true);
    public static final NASMReg R14 = new NASMReg(14,   "r14",  true,   false,  true);
    public static final NASMReg R15 = new NASMReg(15,   "r15",  true,   false,  true);

    public static final List<NASMReg> allRegs = new ArrayList<>(Arrays.asList(
            RAX, RCX, RDX, RBX, RSI, RDI, RSP, RBP, R8, R9, R10, R11, R12, R13, R14, R15
    ));

    public static final List<NASMReg> paramRegs = new ArrayList<>(Arrays.asList(
            RDI, RSI, RDX, RCX, R8, R9
    ));

    public static final Set<NASMReg> calleeSaveRegs = new HashSet<>(Arrays.asList(
            RBX, RBP, R12, R13, R14, R15
    ));

    public static final Set<NASMReg> callerSaveRegs = new HashSet<>(Arrays.asList(
            RAX, RCX, RDX, RSI, RDI, RSP, R8, R9, R10, R11
    ));
}
