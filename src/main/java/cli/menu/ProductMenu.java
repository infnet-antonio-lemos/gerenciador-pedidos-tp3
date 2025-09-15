package cli.menu;

import cli.service.HttpClientService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProductMenu {
    private final HttpClientService httpService;
    private final Scanner scanner;
    private final String token;
    private final ObjectMapper objectMapper;

    public ProductMenu(HttpClientService httpService, Scanner scanner, String token) {
        this.httpService = httpService;
        this.scanner = scanner;
        this.token = token;
        this.objectMapper = new ObjectMapper();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== GERENCIAR PRODUTOS ===");
            System.out.println("1. Listar todos os produtos");
            System.out.println("2. Buscar produto por ID");
            System.out.println("3. Criar novo produto");
            System.out.println("4. Atualizar produto");
            System.out.println("5. Deletar produto");
            System.out.println("6. Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listProducts();
                    break;
                case "2":
                    searchProductById();
                    break;
                case "3":
                    createProduct();
                    break;
                case "4":
                    updateProduct();
                    break;
                case "5":
                    deleteProduct();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void listProducts() {
        try {
            String response = httpService.getProducts(token);
            JsonNode products = objectMapper.readTree(response);

            if (products.isArray() && products.size() > 0) {
                System.out.println("\n=== LISTA DE PRODUTOS ===");
                System.out.printf("%-5s %-20s %-10s %-15s %-10s%n", "ID", "Nome", "Valor", "Estoque", "Status");
                System.out.println("-".repeat(65));

                for (JsonNode product : products) {
                    int id = product.get("id").asInt();
                    String name = product.get("name").asText();
                    double value = product.get("value").asDouble();
                    int stock = product.get("availableAmount").asInt();
                    boolean deleted = product.get("deleted").asBoolean();
                    String status = deleted ? "Inativo" : "Ativo";

                    System.out.printf("%-5d %-20s R$%-8.2f %-15d %-10s%n",
                        id, truncate(name, 20), value, stock, status);
                }
            } else {
                System.out.println("Nenhum produto encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
    }

    private void searchProductById() {
        System.out.print("Digite o ID do produto: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            String response = httpService.getProduct(token, id);
            JsonNode product = objectMapper.readTree(response);

            System.out.println("\n=== DETALHES DO PRODUTO ===");
            System.out.println("ID: " + product.get("id").asInt());
            System.out.println("Nome: " + product.get("name").asText());
            System.out.println("Descrição: " + product.get("description").asText());
            System.out.println("Valor: R$ " + String.format("%.2f", product.get("value").asDouble()));
            System.out.println("Estoque disponível: " + product.get("availableAmount").asInt());
            System.out.println("Status: " + (product.get("deleted").asBoolean() ? "Inativo" : "Ativo"));
            if (product.has("image") && !product.get("image").asText().isEmpty()) {
                System.out.println("Imagem: " + product.get("image").asText());
            }

        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        }
    }

    private void createProduct() {
        System.out.println("\n=== CRIAR NOVO PRODUTO ===");
        Map<String, Object> productData = collectProductData();

        if (productData != null) {
            try {
                httpService.createProduct(token, productData);
                System.out.println("Produto criado com sucesso!");
            } catch (Exception e) {
                System.out.println("Erro ao criar produto: " + e.getMessage());
            }
        }
    }

    private void updateProduct() {
        System.out.print("Digite o ID do produto para atualizar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());

            // First, get current product data to show user
            String response = httpService.getProduct(token, id);
            JsonNode currentProduct = objectMapper.readTree(response);

            String currentName = currentProduct.has("name") && !currentProduct.get("name").isNull() ? currentProduct.get("name").asText() : "N/A";
            double currentValue = currentProduct.has("value") && !currentProduct.get("value").isNull() ? currentProduct.get("value").asDouble() : 0.0;
            String currentDescription = currentProduct.has("description") && !currentProduct.get("description").isNull() ? currentProduct.get("description").asText() : "N/A";
            int currentStock = currentProduct.has("availableAmount") && !currentProduct.get("availableAmount").isNull() ? currentProduct.get("availableAmount").asInt() : 0;

            System.out.println("\n=== PRODUTO ATUAL ===");
            System.out.println("Nome: " + currentName);
            System.out.println("Valor: R$ " + String.format("%.2f", currentValue));
            System.out.println("Descrição: " + currentDescription);
            System.out.println("Estoque: " + currentStock);

            System.out.println("\n=== NOVOS DADOS ===");
            Map<String, Object> productData = collectProductData();

            if (productData != null) {
                httpService.updateProduct(token, id, productData);
                System.out.println("Produto atualizado com sucesso!");
            }

        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        System.out.print("Digite o ID do produto para deletar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());

            // Show product details before deletion
            String response = httpService.getProduct(token, id);
            JsonNode product = objectMapper.readTree(response);

            System.out.println("\n=== PRODUTO A SER DELETADO ===");
            String name = product.has("name") && !product.get("name").isNull() ? product.get("name").asText() : "N/A";
            double value = product.has("value") && !product.get("value").isNull() ? product.get("value").asDouble() : 0.0;
            int stock = product.has("availableAmount") && !product.get("availableAmount").isNull() ? product.get("availableAmount").asInt() : 0;

            System.out.println("Nome: " + name);
            System.out.println("\n=== PRODUTO A SER DELETADO ===");
            System.out.println("Valor: R$ " + String.format("%.2f", value));
            System.out.println("Estoque: " + stock);

            System.out.print("\nTem certeza que deseja deletar este produto? (s/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("s") || confirmation.equals("sim")) {
                httpService.deleteProduct(token, id);
                System.out.println("Produto deletado com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
            System.out.println("ID inválido. Digite apenas números.");
        } catch (NumberFormatException e) {
        } catch (Exception e) {
            System.out.println("Erro ao deletar produto: " + e.getMessage());
        }
    }

    private Map<String, Object> collectProductData() {
        Map<String, Object> data = new HashMap<>();

        System.out.print("Nome do produto: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Nome é obrigatório.");
            return null;
        }
        data.put("name", name);

        System.out.print("Valor (R$): ");
        try {
            double value = Double.parseDouble(scanner.nextLine().trim());
            if (value < 0) {
                System.out.println("Valor deve ser positivo.");
                return null;
            }
            data.put("value", value);
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Digite um número válido.");
            return null;
        }

        System.out.print("Descrição: ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            System.out.println("Descrição é obrigatória.");
            return null;
        }
        data.put("description", description);

        System.out.print("Quantidade disponível: ");
        try {
            int availableAmount = Integer.parseInt(scanner.nextLine().trim());
            if (availableAmount < 0) {
                System.out.println("Quantidade deve ser positiva.");
                return null;
            }
            data.put("availableAmount", availableAmount);
        } catch (NumberFormatException e) {
            System.out.println("Quantidade inválida. Digite um número válido.");
            return null;
        }

        System.out.print("URL da imagem (opcional): ");
        String image = scanner.nextLine().trim();
        data.put("image", image);

        return data;
    }

    private String truncate(String str, int length) {
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}
