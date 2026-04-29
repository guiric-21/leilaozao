import java.util.Scanner;

/**
 * Classe Principal - Launcher do Sistema de Leilões Distribuído
 * 
 * Menu para iniciar:
 * 1. Servidor de Leilões
 * 2. Cliente de Leilões
 */
public class main {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║     SISTEMA DE LEILÕES DISTRIBUÍDO           ║");
        System.out.println("║                                               ║");
        System.out.println("║  Características de Sistema Distribuído:      ║");
        System.out.println("║  • Transparência de Localização              ║");
        System.out.println("║  • Concorrência (múltiplos clientes)         ║");
        System.out.println("║  • Disponibilidade (sempre online)           ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");
        
        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Iniciar Servidor");
            System.out.println("2 - Conectar como Cliente");
            System.out.println("3 - Sair");
            System.out.print("> ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    startServer();
                    break;
                    
                case "2":
                    startClient(scanner);
                    break;
                    
                case "3":
                    System.out.println("Até logo!");
                    scanner.close();
                    System.exit(0);
                    break;
                    
                default:
                    System.out.println("Opção inválida!\n");
            }
        }
    }
    
    private static void startServer() {
        System.out.println("\nIniciando Servidor...\n");
        new Thread(() -> {
            Server server = new Server();
            server.start();
        }).start();
        
        System.out.println("Servidor iniciado em background. Digite 'q' para voltar ao menu.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equalsIgnoreCase("q")) {
            // Aguarda comando para voltar
        }
        System.out.println("Voltando ao menu...\n");
    }
    
    private static void startClient(Scanner scanner) {
        System.out.println("\nConexão com Cliente");
        System.out.print("Hostname (padrão: localhost): ");
        String hostname = scanner.nextLine().trim();
        if (hostname.isEmpty()) {
            hostname = "localhost";
        }
        
        System.out.print("Porta (padrão: 5555): ");
        int port = 5555;
        try {
            String portStr = scanner.nextLine().trim();
            if (!portStr.isEmpty()) {
                port = Integer.parseInt(portStr);
            }
        } catch (NumberFormatException e) {
            System.out.println("Porta inválida, usando 5555");
        }
        
        System.out.println();
        Client client = new Client(hostname, port);
        client.start();
        System.out.println("Voltando ao menu...\n");
    }
}
