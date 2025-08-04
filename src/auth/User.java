package auth;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String document;

    public static final String FILE_NAME = "users.csv";
    private User(int id, String name, String email, String password, String document) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.document = document;
    }

    public static User createUser(String name, String email, String password, String confirmPassword, String document) throws Exception {
        int randomId = (int) (Math.random() * 10000);
        User user = new User(randomId, name, email, password, document);

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(FILE_NAME, true);
        } catch (IOException e) {
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
            throw new Exception("Falha ao escrever no arquivo de usuários.");
        }
        return user;
    }

    public static User login(String email, String password) throws Exception {
        try {
            User[] users = readUsersFromCsv();
            for (User user : users) {
                if (user.email.equals(email) && user.password.equals(password)) {
                    return user;
                }
            }
            throw new Exception("Email ou senha incorretos.");
        } catch (Exception e) {
            throw new Exception("Erro ao ler o arquivo de usuários.");
        }
    }

    public static void writeCsvHeader() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME, true))) {
                String[] header = {"ID", "Name", "Email", "Password", "Document"};
                writer.writeNext(header);
            } catch (IOException e) {
                System.out.println("Erro ao criar o cabeçalho do arquivo CSV.");
            }
        }
    }

    private static User[] readUsersFromCsv() throws Exception {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_NAME))) {
            String[] nextLine;
            boolean isHeader = true;
            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (nextLine.length < 5) continue;
                int id = Integer.parseInt(nextLine[0]);
                User user = new User(
                        id,
                        nextLine[1], // name
                        nextLine[2], // email
                        nextLine[3], // password
                        nextLine[4]  // document
                );
                users.add(user);
            }
        } catch (IOException e) {
            throw new Exception("Erro ao ler o arquivo de usuários.");
        }
        return users.toArray(new User[0]);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', document='%s'}", id, name, email, document);
    }
}
