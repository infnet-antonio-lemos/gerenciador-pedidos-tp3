import io.javalin.Javalin;
import controller.AuthHttpController;
import business.AuthBusiness;
import repository.UserRepository;

public class Main {
    public static void main(String[] args) {
        // Initialize dependencies
        UserRepository userRepository = new UserRepository();
        AuthBusiness authBusiness = new AuthBusiness(userRepository);
        AuthHttpController authHttpController = new AuthHttpController(authBusiness);

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7000);

        // Public routes (no authentication required)
        app.get("/health", ctx -> {
            ctx.json("{\"status\": \"OK\", \"message\": \"Server is running\"}");
        });

        // Auth routes
        app.post("/auth/register", authHttpController::createUser);
        app.post("/auth/login", authHttpController::login);
        app.post("/auth/logout", authHttpController::logout);

        // Protected routes (authentication required)
        app.get("/profile", ctx -> {
            AuthHttpController.requireAuth(ctx);
            Integer userId = ctx.attribute("userId");
            ctx.json("{\"message\": \"This is your protected profile\", \"userId\": " + userId + "}");
        });

        app.get("/protected", ctx -> {
            AuthHttpController.requireAuth(ctx);
            ctx.json("{\"message\": \"This is a protected route - you are authenticated!\"}");
        });

        System.out.println("Server started on http://localhost:7000");
        System.out.println("=== Public Routes ===");
        System.out.println("Health check: GET http://localhost:7000/health");
        System.out.println("Register: POST http://localhost:7000/auth/register");
        System.out.println("Login: POST http://localhost:7000/auth/login");
        System.out.println("Logout: POST http://localhost:7000/auth/logout");
        System.out.println("=== Protected Routes (require Authorization header) ===");
        System.out.println("Profile: GET http://localhost:7000/profile");
        System.out.println("Protected: GET http://localhost:7000/protected");
    }
}