// Generated from MxStar.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MxStarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MxStarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MxStarParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MxStarParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#definitions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinitions(MxStarParser.DefinitionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#functionDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDef(MxStarParser.FunctionDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(MxStarParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#classDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDef(MxStarParser.ClassDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MxStarParser#nonVoidType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(MxStarParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MxStarParser#nonVoidType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonArrayType(MxStarParser.NonArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#basicType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicType(MxStarParser.BasicTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#voidType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoidType(MxStarParser.VoidTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(MxStarParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#paramDec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamDec(MxStarParser.ParamDecContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#varDecList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecList(MxStarParser.VarDecListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#varDec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDec(MxStarParser.VarDecContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#classMembers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassMembers(MxStarParser.ClassMembersContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#constructorDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDef(MxStarParser.ConstructorDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MxStarParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStmt(MxStarParser.BlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varDecStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecStmt(MxStarParser.VarDecStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(MxStarParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(MxStarParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code loopStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStmt(MxStarParser.LoopStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code jumpStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJumpStmt(MxStarParser.JumpStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blankStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlankStmt(MxStarParser.BlankStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#iffStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIffStmt(MxStarParser.IffStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#thenStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThenStmt(MxStarParser.ThenStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#elseStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseStmt(MxStarParser.ElseStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#looppStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLooppStmt(MxStarParser.LooppStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#forStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(MxStarParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(MxStarParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(MxStarParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code retStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRetStmt(MxStarParser.RetStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(MxStarParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#exprs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprs(MxStarParser.ExprsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpr(MxStarParser.NewExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisExpr(MxStarParser.ThisExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(MxStarParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code suffIncDecExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuffIncDecExpr(MxStarParser.SuffIncDecExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralExpr(MxStarParser.LiteralExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code memAccessExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemAccessExpr(MxStarParser.MemAccessExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpr(MxStarParser.BinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code innerExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInnerExpr(MxStarParser.InnerExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCallExpr(MxStarParser.FuncCallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayIndexExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayIndexExpr(MxStarParser.ArrayIndexExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdExpr(MxStarParser.IdExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newArrayObjExpr}
	 * labeled alternative in {@link MxStarParser#newObjExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewArrayObjExpr(MxStarParser.NewArrayObjExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newNonArrayObjExpr}
	 * labeled alternative in {@link MxStarParser#newObjExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewNonArrayObjExpr(MxStarParser.NewNonArrayObjExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MxStarParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(MxStarParser.LiteralContext ctx);
}