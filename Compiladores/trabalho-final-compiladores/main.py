import argparse
import logging
from AnalisaER import ValidaER
from AnalisaPalavra import AnalisaPalavra

def main():
    parser = argparse.ArgumentParser(description='Insira a ER desejada')
    parser.add_argument('-regex', type=str, help='string contendo uma ER, e.g. a(a|b)*b*', required=True)
    args = parser.parse_args()

    if args.regex:
        regex = args.regex

        ehValida = ValidaER(regex)

        if ehValida:
            p = AnalisaPalavra(regex)
            while True:
                word = input('Digite a palavra para validação: ')
                if word == '': break

                ehValida = p.validaPalavra(word)

                if ehValida:
                    print("A palavra", word, "é válida!")
                else:
                    print("A palavra", word, "não é válida!")
                print()
        else:
            print("ER não é válida!")
    else:
        print("ER não fornecida!")

if __name__ == "__main__":
    main()
