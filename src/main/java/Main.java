import entity.User;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import controller.AuthHttpController;
import controller.ProductHttpController;
import controller.AddressHttpController;
import controller.OrderHttpController;
import business.AuthBusiness;
import business.ProductBusiness;
import business.AddressBusiness;
import business.OrderBusiness;
import repository.RepositoryInterface;
import repository.sqlite.UserRepositorySQLite;
import repository.sqlite.ProductRepositorySQLite;
import repository.sqlite.AddressRepositorySQLite;
import repository.sqlite.OrderRepositorySQLite;
import repository.sqlite.OrderItemsRepositorySQLite;
import auth.SimpleTokenManager;

public class Main {
    public static void main(String[] args) {
        // Initialize SQLite repositories
        RepositoryInterface<User> userRepository = new UserRepositorySQLite();
        AuthBusiness authBusiness = new AuthBusiness(userRepository);
        AuthHttpController authHttpController = new AuthHttpController(authBusiness);

        ProductRepositorySQLite productRepository = new ProductRepositorySQLite();
        ProductBusiness productBusiness = new ProductBusiness(productRepository);
        ProductHttpController productHttpController = new ProductHttpController(productBusiness);

        AddressRepositorySQLite addressRepository = new AddressRepositorySQLite();
        AddressBusiness addressBusiness = new AddressBusiness(addressRepository, userRepository);
        AddressHttpController addressHttpController = new AddressHttpController(addressBusiness);

        OrderRepositorySQLite orderRepository = new OrderRepositorySQLite();
        OrderItemsRepositorySQLite orderItemsRepository = new OrderItemsRepositorySQLite();
        OrderBusiness orderBusiness = new OrderBusiness(orderRepository, orderItemsRepository, userRepository, addressRepository, productRepository);
        OrderHttpController orderHttpController = new OrderHttpController(orderBusiness);

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7000);

        // Authentication middleware for protected routes
        app.before("/profile", ctx -> requireAuth(ctx));
        app.before("/products/*", ctx -> requireAuth(ctx));
        app.before("/products", ctx -> requireAuth(ctx));
        app.before("/addresses/*", ctx -> requireAuth(ctx));
        app.before("/addresses", ctx -> requireAuth(ctx));
        app.before("/users/*/addresses", ctx -> requireAuth(ctx));
        app.before("/orders/*", ctx -> requireAuth(ctx));
        app.before("/orders", ctx -> requireAuth(ctx));
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

        // Address CRUD routes (all protected)
        app.post("/addresses", addressHttpController::createAddress);
        app.get("/addresses", addressHttpController::getAllAddresses);
        app.get("/addresses/{id}", addressHttpController::getAddressById);
        app.put("/addresses/{id}", addressHttpController::updateAddress);
        app.delete("/addresses/{id}", addressHttpController::deleteAddress);

        // Order CRUD routes (all protected)
        app.post("/orders", orderHttpController::createOrder);
        app.get("/orders", orderHttpController::listOrdersByUser);
        app.get("/orders/{id}", orderHttpController::getOrderById);
        app.put("/orders/{id}", orderHttpController::updateOrder);
        app.delete("/orders/{id}", orderHttpController::cancelOrder);

        // User orders route (optional - for accessing specific user's orders)
        app.get("/users/{userId}/orders", orderHttpController::listOrdersByUserId);

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
        System.out.println("=== Address CRUD Routes (require Authorization header) ===");
        System.out.println("Create Address: POST http://localhost:7000/addresses");
        System.out.println("List Addresses: GET http://localhost:7000/addresses");
        System.out.println("Get Address: GET http://localhost:7000/addresses/{id}");
        System.out.println("Update Address: PUT http://localhost:7000/addresses/{id}");
        System.out.println("Delete Address: DELETE http://localhost:7000/addresses/{id}");
        System.out.println("User Addresses: GET http://localhost:7000/users/{userId}/addresses");
        System.out.println("Protected: GET http://localhost:7000/protected");
        System.out.println("=== Order CRUD Routes (require Authorization header) ===");
        System.out.println("Create Order: POST http://localhost:7000/orders");
        System.out.println("List Orders: GET http://localhost:7000/orders");
        System.out.println("Get Order: GET http://localhost:7000/orders/{id}");
        System.out.println("Update Order: PUT http://localhost:7000/orders/{id}");
        System.out.println("Cancel Order: DELETE http://localhost:7000/orders/{id}");
        System.out.println("User Orders: GET http://localhost:7000/users/{userId}/orders");
        System.out.println("=== API Documentation ===");
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
