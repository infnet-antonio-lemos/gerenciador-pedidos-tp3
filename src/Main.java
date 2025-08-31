import java.util.Scanner;

import business.AuthBusiness;
import controller.AuthController;
import entity.User;
import repository.UserRepository;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        AuthBusiness authBusiness = new AuthBusiness(userRepository);
        AuthController authController = new AuthController(authBusiness);
        mainMenu(authController);
    }

    public static void mainMenu(AuthController authController) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Criar usuário");
            System.out.println("2. Fazer login");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Digite o nome: ");
                    String name = scanner.nextLine();
                    System.out.print("Digite o email: ");
                    String email = scanner.nextLine();
                    System.out.print("Digite a senha: ");
                    String password = scanner.nextLine();
                    System.out.print("Confirme a senha: ");
                    String confirmPassword = scanner.nextLine();
                    System.out.print("Digite o documento: ");
                    String document = scanner.nextLine();

                    try {
                        User user = authController.createUser(name, email, password, confirmPassword, document);
                        System.out.println("Usuário criado com sucesso!");
                        System.out.println("ID: " + user.getId() + ", Nome: " + user.getName() + ", Email: " + user.getEmail() + ", Documento: " + user.getDocument());
                        break;
                    } catch (Exception e) {
                        System.out.println("Erro ao criar usuário: " + e.getMessage());
                        continue;
                    }
                case 2:
                    System.out.print("Digite o email: ");
                    String loginEmail = scanner.nextLine();
                    System.out.print("Digite a senha: ");
                    String loginPassword = scanner.nextLine();

                    try {
                        User user = authController.login(loginEmail, loginPassword);
                        System.out.println("Login realizado com sucesso!");
                        authenticatedMenu(user);
                        break;
                    } catch (Exception e) {
                        System.out.println("Erro ao fazer login: " + e.getMessage());
                        continue;
                    }
                case 3:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    public static void authenticatedMenu(User user) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Ver perfil");
            System.out.println("2. Criar pedido");
            System.out.println("3. Sair");
            System.out.println("Escolha uma opção: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1:
                    System.out.println("Perfil do usuário:");
                    System.out.println("ID: " + user.getId() + ", Nome: " + user.getName() + ", Email: " + user.getEmail() + ", Documento: " + user.getDocument());
                    break;
                case 2:
                    System.out.println("Ops! Esta funcionalidade ainda não está implementada.");
                    break;
                case 3:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}