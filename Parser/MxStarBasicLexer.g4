// 2019-03-12
// Lexer of Mx*

lexer grammar MxStarBasicLexer;

// Reserved Keywords

BOOL : 'bool';
INT : 'int';
STRING : 'string';
NULL : 'null';
VOID : 'void';
TRUE : 'true';
FALSE : 'false';
IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
BREAK : 'break';
CONTINUE : 'continue';
RETURN : 'return';
NEW : 'new';
CLASS : 'class';
THIS : 'this';

// Basic

fragment DIGIT : [0-9];

fragment ALPHA : [a-zA-Z];

ID : ALPHA (ALPHA | DIGIT | '_')*;

INT_LITERAL : DIGIT+;

fragment ESC : '\\' [n"\\];

STR_LITERAL : '"' (ESC | .)*? '"';

BOOL_LITERAL : TRUE | FALSE ;

// Whitespace

WS : [ \t\r\n]+ -> skip;

// Comment

CM : '//' .*? '\n' -> skip;
