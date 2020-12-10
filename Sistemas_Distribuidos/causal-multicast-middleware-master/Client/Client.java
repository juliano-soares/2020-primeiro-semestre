package Client;

import CausalMulticast.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Representacao de um usuario do middleware
 *
 * @author jlsoares
 * @author rvales
 */
public class Client implements ICausalMulticast {
    private CMChannel canal = new CMChannel(this);
    private ArrayList<String> history = new ArrayList<>();
    
    /**
     * Realiza a leitura das mensagens a partir da entrada padrão e envio para o middleware
     * 
     * @author jlsoares
     * @author rvales
     */
    public void startChatting() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.nextLine();
            canal.mcsend(s, this);
            
            if (s.length() != 0)
                history.add("* " + s);
            printHistory();
        }
    }
    
    /**
     * Recebimento de uma mensagem pelo middleware
     * 
     * @param msg Mensagem recebida
     * @author jlsoares
     * @author rvales
     */
    @Override
    public void deliver(String msg) {
        history.add(msg);
        printHistory();
    }
    
    private void printHistory() {
        int n_lasts = 10;
        // Imprime as n_lasts mensagens recebidas novamente, ja que elas ficarao
        // distantes por causa da reimpressao do buffer e relogios logicos
        System.out.println("\n=== Mensagens");
        
        int t = history.size();
        for (int i = (t > n_lasts ? t-n_lasts : 0); i < history.size(); i++) {
            System.out.println(history.get(i));
        }
        
        System.out.println("\n\n=== Digite uma mensagem");
        System.out.print("> ");
    }
}
