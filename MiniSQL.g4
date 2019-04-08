grammar MiniSQL;

Name : [a-z]+ ;
Number : [0-9]+ ;
String : '\'' (.)+? '\''
	;

type : 'int'
     | 'long'
     | 'float'
     | 'double'
     | 'string' 
     ;

op : '='|'>'|'<'|'>='|'<='|'<>';

value : Number
      | String
	;

WS: [ \t\r\n]+ -> skip;

sql : 'create table' Name '(' schema ')' #create
    | 'drop table' Name #drop
    | 'insert into' Name 'values' '(' values ')' #insertA
    | 'insert into' Name '(' names ')' 'values' '(' values ')' #insertB
    | 'delete from' Name 'where' condition #delete
    | 'update' Name 'set' condition 'where' condition #update
    | 'select' names 'from' Name ('where' condition)? #selectA
    | 'select' cnames 'from' jnames 'on' onCondition ('where' condition)? #selectB
    ;
    

condition : Name op value
	;

names : Name (',' Name)*
	;
cname: Name'.'Name
	;
cnames : cname (',' cname)*
	;
onCondition : cname'='cname
	;
	
jnames : Name ('join' Name)+
	;

values : value (',' value)*
	;

schema : (attribute|constraint) (',' (attribute|constraint))* #attrcons
       ;

attribute : Name type #normalattr
          | Name type 'not null' #notnullattr
          ;

constraint :'primary key' '(' Name ')'#primarykey;

