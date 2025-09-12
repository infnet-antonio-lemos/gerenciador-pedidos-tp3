package business;

import entity.Product;
import repository.ProductRepository;

import java.util.List;

public class ProductBusiness {
    private ProductRepository productRepository;

    public ProductBusiness(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(String name, double value, String description, int availableAmount, String image) throws Exception {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }
        if (value < 0) {
            throw new Exception("Valor do produto deve ser positivo");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new Exception("Descrição do produto é obrigatória");
        }
        if (availableAmount < 0) {
            throw new Exception("Quantidade disponível deve ser positiva");
        }

        int id = productRepository.getNextId();
        Product product = new Product(id, name.trim(), value, description.trim(), availableAmount, image != null ? image.trim() : "");
        productRepository.save(product);
        return product;
    }

    public Product updateProduct(int id, String name, double value, String description, int availableAmount, String image) throws Exception {
        Product existingProduct = productRepository.findById(id);
        if (existingProduct == null) {
            throw new Exception("Produto não encontrado");
        }

        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Nome do produto é obrigatório");
        }
        if (value < 0) {
            throw new Exception("Valor do produto deve ser positivo");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new Exception("Descrição do produto é obrigatória");
        }
        if (availableAmount < 0) {
            throw new Exception("Quantidade disponível deve ser positiva");
        }

        // Update fields
        existingProduct.setName(name.trim());
        existingProduct.setValue(value);
        existingProduct.setDescription(description.trim());
        existingProduct.setAvailableAmount(availableAmount);
        existingProduct.setImage(image != null ? image.trim() : "");

        productRepository.update(existingProduct);
        return existingProduct;
    }

    public List<Product> getAllProducts() throws Exception {
        return productRepository.findAll();
    }

    public Product getProductById(int id) throws Exception {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new Exception("Produto não encontrado");
        }
        return product;
    }

    public void deleteProduct(int id) throws Exception {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new Exception("Produto não encontrado");
        }
        productRepository.delete(id);
    }
}
