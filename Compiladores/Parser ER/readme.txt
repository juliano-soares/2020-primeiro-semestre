ALUNOS:
Lana Bertoldo Rossato
Juliano Leonardo Soares
Rafael Vales Bettker

Execução do código:
$ python3 main.py -regex "(a*b)+"

Passos para a execução:
- Definir uma expressão regular, onde o símbolo de ^+ é + e o símbolo do vazio é $.
- Entrar com a expressão regular na linha de comando ao executar o código, entre
aspas no argumento -regex, conforme descrito acima.
- Caso a ER seja válida, entre com palavras que serão testadas nela.
- Ctrl+c para encerrar a execução.

Dependências:
Biblioteca PLY (implementação do LEX/YACC em python). Os arquivos necessários
para essa lib já estão na pasta ply, nada adicional é necessário.

Plataforma:
Testado em Windows 10
Python 3.8.2

Gramática para especificação da ER:
No arquivo "AnalisaER.py", que corresponde ao LEX/YACC

Arquivos:
- ply/*: Implementação do LEX/YACC em Python
- main.py: Arquivo principal que deve ser chamado para execução do programa.
Contém a leitura da ER via arguemnto; envia para verificar se é válida; caso
seja válida, faz a entrada de palavras que serão testadas na ER.
- AnalisaER.py: Arquivo do LEX/YACC que contém a especificação da gramática da ER.
- AnalisaPalavra.py: Arquivo para conversão da expressão regular em automato, e
validação de palavras.
