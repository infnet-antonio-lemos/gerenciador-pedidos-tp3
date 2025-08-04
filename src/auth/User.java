package auth;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private final int id;
    private final String name;
    private final String email;
    private final String password;
    private final String document;

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
        if (!password.equals(confirmPassword)) {
            throw new Exception("As senhas não coincidem.");
        }
        User user = new User(randomId, name, email, password, document);

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(FILE_NAME, true);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar o arquivo de usuários.", e);
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
            logger.log(Level.SEVERE, "Erro ao escrever no arquivo de usuários.", e);
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
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao ler o arquivo de usuários.", e);
            throw new Exception("Erro ao ler o arquivo de usuários.");
        } catch (CsvValidationException e) {
            logger.log(Level.SEVERE, "Erro ao validar o CSV de usuários.", e);
            throw new Exception("Erro ao validar o CSV de usuários.");
        }
    }

    public static void writeCsvHeader() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME, true))) {
                String[] header = {"ID", "Name", "Email", "Password", "Document"};
                writer.writeNext(header);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erro ao criar o cabeçalho do arquivo CSV.", e);
                System.out.println("Erro ao criar o cabeçalho do arquivo CSV.");
            }
        }
    }

    private static User[] readUsersFromCsv() throws IOException, CsvValidationException {
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
        }
        return users.toArray(new User[0]);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', document='%s'}", id, name, email, document);
    }

    private static final Logger logger = Logger.getLogger(User.class.getName());
}
