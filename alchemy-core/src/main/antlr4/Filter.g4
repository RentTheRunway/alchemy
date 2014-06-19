grammar Filter;

LPAREN: '(';
RPAREN: ')';
AND: ('&' | 'and');
OR: ('|' | 'or');
NOT: ('!' | 'not');
IDENTIFIER: [A-Za-z0-9_-]+;
WS: [ \r\n\t]+ -> skip;

exp: term | exp OR term;
term: factor | factor AND term;
factor: '(' exp ')' | value | NOT factor;
value: IDENTIFIER;
