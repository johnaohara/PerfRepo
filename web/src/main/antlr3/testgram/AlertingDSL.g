grammar AlertingDSL;

options {
    language = Java;
    output = AST;
}

tokens {
    ANY;
}

@lexer::header {
  package org.perfrepo.web.alerting;
}

@parser::header {
  package org.perfrepo.web.alerting;
}

/* Forces parser to store an exception upon recognition error. These can be then retrieved via getErrors(), which is done
in ConditionCheckerImpl.parseTree(). Furthermore if any were encountered, IllegalArgumentExpression is thrown.
Normally, ANTLR recovers from recognition errors and prints them into console, then skips the problematic token and attempts to resync.
While it is handy, it might hurt in our case - having the parser 'strict' allows for much easier tree verification. */
@members {
    private List<String> errors = new java.util.LinkedList<String>();
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        errors.add(hdr + " " + msg);
    }
    public List<String> getErrors() {
        return errors;
    }
}

// Lexer rules - TOKENs:
WS : ( ' ' | '\t' | '\r' | '\n' )+ { $channel = HIDDEN; };

MULTIVALUE           : 'MULTIVALUE';
STRICT               : 'STRICT';
GROUPING             : 'GROUPING';
SELECT               : 'SELECT';
DEFINE               : 'DEFINE';
CONDITION            : 'CONDITION';
WHERE                : 'WHERE';
AND                  : 'AND';
LOGICAL_AND	         : '&&';
LAST                 : 'LAST';
ASSIGN               : '=';
EQUALITY             : '==';
INEQUALITY           : '!=';
OPEN_BRACKET         : '(';
CLOSE_BRACKET        : ')';
LTE                  : '<=';
LT                   : '<';
GT                   : '>';
GTE                  : '>=';
IN                   : 'IN';
AVG                  : 'AVG';
MIN                  : 'MIN';
MAX                  : 'MAX';
NUMBER_NOT_ONE       : ('0' | '2'..'9');
ONE                  : '1';
COMMA                : ',';
CHAR                 : ('a'..'z' | 'A'..'Z');
DECIMAL             : '\.';
HYPHEN	:	'-';
COLON	:	':';
NUMERICAL_OPERATOR : ('+' | '-' | '*' | '/' | '^' | '%');

// Parser rules:

// Required abstraction to retain only one rule with one EOF
expression : query_structure EOF;

query_structure :   condition define |
                    multivalue (strict)? condition define_single_var_no_grouping |
                    multivalue grouping condition define_grouping;


condition         : CONDITION^ multicondition ; //todo: need to split out numerical constructs and mathematical expressions!

multicondition	  : any_with_comparator (LOGICAL_AND any_with_comparator)*;

// Following lines are related to multivalue alerting only
multivalue        : MULTIVALUE^;
strict            : STRICT^;
grouping          : GROUPING^;

// Force only one variable in DEFINE part
define_single_var_no_grouping     : DEFINE^ assign_no_grouping;
// Force only grouping functions in DEFINE part
define_grouping   : DEFINE^ assign_sequence_grouping;

assign_no_grouping  :   any_char ASSIGN^ '('! multi_select ')'! |
                        any_char ASSIGN^ multi_select;
assign_sequence_grouping   : assign_grouping_avg  (COMMA! assign_grouping_avg)* |
                             assign_grouping_max  (COMMA! assign_grouping_max)* |
                             assign_grouping_min  (COMMA! assign_grouping_min)* ;

assign_grouping_avg : any_char ASSIGN^ avg;
assign_grouping_max : any_char ASSIGN^ max;
assign_grouping_min : any_char ASSIGN^ min;

// Following lines are related to singlevalue alerting only
define            : DEFINE^ assign_sequence;
assign_sequence   : assign  (COMMA! assign)*;

assign            : any_char ASSIGN^ OPEN_BRACKET! simple_select CLOSE_BRACKET! |
                    any_char ASSIGN^ simple_select |
                    any_char ASSIGN^ avg |
                    any_char ASSIGN^ max |
                    any_char ASSIGN^ min;

// Following lines are used in both, multi && single value alerting
avg               : AVG^ OPEN_BRACKET! multi_select CLOSE_BRACKET!;
max               : MAX^ OPEN_BRACKET! multi_select CLOSE_BRACKET!;
min               : MIN^ OPEN_BRACKET! multi_select CLOSE_BRACKET!;

simple_select     : (SELECT^ equals_where simple_last?) | (SELECT^ simple_last);

multi_select      : SELECT^ in_where |
                    SELECT^ equals_where multi_last? |
                    SELECT^ multi_last;

equals_where      : WHERE^ equality_condition (AND! equality_condition)*;
in_where          : WHERE^ in_condition;
simple_last       : LAST^ ONE;
multi_last        : LAST^ number | LAST^ number COMMA! number;

equality_condition  : any_char ASSIGN^ any_char |
                    any_char ASSIGN^ '"'! any_char (WS any_char)* '"'! |
                    any_char LTE^ any_char |
                    any_char LTE^ '"'! any_char '"'! |
                    any_char GTE^ any_char |
                    any_char GTE^ '"'! any_char '"'!;

in_condition      : any_char IN^ '('! any_char (COMMA! any_char)* ')'!;

number            : (NUMBER_NOT_ONE | ONE)* -> ANY[$text];
any_with_comparator   : ((CHAR | NUMBER_NOT_ONE | ONE | NUMERICAL_OPERATOR | DECIMAL )* (EQUALITY | INEQUALITY | ASSIGN | GTE | LTE | GT | LT)
                        ( OPEN_BRACKET | CLOSE_BRACKET | CHAR | NUMBER_NOT_ONE | ONE | WS | HYPHEN | COLON | NUMERICAL_OPERATOR | DECIMAL)*)   -> ANY[$text];
any_char          : (NUMBER_NOT_ONE | ONE | CHAR | WS | HYPHEN | COLON | DECIMAL )* -> ANY[$text];