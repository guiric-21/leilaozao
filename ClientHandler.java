import java.io.*;
import java.net.Socket;

/**
 * Representa um cliente conectado ao servidor
 * CONCORRÊNCIA: cada cliente é servido em uma thread separada
 * Implementa Runnable para ser executado em thread dedicada
 */
public class ClientHandler implements Runnable {
    
    private Socket socket;
    private AuctionManager auctionManager;
    private String clientName;
    private BufferedReader in;
    private PrintWriter out;
    
    public ClientHandler(Socket socket, AuctionManager auctionManager) {
        this.socket = socket;
        this.auctionManager = auctionManager;
    }
    
    @Override
    public void run() {
        try {
            // Inicializa streams de comunicação
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // Recebe nome do cliente
            out.println("Bem-vindo ao Sistema de Leilões!");
            out.println("Digite seu nome:");
            clientName = in.readLine();
            
            if (clientName == null || clientName.trim().isEmpty()) {
                clientName = "Cliente_" + socket.getPort();
            }
            
            out.println("Olá, " + clientName + "! Digite 'ajuda' para ver comandos disponíveis.");
            
            // Loop para processar comandos do cliente
            String command;
            while ((command = in.readLine()) != null) {
                processCommand(command.trim());
            }
            
        } catch (IOException e) {
            System.err.println("Erro na comunicação com cliente: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Processa comandos recebidos do cliente
     * TRANSPARÊNCIA DE LOCALIZAÇÃO: cliente não sabe onde estão os dados dos leilões
     */
    private void processCommand(String command) {
        if (command.isEmpty()) {
            return;
        }
        
        String[] parts = command.split(" ");
        String action = parts[0].toLowerCase();
        
        switch (action) {
            case "listar":
                handleListAuctions();
                break;
                
            case "status":
                handleAuctionStatus(parts);
                break;
                
            case "lancar":
                handlePlaceBid(parts);
                break;
                
            case "historico":
                handleBidHistory(parts);
                break;
                
            case "sair":
                out.println("Desconectando...");
                closeConnection();
                break;
                
            case "ajuda":
                handleHelp();
                break;
                
            default:
                out.println("Comando não reconhecido. Digite 'ajuda' para ver opções.");
        }
    }
    
    /**
     * Lista todos os leilões disponíveis
     */
    private void handleListAuctions() {
        out.println(auctionManager.listAllAuctions());
    }
    
    /**
     * Mostra status de um leilão específico
     */
    private void handleAuctionStatus(String[] parts) {
        if (parts.length < 2) {
            out.println("Uso: status <id_leilao>");
            return;
        }
        
        try {
            int auctionId = Integer.parseInt(parts[1]);
            out.println(auctionManager.getAuctionStatus(auctionId));
        } catch (NumberFormatException e) {
            out.println("ID de leilão inválido.");
        }
    }
    
    /**
     * Coloca um lance em um leilão
     * CONCORRÊNCIA: múltiplos clientes podem fazer lances simultaneamente
     */
    private void handlePlaceBid(String[] parts) {
        if (parts.length < 3) {
            out.println("Uso: lancar <id_leilao> <valor>");
            return;
        }
        
        try {
            int auctionId = Integer.parseInt(parts[1]);
            double amount = Double.parseDouble(parts[2]);
            
            if (amount <= 0) {
                out.println("Valor do lance deve ser positivo.");
                return;
            }
            
            boolean success = auctionManager.placeBid(auctionId, clientName, amount);
            
            if (success) {
                out.println("✓ Lance registrado com sucesso!");
                out.println(auctionManager.getAuctionStatus(auctionId));
            } else {
                Auction auction = auctionManager.getAuction(auctionId);
                if (auction == null) {
                    out.println("✗ Leilão não encontrado.");
                } else if (!auction.isActive()) {
                    out.println("✗ Leilão finalizado.");
                } else {
                    out.println("✗ Lance deve ser maior que R$ " + String.format("%.2f", auction.getCurrentHighestBid()));
                }
            }
        } catch (NumberFormatException e) {
            out.println("Formato inválido. Use: lancar <id_leilao> <valor>");
        }
    }
    
    /**
     * Mostra histórico de lances
     */
    private void handleBidHistory(String[] parts) {
        if (parts.length < 2) {
            out.println("Uso: historico <id_leilao>");
            return;
        }
        
        try {
            int auctionId = Integer.parseInt(parts[1]);
            out.println(auctionManager.getBidHistory(auctionId));
        } catch (NumberFormatException e) {
            out.println("ID de leilão inválido.");
        }
    }
    
    /**
     * Mostra ajuda
     */
    private void handleHelp() {
        out.println("\n=== COMANDOS DISPONÍVEIS ===");
        out.println("listar              - Lista todos os leilões disponíveis");
        out.println("status <id>         - Mostra status de um leilão específico");
        out.println("lancar <id> <valor> - Faz um lance em um leilão");
        out.println("historico <id>      - Mostra histórico de lances de um leilão");
        out.println("ajuda               - Mostra este menu");
        out.println("sair                - Desconecta do servidor");
        out.println("============================\n");
    }
    
    /**
     * Fecha a conexão com o cliente
     */
    private void closeConnection() {
        try {
            socket.close();
            System.out.println("Cliente " + clientName + " desconectado.");
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
