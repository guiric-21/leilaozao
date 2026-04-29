import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente para conectar ao Servidor de Leilões
 * 
 * TRANSPARÊNCIA DE LOCALIZAÇÃO: O cliente não precisa saber onde o servidor está
 * (basta saber hostname e porta)
 */
public class Client {
    
    private String hostname;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    /**
     * Conecta ao servidor
     */
    public boolean connect() {
        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            System.out.println("✓ Conectado ao servidor de leilões!");
            return true;
            
        } catch (IOException e) {
            System.out.println("✗ Erro ao conectar: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inicia o cliente - loop de interação com o servidor
     */
    public void start() {
        if (!connect()) {
            return;
        }
        
        try {
            // Thread para receber mensagens do servidor
            Thread readerThread = new Thread(new ServerMessageReader());
            readerThread.setDaemon(true);
            readerThread.start();
            
            // Thread para enviar comandos
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("sair")) {
                    out.println("sair");
                    break;
                }
                out.println(input);
            }
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    /**
     * Classe interna para ler mensagens do servidor
     */
    private class ServerMessageReader implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.out.println("Desconectado do servidor.");
                }
            }
        }
    }
    
    /**
     * Desconecta do servidor
     */
    private void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Desconectado.");
        } catch (IOException e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 5555;
        
        // Permite passar hostname e porta como argumentos
        if (args.length >= 1) {
            hostname = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Porta inválida, usando 5555");
            }
        }
        
        Client client = new Client(hostname, port);
        client.start();
    }
}
