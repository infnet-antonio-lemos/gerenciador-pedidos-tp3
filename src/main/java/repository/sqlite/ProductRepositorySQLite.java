package repository.sqlite;

import entity.Product;
import repository.RepositoryInterface;
import repository.sqlite.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductRepositorySQLite implements RepositoryInterface<Product> {

    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products (name, value, description, available_amount, image, deleted_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getValue());
            pstmt.setString(3, product.getDescription());
            pstmt.setInt(4, product.getAvailableAmount());
            pstmt.setString(5, product.getImage());
            pstmt.setTimestamp(6, product.getDeletedAt() != null ? new Timestamp(product.getDeletedAt().getTime()) : null);

            pstmt.executeUpdate();

            // Get the generated ID using SQLite's last_insert_rowid() function
            String getIdSql = "SELECT last_insert_rowid()";
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }

            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar produto", e);
        }
    }

    @Override
    public Product update(Product product) {
        String sql = "UPDATE products SET name = ?, value = ?, description = ?, available_amount = ?, image = ?, deleted_at = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getValue());
            pstmt.setString(3, product.getDescription());
            pstmt.setInt(4, product.getAvailableAmount());
            pstmt.setString(5, product.getImage());
            pstmt.setTimestamp(6, product.getDeletedAt() != null ? new Timestamp(product.getDeletedAt().getTime()) : null);
            pstmt.setInt(7, product.getId());

            pstmt.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
    }

    @Override
    public List<Product> list() {
        return findAll(false); // By default, don't include deleted products
    }

    @Override
    public Product get(int id) {
        return findById(id, false); // By default, don't include deleted products
    }

    @Override
    public void delete(int id) {
        // Soft delete - set deleted_at timestamp
        String sql = "UPDATE products SET deleted_at = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }

    public List<Product> findAll(boolean includeDeleted) {
        String sql = "SELECT * FROM products";
        if (!includeDeleted) {
            sql += " WHERE deleted_at IS NULL";
        }

        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }

        return products;
    }

    public Product findById(int id, boolean includeDeleted) {
        String sql = "SELECT * FROM products WHERE id = ?";
        if (!includeDeleted) {
            sql += " AND deleted_at IS NULL";
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto", e);
        }

        return null;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Timestamp deletedAtTimestamp = rs.getTimestamp("deleted_at");
        Date deletedAt = deletedAtTimestamp != null ? new Date(deletedAtTimestamp.getTime()) : null;

        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getDouble("value"),
            rs.getString("description"),
            rs.getInt("available_amount"),
            rs.getString("image"),
            deletedAt
        );
    }
}
