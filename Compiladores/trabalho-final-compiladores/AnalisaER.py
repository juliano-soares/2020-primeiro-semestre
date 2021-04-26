import ply.yacc as yacc
import ply.lex as lex

class NotRegExError(Exception):
    pass

#################### LEX ####################

# Lista de tokens
tokens = (
    'CHAR',
    'STAR',
    'PLUS',
    'OR',
    'LPAREN',
    'RPAREN',
    'VOID'
)

# Regras dos tokens
t_CHAR   = r'[a-zA-Z0-9]'
t_STAR   = r'\*'
t_PLUS   = r'\+'
t_OR     = r'\|'
t_LPAREN = r'\('
t_RPAREN = r'\)'
t_VOID   = r'\$'

# String com chars ignorados
t_ignore = ' '

def t_error(t):
    t.lexer.skip(1)
    raise NotRegExError('Expressão regular inválida (Char inválido: ' + t.value[0] + ')')

lexer = lex.lex()


#################### PARSE ####################

'''
Gramatica:
RE -> OR | RE_SIMPLE
OR -> RE "|" RE_SIMPLE
RE_SIMPLE -> RE_SIMPLE RE_BASE | RE_BASE
RE_BASE -> STAR | PLUS | PAREN | CHAR | e
STAR -> PAREN "*" | CHAR "*"
PLUS -> PAREN "+" | CHAR "+"
PAREN -> "(" RE ")"
CHAR -> [a-z A-Z 0-9]
'''

def p_re(p):
    ''' re : or
           | re_simple '''
    pass

def p_or(p):
    ''' or : re OR re_simple '''
    pass

def p_re_simple(p):
    ''' re_simple : re_simple re_base
                  | re_base '''
    pass

def p_re_base(p):
    ''' re_base : star
                | plus
                | paren
                | CHAR
                | VOID '''
    pass

def p_star(p):
    ''' star : paren STAR
             | CHAR STAR '''
    pass

def p_plus(p):
    ''' plus : paren PLUS
             | CHAR PLUS '''
    pass

def p_paren(p):
    ''' paren : LPAREN re RPAREN '''
    pass

def p_error(p):
    raise NotRegExError('Expressão regular inválida (Erro de sintaxe)')

parser = yacc.yacc()

#################### ####################

def ValidaER(regex, printErro=False):
    try:
        parser.parse(regex)
    except NotRegExError as e:
        if printErro: print(e)
        return False
    return True

if __name__ == '__main__':
    while True:
        regex = input('> ')
        if not regex: continue
        print(regex, 'eh' if ValidaER(regex) else 'NÃO é', 'uma expressão regular.')
