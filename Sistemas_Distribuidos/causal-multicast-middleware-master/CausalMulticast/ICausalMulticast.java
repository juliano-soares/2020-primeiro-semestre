package CausalMulticast;

/**
 * Interface para o usuario do middleware
 * 
 * @author jlsoares
 * @author rvales
 */
public interface ICausalMulticast {
    /**
     * Metodo chamado pelo middleware para entregar uma mensagem ao usuario
     * 
     * @param msg Mensagem a ser entregue
     * @author jlsoares
     * @author rvales
     */
    public void deliver(String msg);
}