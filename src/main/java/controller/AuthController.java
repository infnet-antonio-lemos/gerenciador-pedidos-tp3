package controller;

import business.AuthBusiness;
import entity.User;

public class AuthController {
    private AuthBusiness authBusiness;

    public AuthController(AuthBusiness authBusiness) {
        this.authBusiness = authBusiness;
    }

    public User createUser(String name, String email, String password, String confirmPassword, String document) throws Exception {
        return authBusiness.createUser(name, email, password, confirmPassword, document);
    }

    public User login(String email, String password) throws Exception {
        return authBusiness.login(email, password);
    }

    public User getUserById(int id) throws Exception {
        return authBusiness.getUserById(id);
    }
}
