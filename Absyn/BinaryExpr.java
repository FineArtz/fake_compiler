// 2019-03-24

package Absyn;

public class BinaryExpr extends Expr {
    public enum OP {
        MUL,    // *
        DIV,    // /
        MOD,    // %
        ADD,    // +
        SUB,    // -
        SLA,    // <<
        SRA,    // >>
        LES,    // <
        GRT,    // >
        LTE,    // <=
        GTE,    // >=
        EQL,    // ==
        NEQ,    // !=
        BAND,   // &
        BXOR,   // ^
        BOR,    // |
        LAND,   // &&
        LOR,    // ||
        ASS     // =
    }
    public OP op;
    public Expr lhs;
    public Expr rhs;

    public BinaryExpr(Position p, OP o, Expr x, Expr y){
        pos = p;
        op = o;
        lhs = x;
        rhs = y;
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
