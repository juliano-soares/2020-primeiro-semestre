## para rodar abra um terminal na pasta dos arquivos
sendo eles segundo.l, quinto.l, exemplo.c; rode o comando e veja o resultado no terminal.

## comandos
rodar o segundo.l:
> flex segundo.l && gcc -oexemplo lex.yy.c -ll && ./exemplo < exemplo.c

rodar o quinto.l:
> flex quinto.l && gcc -oexemplo lex.yy.c -ll && ./exemplo < exemplo.c

- isto usará o arquivo exemplo
> mas caso queira mudar troque o "exemplo.c" pelo que desejar.

## Possivel erro falta de biblioteca:
Ubuntu:
> sudo apt install flex
> sudo apt install gcc

## Os programas funcionam da seguinte forma:

você insere um arquivo e ele porcorre este arquivo escrevendo em terminal os tokens que foram encontrados

todos os tokens devem estar separados por espaço para o sistema detecta-los

por exemplo: 

entrada if { a = b }
retorno <IF> <D_CURLY> <VAR, "a"> <ATR> <VAR, "b"> <D_CURLY>

caso entrada if {a = b }
retorno <IF> <ATR> <VAR, "b"> <D_CURLY>

veja que os programas não reconhece {a juntos.
e tambem não reconhece muitas coisas kkk.


