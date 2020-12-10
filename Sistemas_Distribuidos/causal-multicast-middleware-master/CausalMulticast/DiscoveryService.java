package CausalMulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementa thread para o servico de descobrimento
 * 
 * @author jlsoares
 * @author rvales
 */
public class DiscoveryService implements Runnable {
    public Group group;
    
    private MulticastSocket socket;
    
    private static final String multicastIp = "230.0.0.0";
    private static final int port = 4321;
    
    private static final byte ENTER = 1;
    private static final byte ACK = 2;

    /**
     * Construtor de DiscoveryService
     * 
     * @param group Grupo de rede que serao adicionados as instancias descobertas
     * @author jlsoares
     * @author rvales
     */
    public DiscoveryService(Group group) {
        this.group = group;

        try {
            // Entra no grupo multicast
            socket = new MulticastSocket(port);
            InetAddress groupMulticast = InetAddress.getByName(multicastIp);
            socket.joinGroup(groupMulticast);

            // Avisa que entrou para o grupo multicast
            byte[] msg = new byte[1];
            msg[0] = ENTER; // flag ENTER para avisar que entrou
            DatagramPacket packet = new DatagramPacket(msg, msg.length, groupMulticast, port);
            socket.send(packet);
            
        } catch (IOException ex) {
            System.out.println(ex);
            Logger.getLogger(DiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Realiza o descobrimento dos outros membros
     * 
     * @author jlsoares
     * @author rvales
     */
    @Override
    public void run() {
        try {
            System.out.println("\n=== Iniciando etapa de descobrimento");
            
            // Se adiciona no grupo
            this.group.addMember(InetAddress.getLocalHost());
            
            byte rep[] = new byte[1024];
            DatagramPacket pkg = new DatagramPacket(rep, rep.length);
            
            while (!this.group.isComplete()) {
                socket.receive(pkg);
                byte[] data = pkg.getData();

                // Se eh mensagem dele mesmo, ignora
                if (pkg.getAddress().getHostAddress().compareTo(InetAddress.getLocalHost().getHostAddress()) == 0) {
                    continue;
                }
                
                // Se eh ACK, adiciona o remetente ao grupo
                // Se eh ENTER, adiciona o remetente ao grupo e envia o ACK de volta
                if (data[0] == ACK) {
                    this.group.addMember(pkg.getAddress());
                    
                } else if (data[0] == ENTER) {
                    this.group.addMember(pkg.getAddress());
                    
                    // Confirma recebimento
                    byte[] msg = new byte[1];
                    msg[0] = ACK;
                    DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(multicastIp), port);
                    socket.send(packet);
                }
            }
            
            System.out.println("Grupo completo (" + this.group.members.size() + " membros)");
            System.out.println("=== Encerrando etapa de descobrimento\n");
            
            socket.close();
        
        } catch (IOException ex) {
            System.out.println(ex);
            Logger.getLogger(DiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
