package entity;

import java.util.Date;

public class Order {
    private int id;
    private User user;
    private Address address;
    private String paymentStatus;
    private String shippingStatus;
    private String orderStatus;
    private Date createdAt;
    private Date updatedAt;

    // Default constructor
    public Order() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Order(int id, User user, Address address, String paymentStatus, String shippingStatus, String orderStatus, Date createdAt, Date updatedAt) {
        this.id = id;
        this.user = user;
        this.address = address;
        this.paymentStatus = paymentStatus;
        this.shippingStatus = shippingStatus;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for new orders (without timestamps)
    public Order(int id, User user, Address address, String paymentStatus, String shippingStatus, String orderStatus) {
        this(id, user, address, paymentStatus, shippingStatus, orderStatus, new Date(), new Date());
    }

    // Getters
    public int getId() { return id; }
    public User getUser() { return user; }
    public Address getAddress() { return address; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getShippingStatus() { return shippingStatus; }
    public String getOrderStatus() { return orderStatus; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAddress(Address address) { this.address = address; }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.updatedAt = new Date();
    }
    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
        this.updatedAt = new Date();
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        this.updatedAt = new Date();
    }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
