package controller;

import business.AuthBusiness;
import entity.User;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import auth.SimpleTokenManager;

public class AuthHttpController {
    private final AuthBusiness authBusiness;
    private final ObjectMapper objectMapper;

    public AuthHttpController(AuthBusiness authBusiness) {
        this.authBusiness = authBusiness;
        this.objectMapper = new ObjectMapper();
    }

    public void createUser(Context ctx) {
        try {
            System.out.println("[AUTH] Creating user - Request body: " + ctx.body());

            JsonNode json = objectMapper.readTree(ctx.body());
            System.out.println("[AUTH] Parsed JSON successfully");

            // Validate required fields exist
            if (json.get("name") == null) {
                System.err.println("[AUTH] ERROR: Missing 'name' field");
                ctx.status(400).json(new ErrorResponse("Campo 'name' é obrigatório"));
                return;
            }
            if (json.get("email") == null) {
                System.err.println("[AUTH] ERROR: Missing 'email' field");
                ctx.status(400).json(new ErrorResponse("Campo 'email' é obrigatório"));
                return;
            }
            if (json.get("password") == null) {
                System.err.println("[AUTH] ERROR: Missing 'password' field");
                ctx.status(400).json(new ErrorResponse("Campo 'password' é obrigatório"));
                return;
            }
            if (json.get("confirmPassword") == null) {
                System.err.println("[AUTH] ERROR: Missing 'confirmPassword' field");
                ctx.status(400).json(new ErrorResponse("Campo 'confirmPassword' é obrigatório"));
                return;
            }
            if (json.get("document") == null) {
                System.err.println("[AUTH] ERROR: Missing 'document' field");
                ctx.status(400).json(new ErrorResponse("Campo 'document' é obrigatório"));
                return;
            }

            String name = json.get("name").asText();
            String email = json.get("email").asText();
            String password = json.get("password").asText();
            String confirmPassword = json.get("confirmPassword").asText();
            String document = json.get("document").asText();

            System.out.println("[AUTH] Creating user with email: " + email + ", name: " + name);

            User user = authBusiness.createUser(name, email, password, confirmPassword, document);

            System.out.println("[AUTH] User created successfully with ID: " + user.getId());

            // Return user without password for security
            ctx.status(201).json(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getDocument()));
        } catch (Exception e) {
            System.err.println("[AUTH] ERROR creating user: " + e.getMessage());
            e.printStackTrace();
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    public void login(Context ctx) {
        try {
            System.out.println("[AUTH] Login attempt - Request body: " + ctx.body());

            JsonNode json = objectMapper.readTree(ctx.body());
            System.out.println("[AUTH] Parsed JSON successfully");

            // Validate required fields exist
            if (json.get("email") == null) {
                System.err.println("[AUTH] ERROR: Missing 'email' field in login");
                ctx.status(400).json(new ErrorResponse("Campo 'email' é obrigatório"));
                return;
            }
            if (json.get("password") == null) {
                System.err.println("[AUTH] ERROR: Missing 'password' field in login");
                ctx.status(400).json(new ErrorResponse("Campo 'password' é obrigatório"));
                return;
            }

            String email = json.get("email").asText();
            String password = json.get("password").asText();

            System.out.println("[AUTH] Attempting login for email: " + email);

            User user = authBusiness.login(email, password);

            System.out.println("[AUTH] Login successful for user ID: " + user.getId());

            // Generate authentication token
            String token = SimpleTokenManager.generateToken(user.getId());
            System.out.println("[AUTH] Generated token for user: " + user.getId());

            // Return user data with token
            ctx.status(200).json(new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDocument(),
                token
            ));
        } catch (Exception e) {
            System.err.println("[AUTH] ERROR during login: " + e.getMessage());
            e.printStackTrace();
            ctx.status(401).json(new ErrorResponse(e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        try {
            System.out.println("[AUTH] Logout attempt");

            String token = ctx.header("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove "Bearer " prefix
                System.out.println("[AUTH] Removing token for logout");
                SimpleTokenManager.removeToken(token);
            } else {
                System.out.println("[AUTH] No valid token found for logout");
            }

            System.out.println("[AUTH] Logout successful");
            ctx.status(200).json(new MessageResponse("Logout realizado com sucesso"));
        } catch (Exception e) {
            System.err.println("[AUTH] ERROR during logout: " + e.getMessage());
            e.printStackTrace();
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    public void getProfile(Context ctx) {
        try {
            System.out.println("[AUTH] Getting user profile");

            // Authentication is handled by middleware, get userId from context
            Integer userId = ctx.attribute("userId");
            if (userId == null) {
                System.err.println("[AUTH] ERROR: No userId in context for profile request");
                ctx.status(401).json(new ErrorResponse("Usuário não autenticado"));
                return;
            }

            System.out.println("[AUTH] Getting profile for user ID: " + userId);

            User user = authBusiness.getUserById(userId);

            System.out.println("[AUTH] Profile retrieved successfully for user: " + user.getEmail());

            // Return user profile without password for security
            ctx.status(200).json(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getDocument()));
        } catch (Exception e) {
            System.err.println("[AUTH] ERROR getting profile: " + e.getMessage());
            e.printStackTrace();
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
