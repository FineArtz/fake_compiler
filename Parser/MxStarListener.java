// Generated from MxStar.g4 by ANTLR 4.7.2

    package Parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxStarParser}.
 */
public interface MxStarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxStarParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MxStarParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MxStarParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#definitions}.
	 * @param ctx the parse tree
	 */
	void enterDefinitions(MxStarParser.DefinitionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#definitions}.
	 * @param ctx the parse tree
	 */
	void exitDefinitions(MxStarParser.DefinitionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#functionDef}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDef(MxStarParser.FunctionDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#functionDef}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDef(MxStarParser.FunctionDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(MxStarParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(MxStarParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#classDef}.
	 * @param ctx the parse tree
	 */
	void enterClassDef(MxStarParser.ClassDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#classDef}.
	 * @param ctx the parse tree
	 */
	void exitClassDef(MxStarParser.ClassDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MxStarParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(MxStarParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MxStarParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(MxStarParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MxStarParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayType(MxStarParser.NonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MxStarParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayType(MxStarParser.NonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#basicType}.
	 * @param ctx the parse tree
	 */
	void enterBasicType(MxStarParser.BasicTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#basicType}.
	 * @param ctx the parse tree
	 */
	void exitBasicType(MxStarParser.BasicTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#voidType}.
	 * @param ctx the parse tree
	 */
	void enterVoidType(MxStarParser.VoidTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#voidType}.
	 * @param ctx the parse tree
	 */
	void exitVoidType(MxStarParser.VoidTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(MxStarParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(MxStarParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#paramDec}.
	 * @param ctx the parse tree
	 */
	void enterParamDec(MxStarParser.ParamDecContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#paramDec}.
	 * @param ctx the parse tree
	 */
	void exitParamDec(MxStarParser.ParamDecContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#varDecList}.
	 * @param ctx the parse tree
	 */
	void enterVarDecList(MxStarParser.VarDecListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#varDecList}.
	 * @param ctx the parse tree
	 */
	void exitVarDecList(MxStarParser.VarDecListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#varDec}.
	 * @param ctx the parse tree
	 */
	void enterVarDec(MxStarParser.VarDecContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#varDec}.
	 * @param ctx the parse tree
	 */
	void exitVarDec(MxStarParser.VarDecContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#classMembers}.
	 * @param ctx the parse tree
	 */
	void enterClassMembers(MxStarParser.ClassMembersContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#classMembers}.
	 * @param ctx the parse tree
	 */
	void exitClassMembers(MxStarParser.ClassMembersContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#constructorDef}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDef(MxStarParser.ConstructorDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#constructorDef}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDef(MxStarParser.ConstructorDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MxStarParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MxStarParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterBlockStmt(MxStarParser.BlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitBlockStmt(MxStarParser.BlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varDefStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterVarDefStmt(MxStarParser.VarDefStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varDefStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitVarDefStmt(MxStarParser.VarDefStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(MxStarParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(MxStarParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(MxStarParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(MxStarParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code loopStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterLoopStmt(MxStarParser.LoopStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code loopStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitLoopStmt(MxStarParser.LoopStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jumpStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterJumpStmt(MxStarParser.JumpStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code jumpStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitJumpStmt(MxStarParser.JumpStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blankStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterBlankStmt(MxStarParser.BlankStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blankStmt}
	 * labeled alternative in {@link MxStarParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitBlankStmt(MxStarParser.BlankStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#iffStmt}.
	 * @param ctx the parse tree
	 */
	void enterIffStmt(MxStarParser.IffStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#iffStmt}.
	 * @param ctx the parse tree
	 */
	void exitIffStmt(MxStarParser.IffStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#thenStmt}.
	 * @param ctx the parse tree
	 */
	void enterThenStmt(MxStarParser.ThenStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#thenStmt}.
	 * @param ctx the parse tree
	 */
	void exitThenStmt(MxStarParser.ThenStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#elseStmt}.
	 * @param ctx the parse tree
	 */
	void enterElseStmt(MxStarParser.ElseStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#elseStmt}.
	 * @param ctx the parse tree
	 */
	void exitElseStmt(MxStarParser.ElseStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#looppStmt}.
	 * @param ctx the parse tree
	 */
	void enterLooppStmt(MxStarParser.LooppStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#looppStmt}.
	 * @param ctx the parse tree
	 */
	void exitLooppStmt(MxStarParser.LooppStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(MxStarParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(MxStarParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(MxStarParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(MxStarParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(MxStarParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(MxStarParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code retStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 */
	void enterRetStmt(MxStarParser.RetStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code retStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 */
	void exitRetStmt(MxStarParser.RetStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(MxStarParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link MxStarParser#jumppStmt}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(MxStarParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#exprs}.
	 * @param ctx the parse tree
	 */
	void enterExprs(MxStarParser.ExprsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#exprs}.
	 * @param ctx the parse tree
	 */
	void exitExprs(MxStarParser.ExprsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(MxStarParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(MxStarParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterThisExpr(MxStarParser.ThisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitThisExpr(MxStarParser.ThisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(MxStarParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(MxStarParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code suffIncDecExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSuffIncDecExpr(MxStarParser.SuffIncDecExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code suffIncDecExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSuffIncDecExpr(MxStarParser.SuffIncDecExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code literalExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpr(MxStarParser.LiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code literalExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpr(MxStarParser.LiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memAccessExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMemAccessExpr(MxStarParser.MemAccessExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memAccessExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMemAccessExpr(MxStarParser.MemAccessExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(MxStarParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(MxStarParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code innerExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInnerExpr(MxStarParser.InnerExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code innerExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInnerExpr(MxStarParser.InnerExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpr(MxStarParser.FuncCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpr(MxStarParser.FuncCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayIndexExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArrayIndexExpr(MxStarParser.ArrayIndexExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayIndexExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArrayIndexExpr(MxStarParser.ArrayIndexExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIdExpr(MxStarParser.IdExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MxStarParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIdExpr(MxStarParser.IdExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newArrayObjExpr}
	 * labeled alternative in {@link MxStarParser#newObjExpr}.
	 * @param ctx the parse tree
	 */
	void enterNewArrayObjExpr(MxStarParser.NewArrayObjExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newArrayObjExpr}
	 * labeled alternative in {@link MxStarParser#newObjExpr}.
	 * @param ctx the parse tree
	 */
	void exitNewArrayObjExpr(MxStarParser.NewArrayObjExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newNonArrayObjExpr}
	 * labeled alternative in {@link MxStarParser#newObjExpr}.
	 * @param ctx the parse tree
	 */
	void enterNewNonArrayObjExpr(MxStarParser.NewNonArrayObjExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newNonArrayObjExpr}
	 * labeled alternative in {@link MxStarParser#newObjExpr}.
	 * @param ctx the parse tree
	 */
	void exitNewNonArrayObjExpr(MxStarParser.NewNonArrayObjExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxStarParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(MxStarParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxStarParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(MxStarParser.LiteralContext ctx);
}