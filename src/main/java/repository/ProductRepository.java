package repository;

import entity.Product;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProductRepository {
    private static final String FILE_NAME = "products.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ProductRepository() {
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                try (FileWriter fw = new FileWriter(FILE_NAME);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    out.println("id,name,value,description,available_amount,image,deleted_at");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo CSV: " + e.getMessage());
        }
    }

    public void save(Product product) throws IOException {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String deletedAtStr = product.getDeletedAt() != null ? DATE_FORMAT.format(product.getDeletedAt()) : "";
            out.printf("%d,%s,%f,%s,%d,%s,%s\n",
                product.getId(),
                product.getName(),
                product.getValue(),
                product.getDescription(),
                product.getAvailableAmount(),
                product.getImage(),
                deletedAtStr);
        }
    }

    public void update(Product product) throws IOException {
        List<Product> products = findAll();
        try (FileWriter fw = new FileWriter(FILE_NAME);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // Write header
            out.println("id,name,value,description,available_amount,image,deleted_at");

            // Write all products, replacing the updated one
            for (Product p : products) {
                if (p.getId() == product.getId()) {
                    String deletedAtStr = product.getDeletedAt() != null ? DATE_FORMAT.format(product.getDeletedAt()) : "";
                    out.printf("%d,%s,%f,%s,%d,%s,%s\n",
                        product.getId(),
                        product.getName(),
                        product.getValue(),
                        product.getDescription(),
                        product.getAvailableAmount(),
                        product.getImage(),
                        deletedAtStr);
                } else {
                    String deletedAtStr = p.getDeletedAt() != null ? DATE_FORMAT.format(p.getDeletedAt()) : "";
                    out.printf("%d,%s,%f,%s,%d,%s,%s\n",
                        p.getId(),
                        p.getName(),
                        p.getValue(),
                        p.getDescription(),
                        p.getAvailableAmount(),
                        p.getImage(),
                        deletedAtStr);
                }
            }
        }
    }

    public List<Product> findAll() throws IOException {
        return findAll(false); // By default, don't include deleted products
    }

    public List<Product> findAll(boolean includeDeleted) throws IOException {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length >= 6) {
                    try {
                        Date deletedAt = null;
                        if (parts.length > 6 && !parts[6].isEmpty()) {
                            deletedAt = DATE_FORMAT.parse(parts[6]);
                        }

                        Product product = new Product(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            Double.parseDouble(parts[2]),
                            parts[3],
                            Integer.parseInt(parts[4]),
                            parts[5],
                            deletedAt
                        );

                        // Only add non-deleted products unless includeDeleted is true
                        if (includeDeleted || !product.isDeleted()) {
                            products.add(product);
                        }
                    } catch (NumberFormatException | ParseException e) {
                        System.err.println("Erro ao parsear linha: " + line);
                    }
                }
            }
        }
        return products;
    }

    public Product findById(int id) throws IOException {
        return findById(id, false); // By default, don't include deleted products
    }

    public Product findById(int id, boolean includeDeleted) throws IOException {
        for (Product product : findAll(includeDeleted)) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public void delete(int id) throws IOException {
        Product product = findById(id, true); // Include deleted to find the product
        if (product != null) {
            product.setDeletedAt(new Date());
            update(product);
        }
    }

    public int getNextId() throws IOException {
        List<Product> products = findAll(true); // Include deleted to get accurate next ID
        return products.isEmpty() ? 1 : products.stream().mapToInt(Product::getId).max().orElse(0) + 1;
    }
}
