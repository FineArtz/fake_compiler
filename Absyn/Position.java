// 2019-03-23

package Absyn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.ParserRuleContext;

public class Position{
    public int line;
    public int col;
    public Interval intv;

    public Position(int line, int col){
        this.line = line;
        this.col = col;
        this.intv = null;
    }

    public Position(int line, int col, Interval intv){
        this.line = line;
        this.col = col;
        this.intv = intv;
    }

    public Position(Token token){
        this.line = token.getLine();
        this.col = token.getCharPositionInLine();
        this.intv = new Interval(token.getTokenIndex(), token.getTokenIndex());
    }

    public Position(ParserRuleContext ctx){
        this.line = ctx.start.getLine();
        this.col = ctx.start.getCharPositionInLine();
        this.intv = ctx.getSourceInterval();
    }

    public boolean less(Position p){
        return (line < p.line || (line == p.line && col < p.col));
    }

    @Override
    public String toString(){
        return String.format("(line: %d, col: %d)", line, col);
    }

    public Interval getInterval(){
        return intv;
    }
}
