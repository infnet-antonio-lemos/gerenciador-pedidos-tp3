package entity;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String document;

    public User(int id, String name, String email, String password, String document) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.document = document;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDocument() { return document; }
}

