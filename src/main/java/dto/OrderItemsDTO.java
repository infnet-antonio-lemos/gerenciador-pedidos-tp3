package dto;

public class OrderItemsDTO {
    private int productId;
    private double currentValue;
    private int amount;

    public OrderItemsDTO() {}

    public OrderItemsDTO(int productId, double currentValue, int amount) {
        this.productId = productId;
        this.currentValue = currentValue;
        this.amount = amount;
    }

    // Getters
    public int getProductId() { return productId; }
    public double getCurrentValue() { return currentValue; }
    public int getAmount() { return amount; }

    // Setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }
    public void setAmount(int amount) { this.amount = amount; }

    // Helper method to calculate total value for this line item
    public double getTotalValue() {
        return currentValue * amount;
    }
}
