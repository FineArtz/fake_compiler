// 2019-05-03

package BE;

import java.util.*;

public class NASMRegSet {
    //                                              id,     name,   gr,     er,     ee
    public static final NASMReg RAX = new NASMReg(0,    "rax",  "eax",  false,  true,   false);
    public static final NASMReg RCX = new NASMReg(1,    "rcx",  "ecx",  false,  true,   false);
    public static final NASMReg RDX = new NASMReg(2,    "rdx",  "edx",  false,  true,   false);
    public static final NASMReg RBX = new NASMReg(3,    "rbx",  "ebx",  false,  false,  true);
    public static final NASMReg RSI = new NASMReg(4,    "rsi",  "esi",  false,  true,   false);
    public static final NASMReg RDI = new NASMReg(5,    "rdi",  "edi",  false,  true,   false);
    public static final NASMReg RSP = new NASMReg(6,    "rsp",  "esp",  false,  true,   false);
    public static final NASMReg RBP = new NASMReg(7,    "rbp",  "ebp",  false,  false,  true);
    public static final NASMReg R8  = new NASMReg(8,    "r8",   "r8d",  true,   true,   false);
    public static final NASMReg R9  = new NASMReg(9,    "r9",   "r9d",  true,   true,   false);
    public static final NASMReg R10 = new NASMReg(10,   "r10",  "r10d", true,   true,   false);
    public static final NASMReg R11 = new NASMReg(11,   "r11",  "r11d", true,   true,   false);
    public static final NASMReg R12 = new NASMReg(12,   "r12",  "r12d", true,   false,  true);
    public static final NASMReg R13 = new NASMReg(13,   "r13",  "r13d", true,   false,  true);
    public static final NASMReg R14 = new NASMReg(14,   "r14",  "r14d", true,   false,  true);
    public static final NASMReg R15 = new NASMReg(15,   "r15",  "r15d", true,   false,  true);

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
