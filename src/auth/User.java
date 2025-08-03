package auth;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String document;

    public static final String FILE_NAME = "users.csv";
    private User(String name, String email, String password, String document) {
        this.id = (int) (Math.random() * 10000);
        this.name = name;
        this.email = email;
        this.password = password;
        this.document = document;
    }

    public static User createUser(String name, String email, String password, String confirmPassword, String document) throws Exception {
        User user = new User(name, email, password, document);

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(FILE_NAME, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Falha ao criar o arquivo de usuários.");
        }
        try (CSVWriter writer = new CSVWriter(fileWriter)) {
            String[] line = {
                    String.valueOf(user.id),
                    user.name,
                    user.email,
                    user.password,
                    user.document
            };
            writer.writeNext(line);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Falha ao escrever no arquivo de usuários.");
        }
        return user;
    }

    public static String login(String email, String password) {
        return "Brabo";
    }

    public static void writeCsvHeader() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME, true))) {
                String[] header = {"ID", "Name", "Email", "Password", "Document"};
                writer.writeNext(header);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erro ao criar o cabeçalho do arquivo CSV.");
            }
        }
    }
}
