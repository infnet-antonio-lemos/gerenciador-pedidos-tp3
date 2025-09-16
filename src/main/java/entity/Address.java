package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private int id;
    private User user;
    private String street;
    private String number;
    private String neighborhood;
    private String zipCode;
    private String complement;
    private String city;
    private String state;

    // Constructor with userId instead of User object (for CSV loading)
    public Address(int id, int userId, String street, String number, String neighborhood,
                   String zipCode, String complement, String city, String state) {
        this.id = id;
        this.user = new User(userId, "", "", "", ""); // Temporary user object with just ID
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.zipCode = zipCode;
        this.complement = complement;
        this.city = city;
        this.state = state;
    }

    // Helper method to get userId
    public int getUserId() {
        return user != null ? user.getId() : 0;
    }
}
