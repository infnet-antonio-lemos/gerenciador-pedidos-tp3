package entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    // Single simplified order status enum
    public enum OrderStatus {
        PENDING,        // Order created, awaiting payment
        PAID,           // Payment confirmed, being prepared
        PROCESSING,     // Order being prepared for shipping
        SHIPPED,        // Order sent to customer
        DELIVERED,      // Order successfully delivered
        CANCELLED,      // Order cancelled
        REFUNDED        // Order refunded
    }

    private int id;
    private User user;
    private Address address;

    @Setter(AccessLevel.NONE) // We'll provide custom setter
    private OrderStatus orderStatus;

    private Date createdAt;
    private Date updatedAt;

    // Convenience constructor with String values (for backward compatibility)
    public Order(int id, User user, Address address, String orderStatus) {
        this(id, user, address, OrderStatus.valueOf(orderStatus), new Date(), new Date());
    }

    // Custom setter that updates timestamp
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.updatedAt = new Date();
    }
}
