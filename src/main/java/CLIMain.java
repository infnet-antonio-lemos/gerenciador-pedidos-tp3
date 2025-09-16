import cli.CLIApplication;

public class CLIMain {
    public static void main(String[] args) {
        System.out.println("=== Gerenciador de Pedidos - Cliente CLI ===");
        System.out.println("Conectando ao servidor HTTP...");

        CLIApplication app = new CLIApplication("http://localhost:7000");
        app.start();
    }
}
