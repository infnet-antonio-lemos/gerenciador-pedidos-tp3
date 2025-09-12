package controller;

import business.AuthBusiness;
import entity.User;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import auth.SimpleTokenManager;

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

            // Generate authentication token
            String token = SimpleTokenManager.generateToken(user.getId());

            // Return user data with token
            ctx.status(200).json(new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDocument(),
                token
            ));
        } catch (Exception e) {
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        try {
            String token = ctx.header("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove "Bearer " prefix
                SimpleTokenManager.removeToken(token);
            }
            ctx.status(200).json(new MessageResponse("Logout realizado com sucesso"));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    // Authentication middleware helper
    public static void requireAuth(Context ctx) throws Exception {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.status(401).json(new ErrorResponse("Token de autenticação obrigatório"));
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!SimpleTokenManager.isValidToken(token)) {
            ctx.status(401).json(new ErrorResponse("Token inválido ou expirado"));
            return;
        }

        // Store user ID in context for use in handlers
        Integer userId = SimpleTokenManager.getUserIdFromToken(token);
        ctx.attribute("userId", userId);
    }

    // Response classes
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

    public static class LoginResponse {
        public int id;
        public String name;
        public String email;
        public String document;
        public String token;

        public LoginResponse(int id, String name, String email, String document, String token) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.document = document;
            this.token = token;
        }
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }
}
