import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerencia todos os leilões do sistema
 * Implementa DISPONIBILIDADE: mantém todos os leilões acessíveis
 * Implementa CONCORRÊNCIA: usa ConcurrentHashMap para acesso thread-safe
 */
public class AuctionManager {
    
    // ConcurrentHashMap: garante acesso seguro sem locks (mais eficiente)
    private ConcurrentHashMap<Integer, Auction> auctions;
    private int nextAuctionId;
    
    public AuctionManager() {
        this.auctions = new ConcurrentHashMap<>();
        this.nextAuctionId = 1;
    }
    
    /**
     * Cria um novo leilão
     */
    public synchronized Auction createAuction(String itemDescription, double initialPrice) {
        Auction auction = new Auction(nextAuctionId++, itemDescription, initialPrice);
        auctions.put(auction.getAuctionId(), auction);
        return auction;
    }
    
    /**
     * Adiciona um lance a um leilão específico
     * Thread-safe graças ao ConcurrentHashMap
     */
    public boolean placeBid(int auctionId, String bidderName, double amount) {
        Auction auction = auctions.get(auctionId);
        if (auction != null) {
            return auction.addBid(bidderName, amount);
        }
        return false;
    }
    
    /**
     * Retorna informações de um leilão específico
     */
    public String getAuctionStatus(int auctionId) {
        Auction auction = auctions.get(auctionId);
        if (auction != null) {
            return auction.getAuctionStatus();
        }
        return "Leilão não encontrado.";
    }
    
    /**
     * Lista todos os leilões disponíveis
     */
    public String listAllAuctions() {
        if (auctions.isEmpty()) {
            return "Nenhum leilão disponível.";
        }
        
        StringBuilder sb = new StringBuilder("=== LEILÕES DISPONÍVEIS ===\n");
        for (Auction auction : auctions.values()) {
            sb.append(String.format("#%d - %s (Lance Atual: R$ %.2f)\n", 
                auction.getAuctionId(), 
                auction.getItemDescription(),
                auction.getCurrentHighestBid()));
        }
        return sb.toString();
    }
    
    /**
     * Obtém histórico de lances de um leilão
     */
    public String getBidHistory(int auctionId) {
        Auction auction = auctions.get(auctionId);
        if (auction == null) {
            return "Leilão não encontrado.";
        }
        
        StringBuilder sb = new StringBuilder("=== HISTÓRICO DE LANCES - LEILÃO #" + auctionId + " ===\n");
        for (Bid bid : auction.getBidHistory()) {
            sb.append(bid.toString()).append("\n");
        }
        return sb.toString();
    }
    
    public Auction getAuction(int auctionId) {
        return auctions.get(auctionId);
    }
    
    public Map<Integer, Auction> getAllAuctions() {
        return new HashMap<>(auctions);
    }
}
