import java.util.Scanner;
import auth.User;

public class Main {
    public static void main(String[] args) {
        mainMenu();
    }

    public static void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Criar usuário");
            System.out.println("2. Fazer login");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Digite o nome: ");
                    String name = scanner.next();
                    System.out.print("Digite o email: ");
                    String email = scanner.next();
                    System.out.print("Digite a senha: ");
                    String password = scanner.next();
                    System.out.print("Confirme a senha: ");
                    String confirmPassword = scanner.next();
                    System.out.print("Digite o documento: ");
                    String document = scanner.next();

                    try {
                    User.createUser(name, email, password, confirmPassword, document);
                    } catch (Exception e) {
                        System.out.println("Erro ao criar usuário: " + e.getMessage());
                        continue;
                    }
                    System.out.println("Usuário criado com sucesso!");
                    break;
                case 2:
                    System.out.print("Digite o email: ");
                    String loginEmail = scanner.next();
                    System.out.print("Digite a senha: ");
                    String loginPassword = scanner.next();

                    User.login(loginEmail, loginPassword);
                    System.out.println("Login realizado com sucesso!");
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