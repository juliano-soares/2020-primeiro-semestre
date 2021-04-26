package CausalMulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementa thread para o recebimento das mensagens trocadas pelos usuarios
 * 
 * @author jlsoares
 * @author rvales
 */
class Listener implements Runnable {
    private ICausalMulticast client;
    private Group group;
    
    private static final int port = 4321;
    
    /**
     * Construtor de Listener
     * 
     * @param client Referencia para o client (para entrega das mensagens recebidas)
     * @param group Grupo da rede
     * @author jlsoares
     * @author rvales
     */
    public Listener(ICausalMulticast client, Group group) {
        this.client = client;
        this.group = group;
    }
    
    /**
     * Realiza a escuta das mensagens recebidas por UDP
     * 
     * @author jlsoares
     * @author rvales
     */
    @Override
    public void run() {
        try {
            // Bloqueia ate terminar a etapa de descobrimento
            while (!this.group.isComplete()) {
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            // Inicia grupo (ordena ips, inicia clocks, ...)
            this.group.init();
            
            System.out.println("\n=== Digite uma mensagem");
            System.out.print("> ");
            
            // Cria socket e packet
            DatagramSocket socket = new DatagramSocket(4321);
            byte[] buffer = new byte[1024];
            DatagramPacket rep = new DatagramPacket(buffer, buffer.length);
            
            while (true) {
                for (int i = 0; i < 1024; i++)
                    buffer[i] = 0;
                
                socket.receive(rep);

                // Converte o array de byte para objeto Message
                Message m = new Message(rep.getData());
                
                // Coloca no buffer
                deposit(m);

                // Atualiza vc da instancia com o recebido
                for (int i = 0; i < this.group.getSize(); i++) {
                    this.group.vc[m.sender][i] = m.vc[i];
                }

                // Verifica e realiza o ordenamento causal
                boolean delay = false;
                System.out.println("[" + m.vc[0] + ", " + m.vc[1] + ", " + m.vc[2] + "]");
                System.out.println("e [" + this.group.vc[this.group.myPos][0] + ", " + this.group.vc[this.group.myPos][1] + ", " + this.group.vc[this.group.myPos][2] + "]");
                for (int i = 0; i < this.group.getSize(); i++) {
                    // Se vc da mensagem eh maior em alguma posicao, atrasa a mensagem
                    
                    // Para o sender testa com -1, visto que ja incrementou
                    if (i == m.sender) {
                        if (m.vc[i] - 1 > this.group.vc[this.group.myPos][i]) {
                            delay = true;
                            this.group.delayed.add(m);
                            break;
                        }
                        
                    } else if (m.vc[i] > this.group.vc[this.group.myPos][i]) {
                        delay = true;
                        this.group.delayed.add(m);
                        break;
                    }
                }
                
                // Se a mensagem nao foi atrasada...
                if (!delay) {
                    // Atualiza vc e entrega mensagem
                    if (this.group.myPos != m.sender) {
                        this.group.vc[this.group.myPos][m.sender]++;
                        
                        checkDiscart();
                        
                        this.group.printInfos();
                        this.client.deliver(m.content);
                    }

                    // Desatrasa as mensagens que puderem ser desatrasadas com a chegada dessa nova
                    boolean delivered;
                    do {
                        delivered = false;
                        // Percorre todas menssagens atrasadas
                        for (int i = 0; i < this.group.delayed.size(); i++) {
                            m = this.group.delayed.get(i);
                            
                            // Verifica se todo vc da mensagem tem <= que o vc da instancia
                            for (int j = 0; j < this.group.getSize(); j++) {
                                // Caso haja um maior, quebra o laco e mantem ela atrasada
                                if (j == m.sender) {
                                    if (m.vc[j] - 1 > this.group.vc[this.group.myPos][j])
                                        break;
                                    
                                } else if (m.vc[j] > this.group.vc[this.group.myPos][j]) {
                                    break;
                                }

                                // Se terminou o loop e nao quebrou o laco, entao entrega a mensagem pra camada superior
                                if (j == this.group.getSize() - 1) {
                                    Message md = this.group.delayed.remove(i);
                                    this.group.vc[this.group.myPos][md.sender]++;
                                    
                                    checkDiscart();
                                    
                                    this.group.printInfos();
                                    this.client.deliver(m.content);

                                    // Desatrasar essa pode ter liberado uma outra mensagem para ser entregue,
                                    // entao define flag para testar novamente o while
                                    delivered = true;
                                }
                            }
                        }
                    } while (delivered);
                }
            }
            
            //socket.close();

        } catch (SocketException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Verifica o buffer para descarte
     * 
     * @author jlsoares
     * @author rvales
     */
    private void checkDiscart() {
        // Verifica se alguma mensagem pode ser eliminada do buffer local
        for (int i = 0; i < this.group.buffer.size(); i++) {
            Message msg = this.group.buffer.get(i);
            
            // Obtem o campo de menor valor
            int min = 9999;
            for (int j = 0; j < this.group.getSize(); j++) {
                if (this.group.vc[j][msg.sender] < min)
                    min = this.group.vc[j][msg.sender];
            }
            
            if (msg.vc[msg.sender] <= min && min != -1) {
                discart(msg);
                i--;
            }
        }
    }
    
    /**
     * Deposita uma mensagem no buffer local
     * 
     * @param m Mensagem para ser depositada
     * @author jlsoares
     * @author rvales
     */
    private void deposit(Message m) {
        this.group.buffer.add(m);
    }
    
    /**
     * Descarta uma mensagem do buffer local
     * 
     * @param m Mensagem para ser descartada
     * @author jlsoares
     * @author rvales
     */
    private void discart(Message m) {
        System.out.println("descartou");
        this.group.buffer.remove(m);
    }
}
