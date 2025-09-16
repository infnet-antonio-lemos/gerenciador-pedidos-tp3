package cli;

import cli.service.HttpClientService;
import cli.menu.*;
import java.util.Scanner;

public class CLIApplication {
    private final HttpClientService httpService;
    private final Scanner scanner;
    private String authToken;
    private boolean running;

    public CLIApplication(String baseUrl) {
        this.httpService = new HttpClientService(baseUrl);
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        while (running) {
            if (authToken == null) {
                // Show authentication menu
                AuthMenu authMenu = new AuthMenu(httpService, scanner);
                authToken = authMenu.show();

                if (authToken == null) {
                    // User chose to exit
                    running = false;
                }
            } else {
                // Show main menu
                showMainMenu();
            }
        }

        System.out.println("Aplicação encerrada. Até logo!");
        scanner.close();
    }

    private void showMainMenu() {
        while (running && authToken != null) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Gerenciar Produtos");
            System.out.println("2. Gerenciar Endereços");
            System.out.println("3. Gerenciar Pedidos");
            System.out.println("4. Logout");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    ProductMenu productMenu = new ProductMenu(httpService, scanner, authToken);
                    productMenu.show();
                    break;
                case "2":
                    AddressMenu addressMenu = new AddressMenu(httpService, scanner, authToken);
                    addressMenu.show();
                    break;
                case "3":
                    OrderMenu orderMenu = new OrderMenu(httpService, scanner, authToken);
                    orderMenu.show();
                    break;
                case "4":
                    logout();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void logout() {
        try {
            httpService.logout(authToken);
            authToken = null;
            System.out.println("Logout realizado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao fazer logout: " + e.getMessage());
            authToken = null; // Clear token anyway
        }
    }
}
