package repository.sqlite;

import entity.Address;
import entity.User;
import repository.RepositoryInterface;
import repository.sqlite.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressRepositorySQLite implements RepositoryInterface<Address> {

    @Override
    public Address create(Address address) {
        String sql = "INSERT INTO addresses (user_id, street, number, neighborhood, zip_code, complement, city, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, address.getUserId());
            pstmt.setString(2, address.getStreet());
            pstmt.setString(3, address.getNumber());
            pstmt.setString(4, address.getNeighborhood());
            pstmt.setString(5, address.getZipCode());
            pstmt.setString(6, address.getComplement());
            pstmt.setString(7, address.getCity());
            pstmt.setString(8, address.getState());

            pstmt.executeUpdate();

            // Get the generated ID using SQLite's last_insert_rowid() function
            String getIdSql = "SELECT last_insert_rowid()";
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    address.setId(rs.getInt(1));
                }
            }

            return address;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar endereço", e);
        }
    }

    @Override
    public Address update(Address address) {
        String sql = "UPDATE addresses SET user_id = ?, street = ?, number = ?, neighborhood = ?, zip_code = ?, complement = ?, city = ?, state = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, address.getUserId());
            pstmt.setString(2, address.getStreet());
            pstmt.setString(3, address.getNumber());
            pstmt.setString(4, address.getNeighborhood());
            pstmt.setString(5, address.getZipCode());
            pstmt.setString(6, address.getComplement());
            pstmt.setString(7, address.getCity());
            pstmt.setString(8, address.getState());
            pstmt.setInt(9, address.getId());

            pstmt.executeUpdate();
            return address;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar endereço", e);
        }
    }

    @Override
    public List<Address> list() {
        String sql = "SELECT * FROM addresses";
        List<Address> addresses = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                addresses.add(mapResultSetToAddress(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar endereços", e);
        }

        return addresses;
    }

    @Override
    public Address get(int id) {
        String sql = "SELECT * FROM addresses WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar endereço", e);
        }

        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM addresses WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar endereço", e);
        }
    }

    // Additional methods for compatibility with existing code
    public List<Address> findByUserId(int userId) {
        String sql = "SELECT * FROM addresses WHERE user_id = ?";
        List<Address> addresses = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar endereços por usuário", e);
        }

        return addresses;
    }

    public Address findById(int id) {
        return get(id);
    }

    public List<Address> findAll() {
        return list();
    }

    public void save(Address address) {
        if (address.getId() == 0) {
            create(address);
        } else {
            update(address);
        }
    }

    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        return new Address(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("street"),
            rs.getString("number"),
            rs.getString("neighborhood"),
            rs.getString("zip_code"),
            rs.getString("complement"),
            rs.getString("city"),
            rs.getString("state")
        );
    }
}
