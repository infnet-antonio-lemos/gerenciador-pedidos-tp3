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

    // Constructor for new orders (without timestamps)
    public Order(int id, User user, Address address, OrderStatus orderStatus) {
        this(id, user, address, orderStatus, new Date(), new Date());
    }

    // Convenience constructor with String values (for backward compatibility)
    public Order(int id, User user, Address address, String orderStatus) {
        this(id, user, address, OrderStatus.valueOf(orderStatus), new Date(), new Date());
    }

    // Custom setter that updates timestamp
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.updatedAt = new Date();
    }

    // Convenience setter with String value (for backward compatibility)
    public void setOrderStatus(String orderStatus) {
        setOrderStatus(OrderStatus.valueOf(orderStatus));
    }

    // Deprecated methods for backward compatibility with old 3-status system
    @Deprecated
    public String getPaymentStatus() {
        // Map order status to payment status for backward compatibility
        switch (orderStatus) {
            case PENDING: return "PENDING";
            case PAID:
            case PROCESSING:
            case SHIPPED:
            case DELIVERED: return "PAID";
            case REFUNDED: return "REFUNDED";
            case CANCELLED: return "CANCELLED";
            default: return "PENDING";
        }
    }

    @Deprecated
    public String getShippingStatus() {
        // Map order status to shipping status for backward compatibility
        switch (orderStatus) {
            case PENDING:
            case PAID:
            case PROCESSING: return "PENDING";
            case SHIPPED: return "SHIPPED";
            case DELIVERED: return "DELIVERED";
            case CANCELLED:
            case REFUNDED: return "CANCELLED";
            default: return "PENDING";
        }
    }

    @Deprecated
    public void setPaymentStatus(String paymentStatus) {
        // For backward compatibility - ignore this call or map to order status
        // This maintains API compatibility while internally using single status
    }

    @Deprecated
    public void setShippingStatus(String shippingStatus) {
        // For backward compatibility - ignore this call or map to order status
        // This maintains API compatibility while internally using single status
    }
}
