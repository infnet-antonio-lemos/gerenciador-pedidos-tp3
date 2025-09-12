package repository;

import entity.OrderItems;
import entity.Order;
import entity.Product;
import java.io.*;
import java.util.*;

public class OrderItemsRepository {
    private static final String FILE_NAME = "order_items.csv";
    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    public OrderItemsRepository() {
        this.orderRepository = new OrderRepository();
        this.productRepository = new ProductRepository();
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                try (FileWriter fw = new FileWriter(FILE_NAME);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    out.println("id,order_id,product_id,amount,value");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo CSV: " + e.getMessage());
        }
    }

    public void save(OrderItems orderItem) throws IOException {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.printf("%d,%d,%d,%d,%f\n",
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getProduct().getId(),
                orderItem.getAmount(),
                orderItem.getValue());
        }
    }

    public OrderItems create(OrderItems orderItem) throws IOException {
        List<OrderItems> orderItems = findAll();
        int newId = orderItems.isEmpty() ? 1 : orderItems.stream().mapToInt(OrderItems::getId).max().orElse(0) + 1;
        orderItem.setId(newId);
        save(orderItem);
        return orderItem;
    }

    public void update(OrderItems orderItem) throws IOException {
        List<OrderItems> orderItems = findAll();
        try (FileWriter fw = new FileWriter(FILE_NAME);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // Write header
            out.println("id,order_id,product_id,amount,value");

            // Write all order items, replacing the updated one
            for (OrderItems oi : orderItems) {
                if (oi.getId() == orderItem.getId()) {
                    out.printf("%d,%d,%d,%d,%f\n",
                        orderItem.getId(),
                        orderItem.getOrder().getId(),
                        orderItem.getProduct().getId(),
                        orderItem.getAmount(),
                        orderItem.getValue());
                } else {
                    out.printf("%d,%d,%d,%d,%f\n",
                        oi.getId(),
                        oi.getOrder().getId(),
                        oi.getProduct().getId(),
                        oi.getAmount(),
                        oi.getValue());
                }
            }
        }
    }

    public List<OrderItems> findAll() throws IOException {
        List<OrderItems> orderItems = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length >= 5) {
                    try {
                        OrderItems orderItem = parseOrderItemFromRow(parts);
                        orderItems.add(orderItem);
                    } catch (Exception e) {
                        System.err.println("Erro ao parsear linha do item do pedido: " + line);
                    }
                }
            }
        }
        return orderItems;
    }

    public OrderItems findById(int id) throws IOException {
        for (OrderItems orderItem : findAll()) {
            if (orderItem.getId() == id) {
                return orderItem;
            }
        }
        return null;
    }

    public List<OrderItems> findByOrderId(int orderId) throws IOException {
        List<OrderItems> orderItems = new ArrayList<>();
        for (OrderItems orderItem : findAll()) {
            if (orderItem.getOrder().getId() == orderId) {
                orderItems.add(orderItem);
            }
        }
        return orderItems;
    }

    public List<OrderItems> findByProductId(int productId) throws IOException {
        List<OrderItems> orderItems = new ArrayList<>();
        for (OrderItems orderItem : findAll()) {
            if (orderItem.getProduct().getId() == productId) {
                orderItems.add(orderItem);
            }
        }
        return orderItems;
    }

    public void deleteByOrderId(int orderId) throws IOException {
        List<OrderItems> orderItems = findAll();
        try (FileWriter fw = new FileWriter(FILE_NAME);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // Write header
            out.println("id,order_id,product_id,amount,value");

            // Write all order items except those belonging to the specified order
            for (OrderItems oi : orderItems) {
                if (oi.getOrder().getId() != orderId) {
                    out.printf("%d,%d,%d,%d,%f\n",
                        oi.getId(),
                        oi.getOrder().getId(),
                        oi.getProduct().getId(),
                        oi.getAmount(),
                        oi.getValue());
                }
            }
        }
    }

    // Compatibility methods for existing code
    public OrderItems getById(int id) throws IOException {
        return findById(id);
    }

    public List<OrderItems> getByOrderId(int orderId) throws IOException {
        return findByOrderId(orderId);
    }

    public List<OrderItems> getByProductId(int productId) throws IOException {
        return findByProductId(productId);
    }

    public List<OrderItems> find() throws IOException {
        return findAll();
    }

    public int getNextId() throws IOException {
        List<OrderItems> orderItems = findAll();
        return orderItems.isEmpty() ? 1 : orderItems.stream().mapToInt(OrderItems::getId).max().orElse(0) + 1;
    }

    private OrderItems parseOrderItemFromRow(String[] parts) throws Exception {
        int id = Integer.parseInt(parts[0]);
        int orderId = Integer.parseInt(parts[1]);
        int productId = Integer.parseInt(parts[2]);
        int amount = Integer.parseInt(parts[3]);
        double value = Double.parseDouble(parts[4]);

        // Get related entities
        Order order = orderRepository.findById(orderId);
        Product product = productRepository.findById(productId);

        if (order == null) {
            throw new RuntimeException("Pedido não encontrado com ID: " + orderId);
        }
        if (product == null) {
            throw new RuntimeException("Produto não encontrado com ID: " + productId);
        }

        return new OrderItems(id, order, product, amount, value);
    }
}
