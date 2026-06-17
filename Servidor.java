import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Servidor {

    static final int PORTA = 5555;
    static final int MAX_CLIENTES = 10;

    public static void main(String[] args) throws IOException {

        List<Leilao> leiloes = new ArrayList<>();
        leiloes.add(new Leilao(1, "Notebook Dell", 1500.0));
        leiloes.add(new Leilao(2, "iPhone 15", 3000.0));
        leiloes.add(new Leilao(3, "Camera Canon", 2500.0));
        leiloes.add(new Leilao(4, "Camisa do Sport Club Recife", 400));
        leiloes.add(new Leilao(5, "Óculos", 80));


        List<ManipuladorCliente> clientesConectados = new CopyOnWriteArrayList<>(); //thread safe
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        ServerSocket servidor = new ServerSocket(PORTA);
        System.out.println("Servidor iniciado na porta " + PORTA);
        System.out.println(leiloes.size() + " leiloes disponiveis. Aguardando clientes...");

        while (true) {
            Socket clienteSocket = servidor.accept();
            System.out.println("Novo cliente: " + clienteSocket.getInetAddress());

            // Passa a lista de clientes conectados pra notificar todo mundo
            ManipuladorCliente manipulador = new ManipuladorCliente(clienteSocket, leiloes, clientesConectados);
            clientesConectados.add(manipulador);
            pool.execute(manipulador);
        }
    }
}