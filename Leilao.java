import java.util.ArrayList;
import java.util.List;


public class Leilao {

    int id;
    String item;
    double maiorLance;
    String vencedor;
    List<Lance> historico;
    boolean ativo;
    long tempoFim;

    static final long DURACAO = 3 * 60 * 1000; // 3 minutos em milissegundos, melhor usar assim pra conseguir mudar so o 1° e ajustar o tempo final

    public Leilao(int id, String item, double lanceInicial) {
        this.id = id;
        this.item = item;
        this.maiorLance = lanceInicial;
        this.vencedor = "Nenhum";
        this.historico = new ArrayList<>();
        this.ativo = true;
        this.tempoFim = System.currentTimeMillis() + DURACAO;
    }

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

    public synchronized String status() {
        long seg = (tempoFim - System.currentTimeMillis()) / 1000;
        if (seg < 0) seg = 0;
        return "= Leilao " + id + " =\n"
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