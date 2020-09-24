# AnalisaPalavra.py

# Lana Bertoldo Rossato, Juliano Leonardo Soares, Rafael Vales Bettker

# Estado do automato
class Estado:
    def __init__(self, id):
        self.id = id
        self.final = False
        self.entrada = []
        self.saida = []

# Ligacao de dois estados do automato
class Ligacao:
    def __init__(self, de, para, token):
        self.de = de
        self.para = para
        self.token = token

        de.saida.append(self)
        para.entrada.append(self)

class AnalisaPalavra:
    estado_id = -1

    todos_estados = []
    todas_ligacoes = []

    def __init__(self, regex):
        self.criaAFN(regex)
    
    def criaAFN(self, regex):
        regex_str = regex
        # Remove espacos
        regex = list(filter(lambda a: a != ' ', regex))

        regex = self.separaOR(regex)

        self.estado_id += 1
        self.estado_inicial = Estado(self.estado_id)
        self.todos_estados.append(self.estado_inicial)
    
        self.calculaEstado(regex, self.estado_inicial)

        # Ultimo estado eh o final
        self.todos_estados[-1].final = True

        print('Automato para '+ regex_str +':')
        for e in self.todos_estados:
            print('Estado', e.id, end=' | ')
            print('Entradas:', end='')
            for ent in e.entrada:
                print(' ('+ str(ent.de.id) +','+ str(ent.para.id) +','+ ent.token +')', end='')
            print(' | Saidas:', end='')
            for ent in e.saida:
                print(' ('+ str(ent.de.id) +','+ str(ent.para.id) +','+ ent.token +')', end='')
            print()
        print("Inicial:", self.estado_inicial.id, "| Final:", self.todos_estados[-1].id, '\n')

    def calculaEstado(self, regex, estado_anterior):
        # Casos base
        if isinstance(regex, str):
            # 'a'
            if len(regex) == 1:
                self.estado_id += 1
                novo_estado = Estado(self.estado_id)
                self.todos_estados.append(novo_estado)
                l = Ligacao(estado_anterior, novo_estado, regex[0])
                self.todas_ligacoes.append(l)
                return novo_estado

            # 'a*' ou 'a+'
            elif len(regex) == 2 and (regex[1] == '*' or regex[1] == '+'):
                self.estado_id += 1
                novo_estado = Estado(self.estado_id)
                self.todos_estados.append(novo_estado)

                if regex[1] == '*':
                    l = Ligacao(estado_anterior, novo_estado, '$')
                else: #elif regex[1] == '+':
                    l = Ligacao(estado_anterior, novo_estado, regex[0])
                self.todas_ligacoes.append(l)
                
                l = Ligacao(novo_estado, novo_estado, regex[0])
                self.todas_ligacoes.append(l)
                
                return novo_estado

            # 'ab' / 'a*b' / 'aabbbaa'...
            else:
                estado = estado_anterior
                while len(regex) > 0:
                    if len(regex) > 1 and (regex[1] == '*' or regex[1] == '+'):
                        w = regex[:2]
                        regex = regex[2:]
                    else:
                        w = regex[:1]
                        regex = regex[1:]

                    estado = self.calculaEstado(w, estado)
                return estado


        # Se tem + ou +, torna o booleano correspondente true
        plus = regex[0] == '+'
        star = regex[0] == '*'
        if plus or star:
            regex = regex[1:]

        # Se eh ['a', 'b', ...] transforma em 'ab...' e chama recursao pra ser tratado ali em cima
        elif '|' not in regex:
            w = ''
            for i in range(len(regex)):
                if isinstance(regex[i], list):
                    break
                w += regex[i]
                if i == len(regex) - 1:
                    return self.calculaEstado(w, estado_anterior)

        # Corta nos |
        # Faz garantir que esteja sempre Caso base | Caso base | ... (sempre um '|' entre dois itens da lista)
        lst = []
        while '|' in regex:
            lst.append(regex[:regex.index('|')])
            regex = regex[regex.index('|') + 1:]
        lst.append(regex)
        # Transforma [x] em x
        for i in range(len(lst)):
            if len(lst[i]) == 1:
                lst[i] = lst[i][0]
        regex = lst

        # Se eh com + ou *, cria estado inicial e final adicionais
        if star or plus:
            self.estado_id += 1
            novo_inicio = Estado(self.estado_id)
            self.todos_estados.append(novo_inicio)
            self.estado_id += 1
            novo_fim = Estado(self.estado_id)
            self.todos_estados.append(novo_fim)
            
            estado_anterior_old = estado_anterior
            estado_anterior = novo_inicio

        estados_criados = []
        # [x, x, x]
        for r in regex:
            # 'x'
            if isinstance(r, str):
                estado = self.calculaEstado(r, estado_anterior)
                estados_criados.append(estado)
            
            # [...]
            else:
                lst_de_lst = False
                for s in r:
                    if isinstance(s, list):
                        lst_de_lst = True

                # [[x, x], x]
                if lst_de_lst and '|' not in r:
                    estado = estado_anterior
                    for i in range(len(r)):
                        estado = self.calculaEstado(r[i], estado)
                    estados_criados.append(estado)
                # [x]
                else:
                    estado = self.calculaEstado(r, estado_anterior)
                    estados_criados.append(estado)

        self.estado_id += 1
        ultimo_estado_comum = Estado(self.estado_id)
        self.todos_estados.append(ultimo_estado_comum)

        for e in estados_criados:
            l = Ligacao(e, ultimo_estado_comum, '$')
            self.todas_ligacoes.append(l)
        
        # Liga os estados adicionais criados anteriormente
        # Se *, liga do inicio ao fim (pra poder pular)
        if plus or star:
            if star:
                self.todas_ligacoes.append(Ligacao(novo_inicio, novo_fim, '$'))
            self.todas_ligacoes.append(Ligacao(ultimo_estado_comum, novo_inicio, '$'))
            self.todas_ligacoes.append(Ligacao(estado_anterior_old, novo_inicio, '$'))
            self.todas_ligacoes.append(Ligacao(ultimo_estado_comum, novo_fim, '$'))

            return novo_fim

        return ultimo_estado_comum

    # de   (a*b|b*) | abb* | (ab|(a|b))+ | $
    # para [['a*b', '|', 'b*'], '|', 'abb*', '|', ['+', 'ab', '|', ['a', '|', 'b']], '|', '$']
    def separaOR(self, regex):
        lst = []
        # Se o primeiro caractere abre parenteses
        if regex[0] == '(':
            # E o ultimo fecha esse parenteses (ou seja, um parenteses pegando todo regex)
            cont = 0
            abriu_fechou = True
            for i in range(1, len(regex)):
                if regex[i] == '(':
                    cont += 1
                elif regex[i] == ')':
                    cont -= 1
                # Se fechou o parenteses aberto no inicio antes do final (ou seja, tem dois abre-fecha de parenteses: (x)...(x))
                if cont == -1 and i < len(regex) - 2:
                    abriu_fechou = False

            if abriu_fechou:
                if regex[-1] == ')':
                    # Tira o parenteses
                    regex = regex[1:-1]
                # Senao, ve se o penultimo fecha e o ultimo eh * ou +
                elif (regex[-1] == '*' and regex[-2] == ')') or (regex[-1] == '+' and regex[-2] == ')'):
                    # Add o * ou + no inicio de lst e tira o parenteses
                    lst.append(regex[-1])
                    regex = regex[1:-2]

        abriu_parenteses = False
        subregex = ''
        i = 0
        cont = 0
        # A cada ciclo, tira fora o inicio do regex (que ja tiver sido computado)
        # Termina quando computar tudo (quando regex ficar vazio)
        while i < len(regex):
            # Se ja abriu parenteses
            if abriu_parenteses:
                # Se abriu outro, aumenta contador de parenteses abertos
                if regex[i] == '(':
                    cont += 1
                # Se fechou parenteses
                elif regex[i] == ')':
                    # E o contador de abertos ta zerado, entao...
                    if cont == 0:
                        # Transfere o char atual para subregex
                        subregex += regex[i]
                        # Se o proximo char eh + ou *, transfere tambem
                        i += 1
                        if i < len(regex) and (regex[i] == '+' or regex[i] == '*'):
                            subregex += regex[i]
                            i += 1
                        # Se subregex tem ( ou |, chama recursivo para continuar dividindo
                        if subregex.find('(') != -1 or (subregex.find('|') != -1 and len(subregex) > 1):
                            subregex = self.separaOR(subregex)
                        lst.append(subregex)
                        subregex = ''
                        abriu_parenteses = False
                    # Se o contador nao tiver zerado, decrementa o parenteses fechado
                    else:
                        cont -= 1
            else:
                # Se abriu parenteses nesse char atual OU se char atual ou anterior eh |, termina subregex e comeca outro
                if (regex[i] == '(') or (regex[i] == '|' or (i > 0 and regex[i - 1] == '|')):
                    # Adiciona o trecho anterior ao parenteses em lst
                    # Se esse trecho tiver ( ou |, chama recursivo para continuar dividindo
                    if subregex.find('(') != -1 or (subregex.find('|') != -1 and len(subregex) > 1):
                        subregex = self.separaOR(subregex)
                    lst.append(subregex)
                    subregex = ''
                    if regex[i] == '(':
                        cont = 0
                        abriu_parenteses = True
            # Transfere o char atual pra subregex
            if i < len(regex):
                subregex += regex[i]
                i += 1
        
        # Se o trecho tiver ( ou |, chama recursivo para continuar dividindo
        if subregex.find('(') != -1 or (subregex.find('|') != -1 and len(subregex) > 1):
            subregex = self.separaOR(subregex)
        lst.append(subregex)

        # Remove itens sem nada
        lst = list(filter(lambda a: a != '', lst))

        # Retorna a lista de itens ou o item, caso haja apenas um
        return lst if len(lst) > 1 else lst[0]


    def validaPalavra(self, word):
        valido, lst = self.validaPalavraAux(word, self.estado_inicial, 0)
        
        # Printa caminho percorrido
        w = ''
        print('Caminho de estados:\n-> (0)', end='')
        for i in range(len(lst)-1, -1, -1):
            print(' -'+ lst[i][0] +'-> ('+ str(lst[i][1]) +')', end='')
            w += lst[i][0]
        print(' -> ERRO' if not valido else '')

        return valido

    def validaPalavraAux(self, word, estado, vazios_seguidos):
        max_vazios_seguidos = 10
        lst = []
        # Se a palavra acabou
        if len(word) == 0:
            # Se ta no estado final, retorna que a palavra eh valida
            if estado.final:
                return True, []
            # Se nao estiver no estado final, avanca pelos $ (se tiver) buscando um estado final
            for e in estado.saida:
                if e.token == '$' and vazios_seguidos < max_vazios_seguidos:
                    valido, lst = self.validaPalavraAux(word, e.para, vazios_seguidos+1)
                    if valido:
                        lst.append(('$', e.para.id))
                        return valido, lst
            return False, lst

        # Pega o token a ser analisado
        prox_token = word[0]

        # Para cada saida do estado
        for e in estado.saida:
            # Se a saida eh igual ao token atual
            if e.token == prox_token:
                valido, lst = self.validaPalavraAux(word[1:], e.para, 0)
                lst.append((prox_token, e.para.id))
                # Se ja tiver validado a palavra, retorna True
                if valido:
                    return valido, lst
            # Se a saida eh vazio, avanca tambem
            elif e.token == '$' and vazios_seguidos < max_vazios_seguidos:
                valido, lst = self.validaPalavraAux(word, e.para, vazios_seguidos + 1)
                lst.append(('$', e.para.id))
                if valido:
                    return valido, lst

        return False, lst
