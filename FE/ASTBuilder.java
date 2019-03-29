// 2019-03-27

package FE;

import Absyn.*;
import Parser.*;
import Err.SomeError;
import Types.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends MxStarBaseVisitor<Absyn> {

    public Program build(MxStarParser.ProgramContext ctx){
        return (Program) visit(ctx);
    }

    @Override
    public Absyn visitProgram(MxStarParser.ProgramContext ctx){
        List<Definitions> defs = new ArrayList<>();

        for (ParseTree child : ctx.definitions()){
            Absyn res = visit(child);
            if (res instanceof Definitions)
                defs.add((Definitions)res);
            else
                throw new SomeError(new Position(ctx), "In visitProgram: unexpected children type");
        }
        return new Program(new Position(ctx), defs);
    }

    @Override
    public Absyn visitDefinitions(MxStarParser.DefinitionsContext ctx){
        if (ctx.classDef() != null)
            return visit(ctx.classDef());
        else if (ctx.functionDef() != null)
            return visit(ctx.functionDef());
        else if (ctx.varDef() != null)
            return visit(ctx.varDef());
        else
            throw new SomeError(new Position(ctx), "In visitDefinitions: definitions can't found");
    }

    @Override
    public Absyn visitFunctionDef(MxStarParser.FunctionDefContext ctx){
        String name;
        List<VarDef> params = new ArrayList<>();
        Ty retTy;
        BlockStmt body;

        name = ctx.ID().getText();
        Absyn param;
        for (ParseTree p : ctx.paramList().paramDec()){
            param = visit(p);
            params.add((VarDef)param);
        }
        if (ctx.voidType() != null)
            retTy = null;
        else
            retTy = (Ty)visit(ctx.nonVoidType());
        body = (BlockStmt)visit(ctx.block());
        return new FunctionDef(new Position(ctx), name, params, retTy, body);
    }

    private Ty nowTy;
    @Override
    public Absyn visitVarDef(MxStarParser.VarDefContext ctx){
        nowTy = (Ty)visit(ctx.nonVoidType());
        return visit(ctx.varDecList());
    }

    @Override
    public Absyn visitArrayType(MxStarParser.ArrayTypeContext ctx){
        Ty elemTy = (Ty)visit(ctx.nonVoidType());
        return new Ty(new Position(ctx), new ARRAY(elemTy.type));
    }

    @Override
    public Absyn visitNonArrayType(MxStarParser.NonArrayTypeContext ctx){
        return visit(ctx.basicType());
    }

    @Override
    public Absyn visitBasicType(MxStarParser.BasicTypeContext ctx){
        if (ctx.INT() != null)
            return new Ty(new Position(ctx), new INT());
        else if (ctx.STRING() != null)
            return new Ty(new Position(ctx), new STRING());
        else if (ctx.BOOL() != null)
            return new Ty(new Position(ctx), new BOOL());
        else if (ctx.ID() != null)
            return new Ty(new Position(ctx), new CLASS(ctx.ID().getText()));
        else
            throw new SomeError(new Position(ctx), "In visitBasicType: unexpected type");
    }

    @Override
    public Absyn visitVoidType(MxStarParser.VoidTypeContext ctx){
        return new Ty(new Position(ctx), new VOID());
    }

    @Override
    public Absyn visitParamList(MxStarParser.ParamListContext ctx){
        return super.visitParamList(ctx);
    }

    @Override
    public Absyn visitParamDec(MxStarParser.ParamDecContext ctx){
        String name;
        Ty ty;
        Expr init;

        name = ctx.ID().getText();
        ty = (Ty)visit(ctx.nonVoidType());
        init = null;
        return new VarDef(new Position(ctx), name, ty, init);
    }

    @Override
    public Absyn visitVarDecList(MxStarParser.VarDecListContext ctx){
        List<VarDef> varDefs = new ArrayList<>();

        Absyn v;
        for (ParseTree p : ctx.varDec()){
            v = visit(p);
            if (v instanceof VarDef)
                varDefs.add((VarDef)v);
            else
                throw new SomeError(new Position(ctx), "In visitVarDecList: unexpected Absyn");
        }
        return new VarDefList(new Position(ctx), varDefs);
    }

    @Override
    public Absyn visitVarDec(MxStarParser.VarDecContext ctx){
        String name;
        Ty ty;
        Expr init;

        name = ctx.ID().getText();
        ty = nowTy;
        if (ctx.expr() != null)
            init = (Expr)visit(ctx.expr());
        else
            init = null;
        return new VarDef(new Position(ctx), name, ty, init);
    }

    @Override
    public Absyn visitClassMembers(MxStarParser.ClassMembersContext ctx){
        if (ctx.constructorDef() != null)
            return visit(ctx.constructorDef());
        else if (ctx.functionDef() != null)
            return visit(ctx.functionDef());
        else if (ctx.varDef() != null)
            return visit(ctx.varDef());
        else
            throw new SomeError(new Position(ctx), "In visitClassMembers: unexpected class member");
    }

    @Override
    public Absyn visitConstructorDef(MxStarParser.ConstructorDefContext ctx){
        String name;
        List<VarDef> params;
        Ty result;
        BlockStmt body;

        name = ctx.ID().getText();
        params = null;
        if (ctx.paramList() != null)
            throw new SomeError(new Position(ctx), "In visitConstructorDef: constructor must have no params");
        result = null;
        body = (BlockStmt)visit(ctx.block());
        return new FunctionDef(new Position(ctx), name, params, result, body);
    }

    @Override
    public Absyn visitBlock(MxStarParser.BlockContext ctx){
        List<Absyn> stmts = new ArrayList<>();
        List<VarDef> varDefs = new ArrayList<>();

        Absyn s;
        for (ParseTree p : ctx.stmt()){
            s = visit(p);
            if (s != null){
                if (s instanceof VarDefList)
                    varDefs.addAll(((VarDefList)s).varList);
                stmts.add(s);
            }
        }
        return new BlockStmt(new Position(ctx), stmts, varDefs);
    }

    @Override
    public Absyn visitBlockStmt(MxStarParser.BlockStmtContext ctx){
        return visit(ctx.block());
    }

    @Override
    public Absyn visitVarDefStmt(MxStarParser.VarDefStmtContext ctx){
        return visit(ctx.varDef());
    }

    @Override
    public Absyn visitExprStmt(MxStarParser.ExprStmtContext ctx){
        return visit(ctx.expr());
    }

    @Override
    public Absyn visitIfStmt(MxStarParser.IfStmtContext ctx){
        return visit(ctx.iffStmt());
    }

    @Override
    public Absyn visitLoopStmt(MxStarParser.LoopStmtContext ctx){
        return visit(ctx.looppStmt());
    }

    @Override
    public Absyn visitJumpStmt(MxStarParser.JumpStmtContext ctx){
        return visit(ctx.jumppStmt());
    }

    @Override
    public Absyn visitBlankStmt(MxStarParser.BlankStmtContext ctx){
        return null;
    }

    @Override
    public Absyn visitIffStmt(MxStarParser.IffStmtContext ctx){
        Expr x;
        Stmt y;
        Stmt z;

        x = (Expr)visit(ctx.cond);
        y = (Stmt)visit(ctx.thenStmt());
        if (ctx.elseStmt() != null)
            z = (Stmt)visit(ctx.elseStmt());
        else
            z = null;
        return new IfStmt(new Position(ctx), x, y, z);
    }

    @Override
    public Absyn visitThenStmt(MxStarParser.ThenStmtContext ctx){
        return visit(ctx.stmt());
    }

    @Override
    public Absyn visitElseStmt(MxStarParser.ElseStmtContext ctx){
        return visit(ctx.stmt());
    }

    @Override
    public Absyn visitLooppStmt(MxStarParser.LooppStmtContext ctx){
        if (ctx.forStmt() != null)
            return visit(ctx.forStmt());
        else if (ctx.whileStmt() != null)
            return visit(ctx.whileStmt());
        else
            throw new SomeError(new Position(ctx), "In visitLoopStmt: unexpected loop");
    }

    @Override
    public Absyn visitForStmt(MxStarParser.ForStmtContext ctx){
        Expr x;
        Expr y;
        Expr z;
        Stmt b;

        if (ctx.init != null)
            x = (Expr)visit(ctx.init);
        else
            x = null;
        if (ctx.cond != null)
            y = (Expr)visit(ctx.cond);
        else
            y = null;
        if (ctx.step != null)
            z = (Expr)visit(ctx.step);
        else
            z = null;
        b = (Stmt)visit(ctx.stmt());
        return new ForStmt(new Position(ctx), x, y, z, b);
    }

    @Override
    public Absyn visitWhileStmt(MxStarParser.WhileStmtContext ctx){
        Expr t;
        Stmt b;

        t = (Expr)visit(ctx.cond);
        b = (Stmt)visit(ctx.stmt());
        return new WhileStmt(new Position(ctx), t, b);
    }

    @Override
    public Absyn visitContinueStmt(MxStarParser.ContinueStmtContext ctx){
        return new ContinueStmt(new Position(ctx));
    }

    @Override
    public Absyn visitRetStmt(MxStarParser.RetStmtContext ctx){
        Expr expr;

        if (ctx.expr() != null)
            expr = (Expr)visit(ctx.expr());
        else
            expr = null;
        return new RetStmt(new Position(ctx), expr);
    }

    @Override
    public Absyn visitBreakStmt(MxStarParser.BreakStmtContext ctx){
        return new BreakStmt(new Position(ctx));
    }

    @Override
    public Absyn visitExprs(MxStarParser.ExprsContext ctx){
        return super.visitExprs(ctx);
    }

    @Override
    public Absyn visitNewExpr(MxStarParser.NewExprContext ctx){
        return visit(ctx.newObjExpr());
    }

    @Override
    public Absyn visitThisExpr(MxStarParser.ThisExprContext ctx){
        return new ThisExpr(new Position(ctx));
    }

    @Override
    public Absyn visitUnaryExpr(MxStarParser.UnaryExprContext ctx){
        int op;
        Expr expr;

        switch (ctx.op.getText()){
            case "++": op = 0; break;
            case "--": op = 1; break;
            case "+": op = 2; break;
            case "-": op = 3; break;
            case "!": op = 4; break;
            case "~": op = 5; break;
            default:
                throw new SomeError(new Position(ctx), "In visitUnaryExpr: unexpected unary operation");
        }
        expr = (Expr)visit(ctx.expr());
        return new UnaryExpr(new Position(ctx), op, expr);
    }

    @Override
    public Absyn visitSuffIncDecExpr(MxStarParser.SuffIncDecExprContext ctx){
        Expr expr;
        int op;

        expr = (Expr)visit(ctx.expr());
        switch (ctx.op.getText()){
            case "++": op = 0; break;
            case "--": op = 1; break;
            default:
                throw new SomeError(new Position(ctx), "In visitSuffIncDecExpr: unexpected unary operation");
        }
        return new SufIncDecExpr(new Position(ctx), expr, op);
    }

    @Override
    public Absyn visitLiteralExpr(MxStarParser.LiteralExprContext ctx){
        return visit(ctx.literal());
    }

    @Override
    public Absyn visitMemAccessExpr(MxStarParser.MemAccessExprContext ctx){
        Expr expr;
        String mem;

        expr = (Expr)visit(ctx.expr());
        mem = ctx.ID().getText();
        return new MemAccessExpr(new Position(ctx), expr, mem);
    }

    @Override
    public Absyn visitBinaryExpr(MxStarParser.BinaryExprContext ctx){
        Absyn.BinaryExpr.OP op;
        Expr l;
        Expr r;

        switch (ctx.op.getText()){
            case "*": op = BinaryExpr.OP.MUL; break;
            case "/": op = BinaryExpr.OP.DIV; break;
            case "%": op = BinaryExpr.OP.MOD; break;
            case "+": op = BinaryExpr.OP.ADD; break;
            case "-": op = BinaryExpr.OP.SUB; break;
            case "<<": op = BinaryExpr.OP.SLA; break;
            case ">>": op = BinaryExpr.OP.SRA; break;
            case "<": op = BinaryExpr.OP.LES; break;
            case ">": op = BinaryExpr.OP.GRT; break;
            case "<=": op = BinaryExpr.OP.LTE; break;
            case ">=": op = BinaryExpr.OP.GTE; break;
            case "==": op = BinaryExpr.OP.EQL; break;
            case "!=": op = BinaryExpr.OP.NEQ; break;
            case "&": op = BinaryExpr.OP.BAND; break;
            case "^": op = BinaryExpr.OP.BXOR; break;
            case "|": op = BinaryExpr.OP.BOR; break;
            case "&&": op = BinaryExpr.OP.LAND; break;
            case "||": op = BinaryExpr.OP.LOR; break;
            case "=": op = BinaryExpr.OP.ASS; break;
            default:
                throw new SomeError(new Position(ctx), "In visitBinaryExpr: unexpected binary operation");
        }
        l = (Expr)visit(ctx.lhs);
        r = (Expr)visit(ctx.rhs);
        return new BinaryExpr(new Position(ctx), op, l, r);
    }

    @Override
    public Absyn visitInnerExpr(MxStarParser.InnerExprContext ctx){
        return visit(ctx.expr());
    }

    @Override
    public Absyn visitFuncCallExpr(MxStarParser.FuncCallExprContext ctx){
        Expr func;
        List<Expr> args = new ArrayList<>();

        func = (Expr)visit(ctx.expr());
        Absyn v;
        for (ParseTree p : ctx.exprs().expr()){
            v = visit(p);
            if (v instanceof Expr)
                args.add((Expr) v);
            else
                throw new SomeError(new Position(ctx), "In visitFuncCallExpr: unexpected argument");
        }
        return new FunCallExpr(new Position(ctx), func, args);
    }

    @Override
    public Absyn visitArrayIndexExpr(MxStarParser.ArrayIndexExprContext ctx){
        Expr arr;
        Expr id;

        arr = (Expr)visit(ctx.arr);
        id = (Expr)visit(ctx.index);
        return new ArrayIndexExpr(new Position(ctx), arr, id);
    }

    @Override
    public Absyn visitIdExpr(MxStarParser.IdExprContext ctx){
        String id;

        id = ctx.ID().getText();
        return new IdExpr(new Position(ctx), id);
    }

    @Override
    public Absyn visitNewArrayObjExpr(MxStarParser.NewArrayObjExprContext ctx){
        Ty elem;
        int dim;
        List<Expr> exprs = new ArrayList<>();

        elem = (Ty)visit(ctx.basicType());
        Absyn expr;
        for (ParseTree p : ctx.expr()){
            expr = visit(p);
            if (expr instanceof Expr)
                exprs.add((Expr)expr);
            else
                throw new SomeError(new Position(ctx), "In visitNewArrayObjExpr: unexpected dim expr");
        }
        dim = (ctx.getChildCount() - exprs.size() - 1) / 2;
        for (int i = 0; i < dim; ++i)
            elem.type = new ARRAY(elem.type);
        return new NewExpr(new Position(ctx), elem, dim, exprs);
    }

    @Override
    public Absyn visitNewNonArrayObjExpr(MxStarParser.NewNonArrayObjExprContext ctx){
        Ty elem;

        if (ctx.INT() != null)
            elem = new Ty(new Position(ctx), new INT());
        else if (ctx.BOOL() != null)
            elem = new Ty(new Position(ctx), new BOOL());
        else if (ctx.STRING() != null)
            elem = new Ty(new Position(ctx), new STRING());
        else if (ctx.ID() != null)
            elem = new Ty(new Position(ctx), new CLASS(ctx.ID().getText()));
        else
            throw new SomeError(new Position(ctx), "In visitNewNonArrayObjExpr: unexpected type");
        return new NewExpr(new Position(ctx), elem);
    }

    @Override
    public Absyn visitLiteral(MxStarParser.LiteralContext ctx){
        if (ctx.INT_LITERAL() != null){
            int v;

            try {
                v = Integer.parseInt(ctx.INT_LITERAL().getText());
            }
            catch (Exception e){
                throw new SomeError(new Position(ctx), "In visitLiteral: illegal int literal");
            }
            return new IntExpr(new Position(ctx), v);
        }
        else if (ctx.BOOL_LITERAL() != null){
            boolean v;

            String vv = ctx.BOOL_LITERAL().getText();
            if (vv.equals("true"))
                v = true;
            else if (vv.equals("false"))
                v = false;
            else
                throw new SomeError(new Position(ctx), "In visitLiteral: illegal bool literal");
            return new BoolExpr(new Position(ctx), v);
        }
        else if (ctx.STR_LITERAL() != null){
            String v;

            String vv = ctx.STR_LITERAL().getText();
            StringBuilder s = new StringBuilder();
            for (int i = 1; i < vv.length() - 1; ++i){
                if (i < vv.length() - 2 && vv.charAt(i) == '\\'){
                    char c = vv.charAt(i + 1);
                    if (c == '\n')
                        s.append('\n');
                    else if (c == '\"')
                        s.append('\"');
                    else if (c == '\\')
                        s.append('\\');
                    else
                        throw new SomeError(new Position(ctx), "In visitLiteral: unexpected escaped letter");
                    ++i;
                }
                else
                    s.append(vv.charAt(i));
            }
            v = s.toString();
            return new StringExpr(new Position(ctx), v);
        }
        else if (ctx.NULL() != null){
            return new NullExpr(new Position(ctx));
        }
        else
            throw new SomeError(new Position(ctx), "In visitLiteral: unexpected literal");
    }
}
