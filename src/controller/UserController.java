package controller;

import business.UserBusiness;
import entity.User;

public class UserController {
    private UserBusiness userBusiness;

    public UserController(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public User createUser(String name, String email, String password, String confirmPassword, String document) throws Exception {
        return userBusiness.createUser(name, email, password, confirmPassword, document);
    }

    public User login(String email, String password) throws Exception {
        return userBusiness.login(email, password);
    }
}
