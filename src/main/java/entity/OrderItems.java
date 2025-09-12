package entity;

public class OrderItems {
    private int id;
    private Order order;
    private Product product;
    private int amount;
    private double value;

    public OrderItems(int id, Order order, Product product, int amount, double value) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.amount = amount;
        this.value = value;
    }

    // Constructor without id (for new order items)
    public OrderItems(Order order, Product product, int amount, double value) {
        this(0, order, product, amount, value);
    }

    // Getters
    public int getId() { return id; }
    public Order getOrder() { return order; }
    public Product getProduct() { return product; }
    public int getAmount() { return amount; }
    public double getValue() { return value; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setOrder(Order order) { this.order = order; }
    public void setProduct(Product product) { this.product = product; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setValue(double value) { this.value = value; }

    // Helper method to calculate total value for this line item
    public double getTotalValue() {
        return value * amount;
    }
}
