package repository.sqlite;

import entity.Order;
import entity.User;
import entity.Address;
import repository.RepositoryInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderRepositorySQLite implements RepositoryInterface<Order> {
    @Override
    public Order create(Order order) {
        String sql = "INSERT INTO orders (user_id, address_id, order_status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getUser().getId());
            pstmt.setInt(2, order.getAddress().getId());
            pstmt.setString(3, order.getOrderStatus().toString());
            pstmt.setTimestamp(4, new Timestamp(order.getCreatedAt().getTime()));
            pstmt.setTimestamp(5, new Timestamp(order.getUpdatedAt().getTime()));

            pstmt.executeUpdate();

            // Get the generated ID using SQLite's last_insert_rowid() function
            String getIdSql = "SELECT last_insert_rowid()";
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    order.setId(rs.getInt(1));
                }
            }

            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar pedido", e);
        }
    }

    @Override
    public Order update(Order order) {
        String sql = "UPDATE orders SET user_id = ?, address_id = ?, order_status = ?, created_at = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getUser().getId());
            pstmt.setInt(2, order.getAddress().getId());
            pstmt.setString(3, order.getOrderStatus().toString());
            pstmt.setTimestamp(4, new Timestamp(order.getCreatedAt().getTime()));
            pstmt.setTimestamp(5, new Timestamp(order.getUpdatedAt().getTime()));
            pstmt.setInt(6, order.getId());

            pstmt.executeUpdate();
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido", e);
        }
    }

    @Override
    public List<Order> list() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos", e);
        }

        return orders;
    }

    @Override
    public Order get(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido", e);
        }

        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pedido", e);
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        int addressId = rs.getInt("address_id");

        // Create minimal objects with just IDs - NO database calls during mapping
        User user = new User();
        user.setId(userId);

        Address address = new Address();
        address.setId(addressId);

        // Handle timestamps safely
        Date createdAt = new Date();
        Date updatedAt = new Date();

        try {
            Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
            if (createdAtTimestamp != null) {
                createdAt = new Date(createdAtTimestamp.getTime());
            }
        } catch (SQLException e) {
            System.err.println("[ORDER_REPO] Warning: Could not get created_at timestamp, using current date");
        }

        try {
            Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
            if (updatedAtTimestamp != null) {
                updatedAt = new Date(updatedAtTimestamp.getTime());
            }
        } catch (SQLException e) {
            System.err.println("[ORDER_REPO] Warning: Could not get updated_at timestamp, using current date");
        }

        // Parse order status from database and convert to enum
        String orderStatusString = rs.getString("order_status");
        Order.OrderStatus orderStatus = Order.OrderStatus.PENDING; // default
        try {
            orderStatus = Order.OrderStatus.valueOf(orderStatusString);
        } catch (Exception e) {
            System.err.println("[ORDER_REPO] Warning: Invalid order status '" + orderStatusString + "', using PENDING");
        }

        return new Order(
            rs.getInt("id"),
            user,
            address,
            orderStatus,
            createdAt,
            updatedAt
        );
    }
}
