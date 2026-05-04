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
    long tempoFim;

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
        if (System.currentTimeMillis() > tempoFim) {
            ativo = false;
            return false;
        }
        if (valor <= maiorLance) {
            return false;
        }
        maiorLance = valor;
        vencedor = participante;
        historico.add(new Lance(participante, valor));
        return true;
    }

    // Retorna um resumo do leilao em texto
    public synchronized String status() {
        long seg = (tempoFim - System.currentTimeMillis()) / 1000;
        if (seg < 0) seg = 0;
        return "--- Leilao #" + id + " ---\n"
             + "Item: " + item + "\n"
             + "Maior lance: R$ " + maiorLance + "\n"
             + "Vencedor: " + vencedor + "\n"
             + "Tempo restante: " + seg + "s\n"
             + "Status: " + (ativo ? "ATIVO" : "ENCERRADO");
    }

    public synchronized boolean estaAtivo() {
        if (System.currentTimeMillis() > tempoFim) {
            ativo = false;
        }
        return ativo;
    }
}