import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa um lance em um leilão
 * Implementa Serializable para transmissão via socket
 */
public class Bid implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String bidderName;
    private double amount;
    private LocalDateTime timestamp;
    
    public Bid(String bidderName, double amount) {
        this.bidderName = bidderName;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getBidderName() {
        return bidderName;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: R$ %.2f", timestamp.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")), bidderName, amount);
    }
}
