package repository.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:gerenciador_pedidos.db";
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
                initializeTables();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados", e);
        }
    }

    private static void initializeTables() {
        try (Statement stmt = connection.createStatement()) {
            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    document TEXT NOT NULL
                )
                """);

            // Products table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    value REAL NOT NULL,
                    description TEXT NOT NULL,
                    available_amount INTEGER NOT NULL,
                    image TEXT,
                    deleted_at DATETIME
                )
                """);

            // Addresses table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS addresses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    street TEXT NOT NULL,
                    number TEXT NOT NULL,
                    neighborhood TEXT NOT NULL,
                    zip_code TEXT NOT NULL,
                    complement TEXT,
                    city TEXT NOT NULL,
                    state TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
                """);

            // Orders table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    address_id INTEGER NOT NULL,
                    order_status TEXT NOT NULL,
                    created_at DATETIME NOT NULL,
                    updated_at DATETIME NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (address_id) REFERENCES addresses(id)
                )
                """);

            // Order items table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    amount INTEGER NOT NULL,
                    value REAL NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                )
                """);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar tabelas do banco de dados", e);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão com o banco de dados: " + e.getMessage());
        }
    }
}
