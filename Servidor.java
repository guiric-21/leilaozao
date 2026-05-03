import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Ponto de entrada do sistema
// Fica aguardando clientes e delega cada um para uma thread do pool
public class Servidor {

    static final int PORTA = 5555;
    static final int MAX_CLIENTES = 10;

    public static void main(String[] args) throws IOException {
        // Lista de leiloes compartilhada entre todos os clientes
        List<Leilao> leiloes = new ArrayList<>();
        leiloes.add(new Leilao(1, "Notebook Dell", 1500.0));
        leiloes.add(new Leilao(2, "iPhone 15", 3000.0));
        leiloes.add(new Leilao(3, "Camera Canon", 2500.0));

        // Pool de threads: atende ate 10 clientes ao mesmo tempo
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        ServerSocket servidor = new ServerSocket(PORTA);
        System.out.println("Servidor iniciado na porta " + PORTA);
        System.out.println(leiloes.size() + " leiloes disponiveis. Aguardando clientes...");

        // Loop infinito: aceita um cliente, passa pra uma thread, volta a esperar
        while (true) {
            Socket clienteSocket = servidor.accept(); // bloqueia ate alguem conectar
            System.out.println("Novo cliente: " + clienteSocket.getInetAddress());
            pool.execute(new ManipuladorCliente(clienteSocket, leiloes));
        }
    }
}