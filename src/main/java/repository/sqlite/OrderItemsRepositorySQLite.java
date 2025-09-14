package repository.sqlite;

import entity.OrderItems;
import entity.Order;
import entity.Product;
import repository.RepositoryInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsRepositorySQLite implements RepositoryInterface<OrderItems> {
    public OrderItemsRepositorySQLite() {}

    @Override
    public OrderItems create(OrderItems orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, amount, value) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderItem.getOrder().getId());
            pstmt.setInt(2, orderItem.getProduct().getId());
            pstmt.setInt(3, orderItem.getAmount());
            pstmt.setDouble(4, orderItem.getValue());

            pstmt.executeUpdate();

            // Get the generated ID using SQLite's last_insert_rowid() function
            String getIdSql = "SELECT last_insert_rowid()";
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    orderItem.setId(rs.getInt(1));
                }
            }

            return orderItem;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar item do pedido", e);
        }
    }

    @Override
    public OrderItems update(OrderItems orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, amount = ?, value = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderItem.getOrder().getId());
            pstmt.setInt(2, orderItem.getProduct().getId());
            pstmt.setInt(3, orderItem.getAmount());
            pstmt.setDouble(4, orderItem.getValue());
            pstmt.setInt(5, orderItem.getId());

            pstmt.executeUpdate();
            return orderItem;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item do pedido", e);
        }
    }

    @Override
    public List<OrderItems> list() {
        String sql = "SELECT * FROM order_items";
        List<OrderItems> orderItems = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItems(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens do pedido", e);
        }

        return orderItems;
    }

    @Override
    public OrderItems get(int id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrderItems(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item do pedido", e);
        }

        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item do pedido", e);
        }
    }

    // Additional methods for compatibility with existing code
    public List<OrderItems> getByOrderId(int orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItems> orderItems = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderItems.add(mapResultSetToOrderItems(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por pedido", e);
        }

        return orderItems;
    }

    public List<OrderItems> getByProductId(int productId) {
        String sql = "SELECT * FROM order_items WHERE product_id = ?";
        List<OrderItems> orderItems = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderItems.add(mapResultSetToOrderItems(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por produto", e);
        }

        return orderItems;
    }

    public void deleteByOrderId(int orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar itens por pedido", e);
        }
    }

    public OrderItems getById(int id) {
        return get(id);
    }

    public OrderItems findById(int id) {
        return get(id);
    }

    public List<OrderItems> findAll() {
        return list();
    }

    public List<OrderItems> find() {
        return list();
    }

    private OrderItems mapResultSetToOrderItems(ResultSet rs) throws SQLException {
        try {
            int orderId = rs.getInt("order_id");
            int productId = rs.getInt("product_id");

            // Create minimal objects with just IDs - NO database calls during mapping
            Order order = new Order();
            order.setId(orderId);

            Product product = new Product();
            product.setId(productId);
            // Set a placeholder name to avoid null issues
            product.setName("Product " + productId);

            return new OrderItems(
                rs.getInt("id"),
                order,
                product,
                rs.getInt("amount"),
                rs.getDouble("value")
            );
        } catch (SQLException e) {
            System.err.println("[ORDER_ITEMS_REPO] Error mapping ResultSet to OrderItems: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao mapear item do pedido: " + e.getMessage(), e);
        }
    }
}
