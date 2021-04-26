package CausalMulticast;

/**
 * Representacao de uma mensagem
 * 
 * @author jlsoares
 * @author rvales
 */
public class Message {
    // Conteudo da mensagem
    public String content;
    // Vetor de relogios associado a mensagem
    public int[] vc;
    // Identificador de quem enviou a mensagem (sua posicao na matriz de relogios)
    public int sender;
    
    /**
     * Construtor de Message a partir dos atributos
     * 
     * @param sender Quem enviou a mensagem
     * @param msg Conteudo da mensagem
     * @param vc Vetor de relogios associado
     */
    public Message(int sender, String msg, int[] vc) {
        this.content = msg;
        this.vc = vc;
        this.sender = sender;
    }
    
    /**
     * Construtor de Message a partir de mensagem serializada
     * 
     * @param msg Mensagem serializada
     * @author jlsoares
     * @author rvales
     */
    public Message(byte[] msg) {
        // Transforma em string e divide pelo separador '@'
        String s = new String(msg, 0, msg.length);
        String[] vars = s.split("@");
        
        // Primeiro campo eh o sender
        this.sender = Integer.parseInt(vars[0]);
        
        // Segundo campo eh o vc
        // As posicoes do array divididas pelo separador '/'
        String[] vc_ = vars[1].split("/");
        this.vc = new int[vc_.length];
        for (int i = 0; i < vc_.length; i++) {
            this.vc[i] = Integer.parseInt(vc_[i]);
        }
        
        // Terceiro campo eh o conteudo
        this.content = vars[2];
    }
    
    /**
     * Serializa uma mensagem
     * 
     * @return Mensagem serializada (array de byte no formato sender@vc[0]/../vc[n]@content)
     * @author jlsoares
     * @author rvales
     */
    public byte[] serialize() {
        // Cada campo eh transformado em string e separado por @
        // Os valores do array vc sao separados por /

        String s = Integer.toString(this.sender) + "@";
        for (int i = 0; i < this.vc.length; i++) {
            s += Integer.toString(this.vc[i]);
            if (i < this.vc.length - 1) {
                s += "/";
            }
        }
        s += "@" + content;
        
        return s.getBytes();
    }
}
