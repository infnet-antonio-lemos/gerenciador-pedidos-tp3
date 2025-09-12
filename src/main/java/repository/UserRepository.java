package repository;

import entity.User;

import java.io.*;
import java.util.*;

public class UserRepository {
    private static final String FILE_NAME = "users.csv";

    public UserRepository() {
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                try (FileWriter fw = new FileWriter(FILE_NAME);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    out.println("id,name,email,password,document");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo CSV: " + e.getMessage());
        }
    }

    public void save(User user) throws IOException {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.printf("%d,%s,%s,%s,%s\n", user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getDocument());
        }
    }

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    users.add(new User(
                        Integer.parseInt(parts[0]),
                        parts[1], parts[2], parts[3], parts[4]
                    ));
                }
            }
        }
        return users;
    }

    public User findByEmail(String email) throws IOException {
        for (User user : findAll()) {
            if (user.getEmail().equalsIgnoreCase(email)) return user;
        }
        return null;
    }

    public int getNextId() throws IOException {
        List<User> users = findAll();
        return users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1;
    }
}
