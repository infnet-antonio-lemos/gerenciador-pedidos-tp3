package business;

import entity.Product;
import repository.RepositoryInterface;

import java.util.List;

public class ProductBusiness {
    private RepositoryInterface<Product> productRepository;

    public ProductBusiness(RepositoryInterface<Product> productRepository) {
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

        Product product = new Product(0, name.trim(), value, description.trim(), availableAmount, image != null ? image.trim() : "");
        return productRepository.create(product);
    }

    public Product updateProduct(int id, String name, double value, String description, int availableAmount, String image) throws Exception {
        Product existingProduct = productRepository.get(id);
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

        existingProduct.setName(name.trim());
        existingProduct.setValue(value);
        existingProduct.setDescription(description.trim());
        existingProduct.setAvailableAmount(availableAmount);
        existingProduct.setImage(image != null ? image.trim() : "");

        return productRepository.update(existingProduct);
    }

    public List<Product> getAllProducts() throws Exception {
        return productRepository.list();
    }

    public Product getProductById(int id) throws Exception {
        Product product = productRepository.get(id);
        if (product == null) {
            throw new Exception("Produto não encontrado");
        }
        return product;
    }

    public void deleteProduct(int id) throws Exception {
        Product product = productRepository.get(id);
        if (product == null) {
            throw new Exception("Produto não encontrado");
        }
        productRepository.delete(id);
    }
}
