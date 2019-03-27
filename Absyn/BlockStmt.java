// 2019-03-24

package Absyn;

import Scope.*;

import java.util.List;

public class BlockStmt extends Stmt {
    public List<Absyn> stmts;
    public List<VarDef> varDefs;
    public LocalScope scope;
    // Attention: A BlockStmt begins a new scope!

    public BlockStmt(Position p, List<Absyn> s, List<VarDef> v){
        pos = p;
        stmts = s;
        varDefs = v;
    }

    public void setNewScope(Scope p){
        scope = new LocalScope(p);
    }

    @Override
    public void accept(ASTVisitor v){
        v.visit(this);
    }
}
