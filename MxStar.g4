// 2019-03-12
// Grammar of Mx*

grammar MxStar;
import MxStarBasicLexer;

// general structure
program : definitions* EOF ;

definitions : functionDef | classDef | varDef ;

// definitions
functionDef : (nonVoidType | voidType) ID '(' paramList? ')' block ;

varDef : nonVoidType varDecList ';' ;

classDef : CLASS ID '{' classMembers* '}' ;

nonVoidType 
    : basicType # nonArrayType 
    | nonVoidType '[' ']' # arrayType 
    ;

basicType : INT | BOOL | STRING | ID ;

voidType : VOID ;

paramList : paramDec (',' paramList)* ;

paramDec : nonVoidType ID ;

varDecList : varDec (',' varDec)* ;

varDec : ID ('=' expr)? ;

classMembers : functionDef | constructorDef | varDef ;

constructorDef : ID '(' paramList? ')' block ;

// statement

block : '{' stmt* '}' ;

stmt
    : block # blockStmt
    | varDec # varDecStmt
    | expr ';' # exprStmt
    | iffStmt # ifStmt
    | looppStmt # loopStmt
    | jumppStmt # jumpStmt
    | ';' #blankStmt
    ;

iffStmt : IF '(' cond=expr ')' thenStmt (ELSE elseStmt)? ;

thenStmt : stmt;

elseStmt : stmt;

looppStmt : forStmt | whileStmt ;

forStmt : FOR '(' init=expr? ';' cond=expr? ';' step=expr? ')' stmt ;

whileStmt : WHILE '(' cond=expr ')' stmt ;

jumppStmt
    : CONTINUE ';' # continueStmt
    | RETURN expr? ';' # retStmt
    | BREAK ';' # breakStmt
    ;

// expression

exprs : expr (',' expr)* ;

expr
    // special expression
    : expr '(' exprs? ')' # funcCallExpr
    | expr '.' ID # memAccessExpr
    | expr op=('++' | '--') # suffIncDecExpr
    | arr=expr '[' index=expr ']' # arrayIndexExpr
    | NEW newObjExpr # newExpr 
    // logic and arithmetic expression
        // unary expr
        | <assoc=right> op=('++' | '--') expr # unaryExpr
        | <assoc=right> op=('+' | '-') expr # unaryExpr
        | <assoc=right> op='!' expr # unaryExpr
        | <assoc=right> op='~' expr # unaryExpr
        // binary expr
        | lhs=expr op=('*' | '/' | '%') rhs=expr # binaryExpr
        | lhs=expr op=('+' | '-') rhs=expr # binaryExpr
        | lhs=expr op=('<<' | '>>') rhs=expr # binaryExpr
        | lhs=expr op=('<' | '>' | '<=' | '>=') rhs=expr # binaryExpr
        | lhs=expr op=('==' | '!=' ) rhs=expr # binaryExpr
        | lhs=expr op='&' rhs=expr # binaryExpr
        | lhs=expr op='^' rhs=expr # binaryExpr
        | lhs=expr op='|' rhs=expr # binaryExpr
        | lhs=expr op='&&' rhs=expr # binaryExpr
        | lhs=expr op='||' rhs=expr # binaryExpr
        | <assoc=right> lhs=expr op='=' rhs=expr # binaryExpr
    // primary expression
    | ID # idExpr 
    | THIS # thisExpr
    | literal # literalExpr
    | '(' expr ')' # innerExpr
    ;

newObjExpr
    : basicType ('[' expr ']')+ ('[' ']')* # newArrayObjExpr
    | INT # newNonArrayObjExpr
    | STRING # newNonArrayObjExpr
    | BOOL # newNonArrayObjExpr
    | ID ('(' ')')? # newNonArrayObjExpr
    ;

literal : INT_LITERAL | STR_LITERAL | BOOL_LITERAL | NULL ;
