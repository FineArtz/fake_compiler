// 2019-03-24

package Absyn;

import Types.NULL;

abstract public class Expr extends Absyn {
    public Ty rtype = new Ty(new Position(0, 0), new NULL());
    public boolean lvalue = false;
}
