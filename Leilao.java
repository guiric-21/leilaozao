import java.util.ArrayList;
import java.util.List;

// Representa um leilao de um item
// "synchronized" nos metodos garante que dois clientes nao modifiquem o leilao ao mesmo tempo
public class Leilao {

    int id;
    String item;
    double maiorLance;
    String vencedor;
    List<Lance> historico;
    boolean ativo;
    long tempoFim; // momento em que o leilao encerra (em milissegundos)

    static final long DURACAO = 3 * 60 * 1000; // 3 minutos em milissegundos

    public Leilao(int id, String item, double lanceInicial) {
        this.id = id;
        this.item = item;
        this.maiorLance = lanceInicial;
        this.vencedor = "nenhum";
        this.historico = new ArrayList<>();
        this.ativo = true;
        this.tempoFim = System.currentTimeMillis() + DURACAO;
    }

    // Tenta registrar um lance. Retorna true se aceito, false se recusado.
    // "synchronized" = apenas um cliente por vez pode dar lance (evita conflito)
    public synchronized boolean darLance(String participante, double valor) {
        // Verifica se o leilao ainda esta no prazo
        if (System.currentTimeMillis() > tempoFim) {
            ativo = false;
            return false;
        }

        // O lance precisa ser maior que o atual
        if (valor <= maiorLance) {
            return false;
        }

        // Aceita o lance
        maiorLance = valor;
        vencedor = participante;
        historico.add(new Lance(participante, valor));
        return true;
    }

    // Retorna um resumo do leilao em texto
    public synchronized String status() {
        // Calcula quantos segundos faltam
        long segundosRestantes = (tempoFim - System.currentTimeMillis()) / 1000;
        if (segundosRestantes < 0) segundosRestantes = 0;

        return "=== Leilao #" + id + " ===" +
               "\nItem: " + item +
               "\nMaior lance: R$ " + maiorLance +
               "\nVencedor atual: " + vencedor +
               "\nTempo restante: " + segundosRestantes + "s" +
               "\nStatus: " + (ativo ? "ATIVO" : "ENCERRADO");
    }

    public synchronized boolean estaAtivo() {
        if (System.currentTimeMillis() > tempoFim) {
            ativo = false;
        }
        return ativo;
    }
}