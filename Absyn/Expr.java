// 2019-03-24

package Absyn;

import IR.BasicBlock;
import IR.Reg;
import Types.NULL;

abstract public class Expr extends Absyn {
    public Ty rtype = new Ty(new Position(0, 0), new NULL());
    public boolean lvalue = false;

    // for IR
    public BasicBlock trueBB;
    public BasicBlock falseBB;

    public Reg value;
    public Reg addr;
    public int offset;
}
