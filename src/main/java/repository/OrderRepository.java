package repository;

import entity.Order;
import entity.User;
import entity.Address;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderRepository {
    private static final String FILE_NAME = "orders.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderRepository() {
        this.userRepository = new UserRepository();
        this.addressRepository = new AddressRepository();
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                try (FileWriter fw = new FileWriter(FILE_NAME);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    out.println("id,user_id,address_id,payment_status,shipping_status,order_status,created_at,updated_at");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo CSV: " + e.getMessage());
        }
    }

    public void save(Order order) throws IOException {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.printf("%d,%d,%d,%s,%s,%s,%s,%s\n",
                order.getId(),
                order.getUser().getId(),
                order.getAddress().getId(),
                order.getPaymentStatus(),
                order.getShippingStatus(),
                order.getOrderStatus(),
                DATE_FORMAT.format(order.getCreatedAt()),
                DATE_FORMAT.format(order.getUpdatedAt()));
        }
    }

    public Order create(Order order) throws IOException {
        List<Order> orders = findAll();
        int newId = orders.isEmpty() ? 1 : orders.stream().mapToInt(Order::getId).max().orElse(0) + 1;
        order.setId(newId);
        save(order);
        return order;
    }

    public void update(Order order) throws IOException {
        List<Order> orders = findAll();
        try (FileWriter fw = new FileWriter(FILE_NAME);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // Write header
            out.println("id,user_id,address_id,payment_status,shipping_status,order_status,created_at,updated_at");

            // Write all orders, replacing the updated one
            for (Order o : orders) {
                if (o.getId() == order.getId()) {
                    order.setUpdatedAt(new Date());
                    out.printf("%d,%d,%d,%s,%s,%s,%s,%s\n",
                        order.getId(),
                        order.getUser().getId(),
                        order.getAddress().getId(),
                        order.getPaymentStatus(),
                        order.getShippingStatus(),
                        order.getOrderStatus(),
                        DATE_FORMAT.format(order.getCreatedAt()),
                        DATE_FORMAT.format(order.getUpdatedAt()));
                } else {
                    out.printf("%d,%d,%d,%s,%s,%s,%s,%s\n",
                        o.getId(),
                        o.getUser().getId(),
                        o.getAddress().getId(),
                        o.getPaymentStatus(),
                        o.getShippingStatus(),
                        o.getOrderStatus(),
                        DATE_FORMAT.format(o.getCreatedAt()),
                        DATE_FORMAT.format(o.getUpdatedAt()));
                }
            }
        }
    }

    public List<Order> findAll() throws IOException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length >= 8) {
                    try {
                        Order order = parseOrderFromRow(parts);
                        orders.add(order);
                    } catch (Exception e) {
                        System.err.println("Erro ao parsear linha do pedido: " + line);
                    }
                }
            }
        }
        return orders;
    }

    public Order findById(int id) throws IOException {
        for (Order order : findAll()) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    public List<Order> findByUserId(int userId) throws IOException {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : findAll()) {
            if (order.getUser().getId() == userId) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }

    public Order updateOrder(Order order) throws IOException {
        update(order);
        return order;
    }

    public Order getById(int id) throws IOException {
        return findById(id);
    }

    public List<Order> getByUserId(int userId) throws IOException {
        return findByUserId(userId);
    }

    public List<Order> find() throws IOException {
        return findAll();
    }

    public int getNextId() throws IOException {
        List<Order> orders = findAll();
        return orders.isEmpty() ? 1 : orders.stream().mapToInt(Order::getId).max().orElse(0) + 1;
    }

    private Order parseOrderFromRow(String[] parts) throws Exception {
        int id = Integer.parseInt(parts[0]);
        int userId = Integer.parseInt(parts[1]);
        int addressId = Integer.parseInt(parts[2]);
        String paymentStatus = parts[3];
        String shippingStatus = parts[4];
        String orderStatus = parts[5];
        Date createdAt = DATE_FORMAT.parse(parts[6]);
        Date updatedAt = DATE_FORMAT.parse(parts[7]);

        // Get related entities
        User user = userRepository.findById(userId);
        Address address = addressRepository.findById(addressId);

        if (user == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + userId);
        }
        if (address == null) {
            throw new RuntimeException("Endereço não encontrado com ID: " + addressId);
        }

        return new Order(id, user, address, paymentStatus, shippingStatus, orderStatus, createdAt, updatedAt);
    }
}
