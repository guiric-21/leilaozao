import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um leilão individual
 * Sincronizado para lidar com múltiplas threads (CONCORRÊNCIA)
 * Usa synchronized para garantir thread-safety ao adicionar lances
 */
public class Auction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int auctionId;
    private String itemDescription;
    private double initialPrice;
    private double currentHighestBid;
    private String currentWinner;
    private List<Bid> bidHistory;
    private boolean isActive;
    private long endTime;
    private static final long AUCTION_DURATION = 60000; // 60 segundos
    
    public Auction(int auctionId, String itemDescription, double initialPrice) {
        this.auctionId = auctionId;
        this.itemDescription = itemDescription;
        this.initialPrice = initialPrice;
        this.currentHighestBid = initialPrice;
        this.currentWinner = null;
        this.bidHistory = new ArrayList<>();
        this.isActive = true;
        this.endTime = System.currentTimeMillis() + AUCTION_DURATION;
    }
    
    /**
     * Adiciona um lance ao leilão
     * SINCRONIZADO: garante que apenas uma thread modifica bidHistory por vez
     */
    public synchronized boolean addBid(String bidderName, double amount) {
        // Verifica disponibilidade
        if (!isActive) {
            return false;
        }
        
        // Verifica se leilão expirou
        if (System.currentTimeMillis() > endTime) {
            isActive = false;
            return false;
        }
        
        // Lance deve ser maior que o atual
        if (amount <= currentHighestBid) {
            return false;
        }
        
        // Registra o lance
        Bid bid = new Bid(bidderName, amount);
        bidHistory.add(bid);
        currentHighestBid = amount;
        currentWinner = bidderName;
        
        return true;
    }
    
    /**
     * Retorna informações do leilão de forma sincronizada
     */
    public synchronized String getAuctionStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LEILÃO #").append(auctionId).append(" ===\n");
        sb.append("Item: ").append(itemDescription).append("\n");
        sb.append("Lance Inicial: R$ ").append(String.format("%.2f", initialPrice)).append("\n");
        sb.append("Lance Atual: R$ ").append(String.format("%.2f", currentHighestBid)).append("\n");
        sb.append("Vencedor Atual: ").append(currentWinner != null ? currentWinner : "Nenhum").append("\n");
        sb.append("Total de Lances: ").append(bidHistory.size()).append("\n");
        sb.append("Status: ").append(isActive ? "ATIVO" : "FINALIZADO").append("\n");
        
        long remainingTime = (endTime - System.currentTimeMillis()) / 1000;
        if (isActive && remainingTime > 0) {
            sb.append("Tempo Restante: ").append(remainingTime).append("s\n");
        }
        
        return sb.toString();
    }
    
    public synchronized List<Bid> getBidHistory() {
        return new ArrayList<>(bidHistory);
    }
    
    public synchronized void checkAndCloseAuction() {
        if (isActive && System.currentTimeMillis() > endTime) {
            isActive = false;
        }
    }
    
    public int getAuctionId() {
        return auctionId;
    }
    
    public synchronized boolean isActive() {
        checkAndCloseAuction();
        return isActive;
    }
    
    public synchronized double getCurrentHighestBid() {
        return currentHighestBid;
    }
    
    public synchronized String getCurrentWinner() {
        return currentWinner;
    }
    
    public String getItemDescription() {
        return itemDescription;
    }
}
