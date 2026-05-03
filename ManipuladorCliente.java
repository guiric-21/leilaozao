import java.io.*;
import java.net.Socket;
import java.util.List;

// Responsavel por conversar com UM cliente conectado
// Roda em uma thread separada para cada cliente (permite multiplos simultaneos)
public class ManipuladorCliente implements Runnable {

    Socket socket;
    List<Leilao> leiloes;
    String nomeCliente;

    public ManipuladorCliente(Socket socket, List<Leilao> leiloes) {
        this.socket = socket;
        this.leiloes = leiloes;
    }

    public void run() {
        try {
            // Abre canais de leitura e escrita com o cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

            // Pede o nome do participante
            saida.println("Bem-vindo ao Leilao! Digite seu nome:");
            nomeCliente = entrada.readLine();
            saida.println("Ola, " + nomeCliente + "! Comandos: listar | status <id> | lancar <id> <valor> | sair");

            // Fica lendo comandos ate o cliente sair
            String linha;
            while ((linha = entrada.readLine()) != null) {
                String resposta = processarComando(linha.trim());
                saida.println(resposta);

                if (linha.trim().equals("sair")) break;
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + nomeCliente);
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    // Interpreta o comando digitado pelo cliente e retorna a resposta
    private String processarComando(String comando) {
        if (comando.equals("listar")) {
            return listarLeiloes();
        }

        if (comando.startsWith("status ")) {
            try {
                int id = Integer.parseInt(comando.split(" ")[1]);
                Leilao leilao = buscarLeilao(id);
                if (leilao == null) return "Leilao #" + id + " nao encontrado.";
                return leilao.status();
            } catch (Exception e) {
                return "Formato invalido. Use: status <id>  (ex: status 1)";
            }
        }

        if (comando.startsWith("lancar ")) {
            try {
                String[] partes = comando.split(" ");
                int id = Integer.parseInt(partes[1]);
                double valor = Double.parseDouble(partes[2]);
                Leilao leilao = buscarLeilao(id);

                if (leilao == null) return "Leilao #" + id + " nao encontrado.";

                if (leilao.darLance(nomeCliente, valor)) {
                    return "Lance aceito!\n" + leilao.status();
                } else {
                    return "Lance recusado. O lance deve ser maior que R$ " + leilao.maiorLance;
                }
            } catch (Exception e) {
                return "Formato invalido. Use: lancar <id> <valor>  (ex: lancar 1 2000)";
            }
        }

        if (comando.equals("sair")) {
            return "Ate logo!";
        }

        return "Comando invalido. Use: listar | status <id> | lancar <id> <valor> | sair";
    }

    private String listarLeiloes() {
        StringBuilder sb = new StringBuilder("=== Leiloes disponíveis ===\n");
        for (Leilao l : leiloes) {
            sb.append("#").append(l.id).append(" - ").append(l.item)
              .append(" (R$ ").append(l.maiorLance).append(")")
              .append(l.estaAtivo() ? " [ATIVO]" : " [ENCERRADO]")
              .append("\n");
        }
        return sb.toString();
    }

    private Leilao buscarLeilao(int id) {
        for (Leilao l : leiloes) {
            if (l.id == id) return l;
        }
        return null;
    }
}