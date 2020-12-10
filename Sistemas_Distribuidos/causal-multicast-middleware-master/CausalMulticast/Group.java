package CausalMulticast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Grupo da rede
 * 
 * @author jlsoares
 * @author rvales
 */
public class Group {
    // Lista de membros pertecentes ao grupo
    public ArrayList<InetAddress> members = new ArrayList<>();
    
    // Matriz de relogios
    public int[][] vc;
    
    // Buffer
    public ArrayList<Message> buffer;
    public ArrayList<Message> delayed;
    
    // Endereco de rede e posicao na matriz de relogios da instancia
    public InetAddress myAddress;
    public int myPos;

    // Quantidade de membros
    private int size;

    /**
     * Construtor de Group
     * 
     * @param size Total de membros esperados no grupo
     * @author jlsoares
     * @author rvales
     */
    public Group(int size) {
        this.size = size;
        this.vc = new int[this.size][this.size];
        
        this.delayed = new ArrayList<>();
        this.buffer = new ArrayList<>();
    }
    
    /**
     * Obtem a quantidade esperada de membros no grupo
     * 
     * @return Quantidade esperada de membros no grupo
     * @author jlsoares
     * @author rvales
     */
    public int getSize() {
        return this.size;
    }
    
    /**
     * Verifica se o grupo esta completo
     * 
     * @return Se o grupo esta completo
     * @author jlsoares
     * @author rvales
     */
    public boolean isComplete() {
        return this.members.size() >= this.size;
    }
    
    /**
     * Adiciona um novo membro ao grupo
     * 
     * @param member Novo membro
     * @author jlsoares
     * @author rvales
     */
    public void addMember(InetAddress member) {
        // Se o membro ja foi inserido, retorna
        if (this.members.indexOf(member) != -1)
            return;
        
        this.members.add(member);
        
        System.out.println("Novo membro adicionado: " + member.getHostAddress());
    }
    
    /**
     * Inicializa as variaveis do grupo apos a etapa de descobrimento
     * 
     * @author jlsoares
     * @author rvales
     */
    public void init() {
        // IP da instancia eh sempre o primeiro adicionado na lista
        this.myAddress = this.members.get(0);
        
        // Ordena grupo pelo IP
        int n = this.members.size();
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                String ip1 = this.members.get(j).getHostAddress();
                String ip2 = this.members.get(j+1).getHostAddress();
                
                if (ip1.compareTo(ip2) > 0) { 
                    InetAddress temp = this.members.get(j); 
                    this.members.set(j, this.members.get(j+1));
                    this.members.set(j+1, temp);
                }
            }
        }
        
        // Resgata a posicao da instancia entre os membros
        this.myPos = this.members.indexOf(this.myAddress);
        
        printMembers();
        
        // Cria e inicializa o vetor de relogio
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.vc[i][j] = -1;
            }
        }
        // Coloca 0 no seu
        this.vc[this.myPos][this.myPos] = 0;
    }
    
    /**
     * Imprime os membros do grupo
     * 
     * @author jlsoares
     * @author rvales
     */
    private void printMembers() {
        System.out.println("Lista de membros do grupo:");
        for (int i = 0; i < this.members.size(); i++) {
            System.out.println("| " + this.members.get(i).getHostAddress());
        }
    }
    
    /**
     * Imprime buffer e relogios logicos
     * 
     * @author jlsoares
     * @author rvales
     */
    public void printInfos() {
        for (int i = 0; i < 30; i++)
            System.out.println();
        
        // Imprime buffer
        System.out.println("\n=== Buffer");
        
        if (this.buffer.size() > 0) {
            for (int i = 0; i < this.buffer.size(); i++) {
                Message msg = this.buffer.get(i);
                System.out.print(this.members.get(msg.sender).getHostAddress());
                System.out.print(" | [");
                for (int j = 0; j < msg.vc.length; j++) {
                    System.out.print(msg.vc[j]);
                    if (j < msg.vc.length - 1)
                        System.out.print(", ");
                    else
                        System.out.print("] | ");
                }
                System.out.println(msg.content);
            }
        } else {
            System.out.println("Vazio");
        }
        
        // Imprime relogios
        System.out.println("\n=== Relogios Logicos");
        
        System.out.print("           ");
        for (int i = 0; i < this.vc.length; i++) {
            System.out.print(this.members.get(i).getHostAddress() + "  ");
        }
        System.out.println();
        
        for (int i = 0; i < this.vc.length; i++) {
            System.out.print(this.members.get(i).getHostAddress());
            if (i == this.myPos)
                System.out.print("* ");
            else
                System.out.print("  ");

            for (int j = 0; j < this.vc.length; j++) {
                System.out.print("    ");
                if (this.vc[i][j] >= 0 && this.vc[i][j] < 10)
                    System.out.print(" ");
                System.out.print(this.vc[i][j]);
                System.out.print("     ");
            }
            System.out.println();
        }
    }
}
