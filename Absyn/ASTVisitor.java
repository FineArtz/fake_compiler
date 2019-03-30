// 2019-03-24

package Absyn;

public interface ASTVisitor {
    void visit(Program p);

    void visit(FunctionDef fd);
    void visit(VarDef vd);
    void visit(VarDefList vl);
    void visit(ClassDef cd);

    void visit(BlockStmt bs);
    void visit(IfStmt is);
    void visit(ExprStmt es);
    void visit(ForStmt fs);
    void visit(WhileStmt ws);
    void visit(ContinueStmt cs);
    void visit(RetStmt rs);
    void visit(BreakStmt brs);

    void visit(FunCallExpr fce);
    void visit(MemAccessExpr mae);
    void visit(SufIncDecExpr side);
    void visit(ArrayIndexExpr aie);
    void visit(NewExpr ne);
    void visit(UnaryExpr ue);
    void visit(BinaryExpr be);
    void visit(IdExpr ie);
    void visit(ThisExpr te);
    void visit(IntExpr lie);
    void visit(StringExpr lse);
    void visit(BoolExpr lbe);
    void visit(NullExpr lne);

    void visit(Ty t);
}
