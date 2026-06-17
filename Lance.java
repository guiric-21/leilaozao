public class Lance {
    String nomeParticipante;
    double valor;

    public Lance(String nomeParticipante, double valor) {
        this.nomeParticipante = nomeParticipante;
        this.valor = valor;
    }

    public String toString() {
        return nomeParticipante + " -> R$ " + valor;
    }
}