grammar Filter;

AND: ('&' | 'and');
OR: ('|' | 'or');
NOT: ('!' | 'not');
NUMBER: ('-' | '+')? [0-9]+;
STRING: '"' ~('"')* '"';
BOOLEAN: ('true' | 'false');
IDENTIFIER: [A-Za-z_-][A-Za-z0-9_-]*;
COMPARISON: ('<' | '>' | '=' | '<>' | '!=' | '<=' | '>=');
WS: [ \r\n\t]+ -> skip;

exp: term | exp OR term;
term: factor | factor AND term;
factor: '(' exp ')' | value | comparison | NOT factor;
comparison: value COMPARISON value;
constant: BOOLEAN | NUMBER | STRING;
value: constant | IDENTIFIER;
