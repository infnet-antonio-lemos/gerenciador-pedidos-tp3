import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import controller.AuthHttpController;
import controller.ProductHttpController;
import business.AuthBusiness;
import business.ProductBusiness;
import repository.UserRepository;
import repository.ProductRepository;
import auth.SimpleTokenManager;

public class Main {
    public static void main(String[] args) {
        // Initialize dependencies
        UserRepository userRepository = new UserRepository();
        AuthBusiness authBusiness = new AuthBusiness(userRepository);
        AuthHttpController authHttpController = new AuthHttpController(authBusiness);

        ProductRepository productRepository = new ProductRepository();
        ProductBusiness productBusiness = new ProductBusiness(productRepository);
        ProductHttpController productHttpController = new ProductHttpController(productBusiness);

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7000);

        // Authentication middleware for protected routes
        app.before("/profile", ctx -> requireAuth(ctx));
        app.before("/products/*", ctx -> requireAuth(ctx));
        app.before("/protected", ctx -> requireAuth(ctx));

        // Public routes (no authentication required)
        app.get("/health", ctx -> {
            ctx.json("{\"status\": \"OK\", \"message\": \"Server is running\"}");
        });

        // Auth routes (public)
        app.post("/auth/register", authHttpController::createUser);
        app.post("/auth/login", authHttpController::login);
        app.post("/auth/logout", authHttpController::logout);

        // Protected auth routes
        app.get("/profile", authHttpController::getProfile);

        // Product CRUD routes (all protected)
        app.post("/products", productHttpController::createProduct);
        app.get("/products", productHttpController::getAllProducts);
        app.get("/products/{id}", productHttpController::getProductById);
        app.put("/products/{id}", productHttpController::updateProduct);
        app.delete("/products/{id}", productHttpController::deleteProduct);

        // Other protected routes
        app.get("/protected", ctx -> {
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
        System.out.println("=== Product CRUD Routes (require Authorization header) ===");
        System.out.println("Create Product: POST http://localhost:7000/products");
        System.out.println("List Products: GET http://localhost:7000/products");
        System.out.println("Get Product: GET http://localhost:7000/products/{id}");
        System.out.println("Update Product: PUT http://localhost:7000/products/{id}");
        System.out.println("Delete Product: DELETE http://localhost:7000/products/{id}");
        System.out.println("Protected: GET http://localhost:7000/protected");
    }

    private static void requireAuth(io.javalin.http.Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedResponse("Token de autenticação obrigatório");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!SimpleTokenManager.isValidToken(token)) {
            throw new UnauthorizedResponse("Token inválido ou expirado");
        }

        // Store user ID in context for use in handlers
        Integer userId = SimpleTokenManager.getUserIdFromToken(token);
        ctx.attribute("userId", userId);
    }
}