package business;

import entity.*;
import dto.*;
import repository.*;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.io.IOException;

public class OrderBusiness {
    private OrderRepository orderRepository;
    private OrderItemsRepository orderItemsRepository;
    private UserRepository userRepository;
    private AddressRepository addressRepository;
    private ProductRepository productRepository;

    public OrderBusiness(OrderRepository orderRepository, OrderItemsRepository orderItemsRepository,
                        UserRepository userRepository, AddressRepository addressRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(int userId, AddressDTO addressDTO, Integer existingAddressId, List<OrderItemsDTO> orderItems) {
        try {
            // Validate user exists
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new RuntimeException("Usuário não encontrado com ID: " + userId);
            }

            // Handle address - either create new or use existing
            Address address;
            if (existingAddressId != null) {
                address = addressRepository.findById(existingAddressId);
                if (address == null) {
                    throw new RuntimeException("Endereço não encontrado com ID: " + existingAddressId);
                }
                // Verify address belongs to user
                if (address.getUserId() != userId) {
                    throw new RuntimeException("Endereço não pertence ao usuário");
                }
            } else if (addressDTO != null) {
                // Create new address - need to get next ID first
                int nextAddressId = getNextAddressId();
                address = new Address(nextAddressId, userId, addressDTO.getStreet(), addressDTO.getNumber(),
                                    addressDTO.getNeighborhood(), addressDTO.getZipCode(),
                                    addressDTO.getComplement(), addressDTO.getCity(), addressDTO.getState());
                addressRepository.save(address);
            } else {
                throw new RuntimeException("É necessário fornecer um endereço existente ou dados para criar um novo");
            }

            // Validate order items
            if (orderItems == null || orderItems.isEmpty()) {
                throw new RuntimeException("O pedido deve conter pelo menos um item");
            }

            // Validate products and stock
            for (OrderItemsDTO item : orderItems) {
                Product product = productRepository.findById(item.getProductId());
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
            }

            // Create the order
            Order order = new Order(0, user, address, "PENDING", "PENDING", "PENDING");
            order = orderRepository.create(order);

            // Create order items and update product stock
            List<OrderItems> createdItems = new ArrayList<>();
            for (OrderItemsDTO itemDTO : orderItems) {
                Product product = productRepository.findById(itemDTO.getProductId());

                // Create order item
                OrderItems orderItem = new OrderItems(order, product, itemDTO.getAmount(), itemDTO.getCurrentValue());
                orderItem = orderItemsRepository.create(orderItem);
                createdItems.add(orderItem);

                // Update product stock
                product.setAvailableAmount(product.getAvailableAmount() - itemDTO.getAmount());
                productRepository.update(product);
            }

            return order;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar pedido: " + e.getMessage(), e);
        }
    }

    public List<Order> getOrdersByUserId(int userId) {
        try {
            return orderRepository.getByUserId(userId);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar pedidos do usuário: " + e.getMessage(), e);
        }
    }

    public Order getOrderById(int orderId) {
        try {
            return orderRepository.getById(orderId);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage(), e);
        }
    }

    public List<OrderItems> getOrderItems(int orderId) {
        try {
            return orderItemsRepository.getByOrderId(orderId);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar itens do pedido: " + e.getMessage(), e);
        }
    }

    public Order updateOrderStatus(int orderId, String orderStatus, String paymentStatus, String shippingStatus) {
        try {
            Order order = orderRepository.getById(orderId);
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

            return orderRepository.updateOrder(order);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar status do pedido: " + e.getMessage(), e);
        }
    }

    public void cancelOrder(int orderId) {
        try {
            Order order = orderRepository.getById(orderId);
            if (order == null) {
                throw new RuntimeException("Pedido não encontrado com ID: " + orderId);
            }

            // Only allow cancellation if order is still pending
            if (!"PENDING".equals(order.getOrderStatus())) {
                throw new RuntimeException("Não é possível cancelar um pedido com status: " + order.getOrderStatus());
            }

            // Return products to stock
            List<OrderItems> orderItems = orderItemsRepository.getByOrderId(orderId);
            for (OrderItems item : orderItems) {
                Product product = productRepository.findById(item.getProduct().getId());
                product.setAvailableAmount(product.getAvailableAmount() + item.getAmount());
                productRepository.update(product);
            }

            // Update order status
            order.setOrderStatus("CANCELLED");
            order.setPaymentStatus("CANCELLED");
            order.setShippingStatus("CANCELLED");
            orderRepository.updateOrder(order);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao cancelar pedido: " + e.getMessage(), e);
        }
    }

    public double calculateOrderTotal(int orderId) {
        try {
            List<OrderItems> orderItems = orderItemsRepository.getByOrderId(orderId);
            return orderItems.stream()
                            .mapToDouble(OrderItems::getTotalValue)
                            .sum();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao calcular total do pedido: " + e.getMessage(), e);
        }
    }

    public List<Order> getAllOrders() {
        try {
            return orderRepository.find();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar todos os pedidos: " + e.getMessage(), e);
        }
    }

    private int getNextAddressId() {
        try {
            List<Address> addresses = addressRepository.findAll();
            return addresses.isEmpty() ? 1 : addresses.stream().mapToInt(Address::getId).max().orElse(0) + 1;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao obter próximo ID de endereço", e);
        }
    }
}
