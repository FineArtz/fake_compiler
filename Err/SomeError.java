// 2019-03-24

package Err;

import Absyn.Position;

public class SomeError extends Error {
    public SomeError(String m){
        super(m);
    }
    public SomeError(Position p, String m){
        super(String.format("At Line %d, Col %d: %s", p.line, p.col, m));
    }
}
