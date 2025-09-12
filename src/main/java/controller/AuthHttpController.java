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

            // Validate required fields exist
            if (json.get("name") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'name' é obrigatório"));
                return;
            }
            if (json.get("email") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'email' é obrigatório"));
                return;
            }
            if (json.get("password") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'password' é obrigatório"));
                return;
            }
            if (json.get("confirmPassword") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'confirmPassword' é obrigatório"));
                return;
            }
            if (json.get("document") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'document' é obrigatório"));
                return;
            }

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

            // Validate required fields exist
            if (json.get("email") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'email' é obrigatório"));
                return;
            }
            if (json.get("password") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'password' é obrigatório"));
                return;
            }

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

    public void getProfile(Context ctx) {
        try {
            // Authentication is handled by middleware, get userId from context
            Integer userId = ctx.attribute("userId");
            if (userId == null) {
                ctx.status(401).json(new ErrorResponse("Usuário não autenticado"));
                return;
            }

            User user = authBusiness.getUserById(userId);

            // Return user profile without password for security
            ctx.status(200).json(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getDocument()));
        } catch (Exception e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        }
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
