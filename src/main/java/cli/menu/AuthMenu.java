package cli.menu;

import cli.service.HttpClientService;
import java.util.Scanner;

public class AuthMenu {
    private final HttpClientService httpService;
    private final Scanner scanner;

    public AuthMenu(HttpClientService httpService, Scanner scanner) {
        this.httpService = httpService;
        this.scanner = scanner;
    }

    public String show() {
        while (true) {
            System.out.println("\n=== AUTENTICAÇÃO ===");
            System.out.println("1. Login");
            System.out.println("2. Registrar");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    String token = login();
                    if (token != null) {
                        return token;
                    }
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    return null;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private String login() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Senha: ");
        String password = scanner.nextLine().trim();

        try {
            String token = httpService.login(email, password);
            System.out.println("Login realizado com sucesso!");
            return token;
        } catch (Exception e) {
            System.out.println("Erro no login: " + e.getMessage());
            return null;
        }
    }

    private void register() {
        System.out.print("Nome: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Senha: ");
        String password = scanner.nextLine().trim();

        System.out.print("Confirmar senha: ");
        String confirmPassword = scanner.nextLine().trim();

        System.out.print("Documento (CPF): ");
        String document = scanner.nextLine().trim();

        try {
            httpService.register(name, email, password, confirmPassword, document);
            System.out.println("Usuário registrado com sucesso! Agora você pode fazer login.");
        } catch (Exception e) {
            System.out.println("Erro no registro: " + e.getMessage());
        }
    }
}
