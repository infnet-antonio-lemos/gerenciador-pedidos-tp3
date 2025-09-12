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

        // Health check route
        app.get("/health", ctx -> {
            ctx.json("{\"status\": \"OK\", \"message\": \"Server is running\"}");
        });

        // Auth routes
        app.post("/auth/register", authHttpController::createUser);
        app.post("/auth/login", authHttpController::login);

        System.out.println("Server started on http://localhost:7000");
        System.out.println("Health check available at: http://localhost:7000/health");
        System.out.println("User registration available at: POST http://localhost:7000/auth/register");
        System.out.println("User login available at: POST http://localhost:7000/auth/login");
    }
}