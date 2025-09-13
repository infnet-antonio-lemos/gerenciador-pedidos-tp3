package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItems {
    private int id;
    private Order order;
    private Product product;
    private int amount;
    private double value;

    // Constructor without id (for new order items)
    public OrderItems(Order order, Product product, int amount, double value) {
        this(0, order, product, amount, value);
    }

    // Helper method to calculate total value for this line item
    public double getTotalValue() {
        return value * amount;
    }
}
