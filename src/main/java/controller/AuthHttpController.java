package controller;

import business.AuthBusiness;
import entity.User;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthHttpController {
    private AuthBusiness authBusiness;
    private ObjectMapper objectMapper;

    public AuthHttpController(AuthBusiness authBusiness) {
        this.authBusiness = authBusiness;
        this.objectMapper = new ObjectMapper();
    }

    public void createUser(Context ctx) {
        try {
            JsonNode json = objectMapper.readTree(ctx.body());
            String name = json.get("name").asText();
            String email = json.get("email").asText();
            String password = json.get("password").asText();
            String confirmPassword = json.get("confirmPassword").asText();
            String document = json.get("document").asText();

            User user = authBusiness.createUser(name, email, password, confirmPassword, document);

            // Return user without password for security
            ctx.status(201).json(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getDocument()));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    public void login(Context ctx) {
        try {
            JsonNode json = objectMapper.readTree(ctx.body());
            String email = json.get("email").asText();
            String password = json.get("password").asText();

            User user = authBusiness.login(email, password);

            // Return user without password for security
            ctx.status(200).json(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getDocument()));
        } catch (Exception e) {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        }
    }

    // Response DTOs
    public static class UserResponse {
        public int id;
        public String name;
        public String email;
        public String document;

        public UserResponse(int id, String name, String email, String document) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.document = document;
        }
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
