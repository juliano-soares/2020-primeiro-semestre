%{
    #include <stdio.h>
%}

DIGIT [0-9]
CHARACTERS [a-zA-Z]+
ALPHABET [a-zA-Z]
ALPHANUMERIC [A-Z0-9]
NUM_INTEGER -?{DIGIT}+
NUM_FLOAT -?[0-9]*\.[0-9]+

INT int
FOR for
WHILE while
IF if
FLOAT float

SUM "+"
SUBTRACT "-"
DIVISION "/"
MULTIPLICATION "*"

NOT_EQUAL "!="
AND "&&"
EQUAL "=="
ME_EQUAL "<"
MA_EQUAL ">"

STRING ["].*.["]
NOMEVAR [a-z\_][a-z0-9\_]*

D_QUOT_MARKS ["]
D_QUOT_MARKS_SIMPLE [']

D_PARENT [(]|[)]
D_CURLY [{]|[}]
D_COL  [[]|[]]
%%

{NUM_FLOAT} { printf("<FLOAT> ");};
{NUM_INTEGER} { printf("<INTEGER> ");};


{NOT_EQUAL} { printf("<NOT_EQUAL> ");};
{EQUAL} { printf("<EQUAL> ");};
{AND} { printf("<AND> ");};
{ME_EQUAL} { printf("<ME_EQUAL> ");};
{MA_EQUAL} { printf("<MA_EQUAL> ");};

{SUM} { printf("<SUM> ");};
{SUBTRACT} { printf("<SUBTRACT> ");};
{DIVISION} { printf("<DIVISION> ");};
{MULTIPLICATION} { printf("<MULTIPLI> ");};

{FOR} { printf("<FOR> ");}
{IF} { printf("<IF> ");}
{WHILE} { printf("<WHILE> ");}
{INT} { printf("<INT> ");}
{FLOAT} { printf("<FLOAT> ");}

{D_QUOT_MARKS} { printf("<D_QUOT_MARKS> ");}
{D_QUOT_MARKS_SIMPLE} { printf("<D_QUOT_MARKS_SIMPLE> ");}
{D_PARENT}  { printf("<D_PARENT> ");}
{D_CURLY} { printf("<D_CURLY> ");}
{D_COL}  { printf("<D_COL> ");}


{STRING} { printf("<STRING,'%s'> ", yytext);}
{NOMEVAR} { printf("<VAR,'%s'> ", yytext);}

[\n] { printf("\n");}
[[:space:]] ;
[[:^space:]]+ ;
%%
int main() {
    yylex();
    return 0;
}