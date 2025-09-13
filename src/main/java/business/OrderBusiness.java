package business;

import entity.*;
import dto.*;
import repository.RepositoryInterface;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

public class OrderBusiness {
    private RepositoryInterface<Order> orderRepository;
    private RepositoryInterface<OrderItems> orderItemsRepository;
    private RepositoryInterface<User> userRepository;
    private RepositoryInterface<Address> addressRepository;
    private RepositoryInterface<Product> productRepository;

    public OrderBusiness(RepositoryInterface<Order> orderRepository, RepositoryInterface<OrderItems> orderItemsRepository,
                        RepositoryInterface<User> userRepository, RepositoryInterface<Address> addressRepository,
                        RepositoryInterface<Product> productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(int userId, AddressDTO addressDTO, Integer existingAddressId, List<OrderItemsDTO> orderItems) {
        try {
            System.out.println("[ORDER] Starting order creation for user ID: " + userId);

            // Validate user exists
            User user = userRepository.get(userId);
            if (user == null) {
                throw new RuntimeException("Usuário não encontrado com ID: " + userId);
            }
            System.out.println("[ORDER] User found: " + user.getName());

            // Handle address - either create new or use existing
            Address address;
            if (existingAddressId != null) {
                System.out.println("[ORDER] Using existing address ID: " + existingAddressId);
                address = addressRepository.get(existingAddressId);
                if (address == null) {
                    throw new RuntimeException("Endereço não encontrado com ID: " + existingAddressId);
                }
                // Verify address belongs to user
                if (address.getUserId() != userId) {
                    throw new RuntimeException("Endereço não pertence ao usuário");
                }
                System.out.println("[ORDER] Address found and validated");
            } else if (addressDTO != null) {
                // Create new address
                System.out.println("[ORDER] Creating new address");
                address = new Address(0, userId, addressDTO.getStreet(), addressDTO.getNumber(),
                                    addressDTO.getNeighborhood(), addressDTO.getZipCode(),
                                    addressDTO.getComplement(), addressDTO.getCity(), addressDTO.getState());
                address = addressRepository.create(address);
                System.out.println("[ORDER] New address created with ID: " + address.getId());
            } else {
                throw new RuntimeException("É necessário fornecer um endereço existente ou dados para criar um novo");
            }

            // Validate order items
            if (orderItems == null || orderItems.isEmpty()) {
                throw new RuntimeException("O pedido deve conter pelo menos um item");
            }
            System.out.println("[ORDER] Validating " + orderItems.size() + " order items");

            // Validate products and stock
            for (OrderItemsDTO item : orderItems) {
                Product product = productRepository.get(item.getProductId());
                if (product == null) {
                    throw new RuntimeException("Produto não encontrado com ID: " + item.getProductId());
                }
                if (product.isDeleted()) {
                    throw new RuntimeException("Produto com ID " + item.getProductId() + " não está disponível");
                }
                if (product.getAvailableAmount() < item.getAmount()) {
                    throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName() +
                                             ". Disponível: " + product.getAvailableAmount() +
                                             ", Solicitado: " + item.getAmount());
                }
                if (item.getAmount() <= 0) {
                    throw new RuntimeException("Quantidade deve ser maior que zero");
                }
                if (item.getCurrentValue() <= 0) {
                    throw new RuntimeException("Valor do produto deve ser maior que zero");
                }
                System.out.println("[ORDER] Product validated: " + product.getName() + ", amount: " + item.getAmount());
            }

            // Create the order
            System.out.println("[ORDER] Creating order entity");
            Order order = new Order(0, user, address, "PENDING", "PENDING", "PENDING");
            order = orderRepository.create(order);
            System.out.println("[ORDER] Order created with ID: " + order.getId());

            // Create order items and update product stock
            System.out.println("[ORDER] Creating order items");
            for (OrderItemsDTO itemDTO : orderItems) {
                Product product = productRepository.get(itemDTO.getProductId());

                // Create order item
                OrderItems orderItem = new OrderItems(order, product, itemDTO.getAmount(), itemDTO.getCurrentValue());
                orderItem = orderItemsRepository.create(orderItem);
                System.out.println("[ORDER] Order item created with ID: " + orderItem.getId());

                // Update product stock
                product.setAvailableAmount(product.getAvailableAmount() - itemDTO.getAmount());
                productRepository.update(product);
                System.out.println("[ORDER] Product stock updated: " + product.getName() + ", new stock: " + product.getAvailableAmount());
            }

            System.out.println("[ORDER] Order creation completed successfully");
            return order;
        } catch (Exception e) {
            System.err.println("[ORDER] Error during order creation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar pedido: " + e.getMessage());
        }
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> allOrders = orderRepository.list();
        List<Order> userOrders = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getUser().getId() == userId) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }

    public Order getOrderById(int orderId) {
        return orderRepository.get(orderId);
    }

    public List<OrderItems> getOrderItems(int orderId) {
        List<OrderItems> allOrderItems = orderItemsRepository.list();
        List<OrderItems> orderItems = new ArrayList<>();
        for (OrderItems item : allOrderItems) {
            if (item.getOrder().getId() == orderId) {
                orderItems.add(item);
            }
        }
        return orderItems;
    }

    public Order updateOrderStatus(int orderId, String orderStatus, String paymentStatus, String shippingStatus) {
        Order order = orderRepository.get(orderId);
        if (order == null) {
            throw new RuntimeException("Pedido não encontrado com ID: " + orderId);
        }

        if (orderStatus != null) {
            order.setOrderStatus(orderStatus);
        }
        if (paymentStatus != null) {
            order.setPaymentStatus(paymentStatus);
        }
        if (shippingStatus != null) {
            order.setShippingStatus(shippingStatus);
        }

        order.setUpdatedAt(new Date());
        return orderRepository.update(order);
    }

    public void cancelOrder(int orderId) {
        Order order = orderRepository.get(orderId);
        if (order == null) {
            throw new RuntimeException("Pedido não encontrado com ID: " + orderId);
        }

        // Only allow cancellation if order is still pending
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new RuntimeException("Não é possível cancelar um pedido com status: " + order.getOrderStatus());
        }

        // Return products to stock
        List<OrderItems> orderItems = getOrderItems(orderId);
        for (OrderItems item : orderItems) {
            Product product = productRepository.get(item.getProduct().getId());
            product.setAvailableAmount(product.getAvailableAmount() + item.getAmount());
            productRepository.update(product);
        }

        // Update order status
        order.setOrderStatus("CANCELLED");
        order.setPaymentStatus("CANCELLED");
        order.setShippingStatus("CANCELLED");
        order.setUpdatedAt(new Date());
        orderRepository.update(order);
    }

    public double calculateOrderTotal(int orderId) {
        try {
            System.out.println("[ORDER] Calculating total for order ID: " + orderId);
            List<OrderItems> orderItems = getOrderItems(orderId);
            System.out.println("[ORDER] Found " + orderItems.size() + " items for order " + orderId);

            double total = 0.0;
            for (OrderItems item : orderItems) {
                double itemTotal = item.getTotalValue();
                System.out.println("[ORDER] Item total: " + itemTotal);
                total += itemTotal;
            }
            System.out.println("[ORDER] Final total: " + total);
            return total;
        } catch (Exception e) {
            System.err.println("[ORDER] Error calculating order total: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao calcular total do pedido: " + e.getMessage());
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.list();
    }
}
