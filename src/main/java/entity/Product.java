package entity;

import java.util.Date;

public class Product {
    private int id;
    private String name;
    private double value;
    private String description;
    private int availableAmount;
    private String image;
    private Date deletedAt;

    public Product(int id, String name, double value, String description, int availableAmount, String image, Date deletedAt) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.description = description;
        this.availableAmount = availableAmount;
        this.image = image;
        this.deletedAt = deletedAt;
    }

    // Constructor without deletedAt (for new products)
    public Product(int id, String name, double value, String description, int availableAmount, String image) {
        this(id, name, value, description, availableAmount, image, null);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getValue() { return value; }
    public String getDescription() { return description; }
    public int getAvailableAmount() { return availableAmount; }
    public String getImage() { return image; }
    public Date getDeletedAt() { return deletedAt; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setValue(double value) { this.value = value; }
    public void setDescription(String description) { this.description = description; }
    public void setAvailableAmount(int availableAmount) { this.availableAmount = availableAmount; }
    public void setImage(String image) { this.image = image; }
    public void setDeletedAt(Date deletedAt) { this.deletedAt = deletedAt; }

    // Helper method to check if product is deleted
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
