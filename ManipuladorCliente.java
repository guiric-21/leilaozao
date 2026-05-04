import java.io.*;
import java.net.Socket;
import java.util.List;

// Responsavel por conversar com UM cliente conectado
// Roda em uma thread separada para cada cliente (permite multiplos simultaneos)
public class ManipuladorCliente implements Runnable {

    Socket socket;
    List<Leilao> leiloes;
    List<ManipuladorCliente> clientesConectados;
    String nomeCliente;
    PrintWriter saida;

    public ManipuladorCliente(Socket socket, List<Leilao> leiloes, List<ManipuladorCliente> clientesConectados) {
        this.socket = socket;
        this.leiloes = leiloes;
        this.clientesConectados = clientesConectados;
    }

    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);

            println("==================================");
            println("   BEM-VINDO AO LEILAO SD!");
            println("==================================");
            println("Digite seu nome:");
            nomeCliente = entrada.readLine();
            if (nomeCliente == null) return;

            println("Ola, " + nomeCliente + "!");

            // Loop principal: exibe menu e processa comandos ate todos leiloes encerrarem
            while (true) {
                if (!algumLeilaoAtivo()) {
                    mostrarResultadoFinal();
                    break;
                }

                mostrarMenu();

                String linha = entrada.readLine();
                if (linha == null || linha.trim().equals("sair")) {
                    println("Ate logo, " + nomeCliente + "!");
                    break;
                }

                println(processarComando(linha.trim()));
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + nomeCliente);
        } finally {
            clientesConectados.remove(this); // remove da lista ao desconectar
            try { socket.close(); } catch (IOException e) {}
        }
    }

    // Atalho para enviar texto ao cliente via socket
    private void println(String texto) {
        saida.println(texto);
    }

    // Envia uma notificacao para este cliente (chamado por outros clientes)
    public void notificar(String mensagem) {
        saida.println("\n[NOVO LANCE] " + mensagem);
        saida.print("> ");
        saida.flush();
    }

    // Avisa todos os outros clientes conectados sobre um novo lance
    private void notificarTodos(String mensagem) {
        for (ManipuladorCliente outro : clientesConectados) {
            if (outro != this) { // nao notifica quem deu o lance
                outro.notificar(mensagem);
            }
        }
    }

    // Exibe o menu com os leiloes disponiveis
    private void mostrarMenu() {
        println("");
        println("==================================");
        println("            MENU");
        println("==================================");
        for (Leilao l : leiloes) {
            long seg = (l.tempoFim - System.currentTimeMillis()) / 1000;
            if (seg < 0) seg = 0;
            String st = l.estaAtivo() ? "ATIVO" : "ENCERRADO";
            println("#" + l.id + " " + l.item);
            println("   R$ " + l.maiorLance + " | " + st + " | " + seg + "s");
        }
        println("==================================");
        println("Comandos:");
        println("  status <id>       ex: status 1");
        println("  lancar <id> <val> ex: lancar 1 2000");
        println("  sair");
        println("==================================");
        saida.print("> ");
        saida.flush();
    }

    // Exibe o resultado final quando todos os leiloes encerrarem
    private void mostrarResultadoFinal() {
        println("");
        println("==================================");
        println("       RESULTADO FINAL");
        println("==================================");
        for (Leilao l : leiloes) {
            println("");
            println("Item: " + l.item);
            println("Vencedor: " + l.vencedor);
            println("Valor: R$ " + l.maiorLance);
            println("Lances:");
            if (l.historico.isEmpty()) {
                println("  Nenhum lance recebido.");
            } else {
                for (Lance lance : l.historico) {
                    println("  > " + lance);
                }
            }
            println("----------------------------------");
        }
        println("");
        println("Obrigado, " + nomeCliente + "!");
        println("==================================");
    }

    // Interpreta o comando digitado pelo cliente e retorna a resposta
    private String processarComando(String comando) {
        if (comando.startsWith("status ")) {
            try {
                int id = Integer.parseInt(comando.split(" ")[1]);
                Leilao leilao = buscarLeilao(id);
                if (leilao == null) return "Leilao #" + id + " nao encontrado.";
                return leilao.status();
            } catch (Exception e) {
                return "Uso correto: status <id>  ex: status 1";
            }
        }

        if (comando.startsWith("lancar ")) {
            try {
                String[] partes = comando.split(" ");
                int id = Integer.parseInt(partes[1]);
                double valor = Double.parseDouble(partes[2]);
                Leilao leilao = buscarLeilao(id);

                if (leilao == null) return "Leilao #" + id + " nao encontrado.";
                if (!leilao.estaAtivo()) return "Este leilao ja foi encerrado.";

                if (leilao.darLance(nomeCliente, valor)) {
                    // Notifica todos os outros clientes sobre o novo lance
                    notificarTodos(nomeCliente + " lancou R$ " + valor + " no item #" + id + " (" + leilao.item + ")");
                    return "Lance aceito! Voce lidera com R$ " + valor;
                } else {
                    return "Lance recusado. Lance minimo: R$ " + (leilao.maiorLance + 0.01);
                }
            } catch (Exception e) {
                return "Uso correto: lancar <id> <valor>  ex: lancar 1 2000";
            }
        }

        return "Comando invalido. Use: status | lancar | sair";
    }

    private boolean algumLeilaoAtivo() {
        for (Leilao l : leiloes) {
            if (l.estaAtivo()) return true;
        }
        return false;
    }

    private Leilao buscarLeilao(int id) {
        for (Leilao l : leiloes) {
            if (l.id == id) return l;
        }
        return null;
    }
}