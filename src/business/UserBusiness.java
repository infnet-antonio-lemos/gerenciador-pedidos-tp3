package business;

import entity.User;
import repository.UserRepository;

public class UserBusiness {
    private UserRepository userRepository;

    public UserBusiness(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, String password, String confirmPassword, String document) throws Exception {
        if (name == null || name.isEmpty()) throw new Exception("Nome obrigatório");
        if (email == null || email.isEmpty()) throw new Exception("Email obrigatório");
        if (password == null || password.isEmpty()) throw new Exception("Senha obrigatória");
        if (!password.equals(confirmPassword)) throw new Exception("Senhas não conferem");
        if (document == null || document.isEmpty()) throw new Exception("Documento obrigatório");
        if (userRepository.findByEmail(email) != null) throw new Exception("Email já cadastrado");
        int id = userRepository.getNextId();
        User user = new User(id, name, email, password, document);
        userRepository.save(user);
        return user;
    }

    public User login(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new Exception("Email ou senha incorretos");
        if (!user.getPassword().equals(password)) throw new Exception("Email ou senha incorretos");
        return user;
    }
}
