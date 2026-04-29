import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor de Leilões Distribuído
 * 
 * Características de Sistema Distribuído:
 * 1. CONCORRÊNCIA: Usa ThreadPool para lidar com múltiplos clientes simultaneamente
 * 2. DISPONIBILIDADE: Servidor contínuo aceitando requisições
 * 3. TRANSPARÊNCIA DE LOCALIZAÇÃO: Clientes não conhecem detalhes da arquitetura interna
 * 
 * Arquitetura:
 * - Um thread para aceitar conexões (acceptor)
 * - ThreadPool para servir múltiplos clientes em paralelo
 * - AuctionManager compartilhado entre todas as threads (thread-safe)
 */
public class Server {
    
    private static final int PORT = 5555;
    private static final int THREAD_POOL_SIZE = 10; // Máximo de clientes simultâneos
    
    private ServerSocket serverSocket;
    private AuctionManager auctionManager;
    private ExecutorService executorService;
    private boolean running;
    
    public Server() {
        this.auctionManager = new AuctionManager();
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.running = true;
    }
    
    /**
     * Inicia o servidor
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║  SERVIDOR DE LEILÕES INICIADO          ║");
            System.out.println("║  Porta: " + PORT);
            System.out.println("║  ThreadPool: " + THREAD_POOL_SIZE + " clientes simultâneos");
            System.out.println("╚════════════════════════════════════════╝");
            
            // Inicializa alguns leilões de exemplo
            initializeAuctions();
            
            // Loop para aceitar conexões
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[" + java.time.LocalDateTime.now() + "] Novo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Cria um ClientHandler e submete ao thread pool
                    ClientHandler clientHandler = new ClientHandler(clientSocket, auctionManager);
                    executorService.execute(clientHandler); // CONCORRÊNCIA: executa em thread separada
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * Inicializa alguns leilões de exemplo
     */
    private void initializeAuctions() {
        auctionManager.createAuction("Notebook Dell XPS 13", 2000.0);
        auctionManager.createAuction("iPhone 15 Pro", 3500.0);
        auctionManager.createAuction("Relógio Inteligente", 800.0);
        auctionManager.createAuction("Câmera Canon EOS R5", 5000.0);
        System.out.println("Leilões inicializados: 4 itens disponíveis");
    }
    
    /**
     * Para o servidor
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executorService.shutdown();
            System.out.println("\nServidor encerrado.");
        } catch (IOException e) {
            System.err.println("Erro ao encerrar servidor: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Server server = new Server();
        
        // Hook para parar o servidor com Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nEncerrando servidor...");
            server.stop();
        }));
        
        server.start();
    }
}
