package business;

import entity.User;
import repository.RepositoryInterface;

public class AuthBusiness {
    private RepositoryInterface<User> userRepository;

    public AuthBusiness(RepositoryInterface<User> userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, String password, String confirmPassword, String document) throws Exception {
        if (name == null || name.isEmpty()) throw new Exception("Nome obrigatório");
        if (email == null || email.isEmpty()) throw new Exception("Email obrigatório");
        if (password == null || password.isEmpty()) throw new Exception("Senha obrigatória");
        if (!password.equals(confirmPassword)) throw new Exception("Senhas não conferem");
        if (document == null || document.isEmpty()) throw new Exception("Documento obrigatório");

        // Note: We'll need to add findByEmail to the interface or handle this differently
        // For now, we'll check all users to find by email
        for (User existingUser : userRepository.list()) {
            if (existingUser.getEmail().equals(email)) {
                throw new Exception("Email já cadastrado");
            }
        }

        User user = new User(0, name, email, password, document); // ID will be set by create method
        return userRepository.create(user);
    }

    public User login(String email, String password) throws Exception {
        // Search through all users to find by email
        User foundUser = null;
        for (User user : userRepository.list()) {
            if (user.getEmail().equals(email)) {
                foundUser = user;
                break;
            }
        }

        if (foundUser == null) throw new Exception("Email ou senha incorretos");
        if (!foundUser.getPassword().equals(password)) throw new Exception("Email ou senha incorretos");
        return foundUser;
    }

    public User getUserById(int id) throws Exception {
        User user = userRepository.get(id);
        if (user == null) throw new Exception("Usuário não encontrado");
        return user;
    }
}
