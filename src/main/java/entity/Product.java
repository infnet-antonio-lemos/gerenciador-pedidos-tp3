package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int id;
    private String name;
    private double value;
    private String description;
    private int availableAmount;
    private String image;
    private Date deletedAt;

    // Constructor without deletedAt (for new products)
    public Product(int id, String name, double value, String description, int availableAmount, String image) {
        this(id, name, value, description, availableAmount, image, null);
    }

    // Helper method to check if product is deleted
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
