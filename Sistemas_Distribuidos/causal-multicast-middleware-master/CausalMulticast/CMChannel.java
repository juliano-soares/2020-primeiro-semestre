package CausalMulticast;

import Client.Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representacao do canal do middleware
 * 
 * @author jlsoares
 * @author rvales
 */
public class CMChannel {
    private final ICausalMulticast client;
    private final Group group;
    
    // Thread para a etapa de descoberta
    private DiscoveryService discoveryService;
    private Thread tDiscovery;
    
    // Thread para o recebimento das mensagens
    private Listener listener;
    private Thread tListener;
    
    // Buffers para guardar os escolhidos para envio posterior
    private ArrayList<Message> delayed_m;
    private ArrayList<InetAddress> delayed_ip;
    
    // Quantidade de maquinas que se juntarao ao grupo
    private static final int GROUP_SIZE = 3;
    
    /**
     * Construtor de CMChannel
     * 
     * @param client Client ao qual esta vinculado
     * @author jlsoares
     * @author rvales
     */
    public CMChannel(ICausalMulticast client) {
        this.client = client;
        this.group = new Group(GROUP_SIZE);
        
        this.delayed_m = new ArrayList<>();
        this.delayed_ip = new ArrayList<>();

        this.discoveryService = new DiscoveryService(group);
        this.tDiscovery = new Thread(discoveryService);
        this.tDiscovery.start();
        
        this.listener = new Listener(this.client, this.group);
        this.tListener = new Thread(listener);
        this.tListener.start();
    }
    
    /**
     * Envia uma mensagem as aplicacoes vinculadas ao middleware
     * 
     * @param msg Mensagem para ser enviada
     * @param cliente Usuario que enviara a mensagem
     */
    public void mcsend(String msg, Object cliente) {
        // Se ainda esta na etapa de descobrimento, retorna
        if (!this.group.isComplete()) {
            System.out.println("Etapa de descobrimento em andamento...");
            return;
        }

        Scanner scanner = new Scanner(System.in);        

        if (msg.length() > 0) {
            // Constroi o vetor de relogios e cria a mensagem
            int[] vc = new int[this.group.getSize()];
            for (int i = 0; i < this.group.getSize(); i++) {
                vc[i] = this.group.vc[this.group.myPos][i];
            }
            Message m = new Message(this.group.myPos, msg, vc);

            System.out.print("Enviar a mensagem a todos? (s/n) ");
            char forAll = scanner.next().charAt(0);

            for (int i = 0; i < this.group.getSize(); i++) {
                // Pula si proprio
                if (i == this.group.myPos)
                    continue;

                // Se nao eh pra enviar para todos, pergunta para cada um se eh pra enviar
                // aqueles que nao sejam, adiciona na lista delayed
                if (forAll == 'n') {
                    System.out.print("Enviar para " + this.group.members.get(i).getHostAddress() + "? (s/n) ");
                    if (scanner.next().charAt(0) == 'n') {
                        this.delayed_m.add(m);
                        this.delayed_ip.add(this.group.members.get(i));
                        continue;
                    }
                }

                try {
                    // Cria socket, serializa a mensagem para bytes, e envia para o membro i
                    DatagramSocket socket = new DatagramSocket();
                    byte[] message = m.serialize();
                    DatagramPacket send = new DatagramPacket(message, message.length, this.group.members.get(i), 4321);
                    socket.send(send);
                    socket.close();

                } catch (SocketException ex) {
                    Logger.getLogger(CMChannel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CMChannel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            //  Incrementa o clock
            this.group.vc[this.group.myPos][this.group.myPos]++;

            // Deposita a mensagem no buffer
            this.group.buffer.add(m);

            this.group.printInfos();
        }
        
        if (this.delayed_m.size() > 0) {
            System.out.print("\nEnviar " + this.delayed_m.size() + " mensagens atrasadas? (s/n) ");
            if (scanner.next().charAt(0) == 's') {
                for (int i = 0; i < this.delayed_m.size(); i++) {
                    try {
                        // Cria socket, serializa a mensagem para bytes, e envia para o membro i
                        DatagramSocket socket = new DatagramSocket();
                        byte[] message = this.delayed_m.get(i).serialize();
                        DatagramPacket send = new DatagramPacket(message, message.length, this.delayed_ip.get(i), 4321);
                        socket.send(send);
                        socket.close();

                    } catch (SocketException ex) {
                        Logger.getLogger(CMChannel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CMChannel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                this.delayed_m.clear();
                this.delayed_ip.clear();
            }
        }
    }
}
