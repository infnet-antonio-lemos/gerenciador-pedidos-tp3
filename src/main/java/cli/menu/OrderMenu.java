package cli.menu;

import cli.service.HttpClientService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class OrderMenu {
    private final HttpClientService httpService;
    private final Scanner scanner;
    private final String token;
    private final ObjectMapper objectMapper;

    public OrderMenu(HttpClientService httpService, Scanner scanner, String token) {
        this.httpService = httpService;
        this.scanner = scanner;
        this.token = token;
        this.objectMapper = new ObjectMapper();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== GERENCIAR PEDIDOS ===");
            System.out.println("1. Listar meus pedidos");
            System.out.println("2. Ver detalhes de um pedido");
            System.out.println("3. Criar novo pedido");
            System.out.println("4. Cancelar pedido");
            System.out.println("5. Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listOrders();
                    break;
                case "2":
                    viewOrderDetails();
                    break;
                case "3":
                    createOrder();
                    break;
                case "4":
                    cancelOrder();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void listOrders() {
        try {
            String response = httpService.getOrders(token);
            JsonNode orders = objectMapper.readTree(response);

            if (orders.isArray() && orders.size() > 0) {
                System.out.println("\n=== MEUS PEDIDOS ===");
                System.out.printf("%-5s %-15s %-10s %-12s %-15s%n", "ID", "Status", "Itens", "Total", "Data");
                System.out.println("-".repeat(70));

                for (JsonNode orderSummary : orders) {
                    JsonNode order = orderSummary.get("order");
                    int id = order.get("id").asInt();
                    String status = order.get("orderStatus").asText();
                    int itemCount = orderSummary.get("itemCount").asInt();
                    double total = orderSummary.get("totalValue").asDouble();
                    String createdAt = order.get("createdAt").asText();

                    System.out.printf("%-5d %-15s %-10d R$%-10.2f %-15s%n",
                        id, status, itemCount, total, formatDate(createdAt));
                }
            } else {
                System.out.println("Você não possui pedidos.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }
    }

    private void viewOrderDetails() {
        System.out.print("Digite o ID do pedido: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            String response = httpService.getOrder(token, id);
            JsonNode orderResponse = objectMapper.readTree(response);

            JsonNode order = orderResponse.get("order");
            JsonNode items = orderResponse.get("items");
            double totalValue = orderResponse.get("totalValue").asDouble();

            System.out.println("\n=== DETALHES DO PEDIDO ===");
            System.out.println("ID: " + order.get("id").asInt());
            System.out.println("Status: " + order.get("orderStatus").asText());
            System.out.println("Data de criação: " + formatDate(order.get("createdAt").asText()));
            System.out.println("Última atualização: " + formatDate(order.get("updatedAt").asText()));

            // Address information
            JsonNode address = order.get("address");
            System.out.println("\nEndereço de entrega:");
            System.out.println(address.get("street").asText() + ", " + address.get("number").asText());
            System.out.println(address.get("neighborhood").asText() + " - " + address.get("zipCode").asText());
            System.out.println(address.get("city").asText() + " - " + address.get("state").asText());

            // Items
            System.out.println("\nItens do pedido:");
            System.out.printf("%-30s %-10s %-12s %-12s%n", "Produto", "Qtd", "Preço Unit.", "Subtotal");
            System.out.println("-".repeat(70));

            for (JsonNode item : items) {
                JsonNode product = item.get("product");
                String productName = product.get("name").asText();
                int amount = item.get("amount").asInt();
                double unitPrice = item.get("unitValue").asDouble();
                double subtotal = item.get("totalValue").asDouble();

                System.out.printf("%-30s %-10d R$%-10.2f R$%-10.2f%n",
                    truncate(productName, 30), amount, unitPrice, subtotal);
            }

            System.out.println("-".repeat(70));
            System.out.printf("TOTAL: R$ %.2f%n", totalValue);

        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar pedido: " + e.getMessage());
        }
    }

    private void createOrder() {
        try {
            // First, get user addresses
            String addressResponse = httpService.getAddresses(token);
            JsonNode addresses = objectMapper.readTree(addressResponse);

            if (!addresses.isArray() || addresses.size() == 0) {
                System.out.println("Você precisa ter pelo menos um endereço cadastrado para fazer um pedido.");
                return;
            }

            // Show addresses and let user select
            System.out.println("\n=== SELECIONAR ENDEREÇO ===");
            System.out.println("0. Criar novo endereço");
            for (int i = 0; i < addresses.size(); i++) {
                JsonNode addr = addresses.get(i);
                System.out.printf("%d. %s, %s - %s - %s%n",
                    i + 1,
                    addr.get("street").asText(),
                    addr.get("number").asText(),
                    addr.get("neighborhood").asText(),
                    addr.get("city").asText());
            }

            System.out.print("Escolha um endereço: ");
            int addressChoice = Integer.parseInt(scanner.nextLine().trim());

            Map<String, Object> orderData = new HashMap<>();

            if (addressChoice == 0) {
                System.out.println("Funcionalidade de criar novo endereço não implementada no pedido.");
                return;
            } else if (addressChoice > 0 && addressChoice <= addresses.size()) {
                JsonNode selectedAddress = addresses.get(addressChoice - 1);
                orderData.put("addressId", selectedAddress.get("id").asInt());
            } else {
                System.out.println("Opção inválida.");
                return;
            }

            // Get products and let user select items
            String productResponse = httpService.getProducts(token);
            JsonNode products = objectMapper.readTree(productResponse);

            if (!products.isArray() || products.size() == 0) {
                System.out.println("Não há produtos disponíveis.");
                return;
            }

            // Show available products
            System.out.println("\n=== PRODUTOS DISPONÍVEIS ===");
            System.out.printf("%-5s %-20s %-10s %-10s%n", "ID", "Nome", "Preço", "Estoque");
            System.out.println("-".repeat(50));

            for (JsonNode product : products) {
                if (!product.get("deleted").asBoolean() && product.get("availableAmount").asInt() > 0) {
                    System.out.printf("%-5d %-20s R$%-8.2f %-10d%n",
                        product.get("id").asInt(),
                        truncate(product.get("name").asText(), 20),
                        product.get("value").asDouble(),
                        product.get("availableAmount").asInt());
                }
            }

            List<Map<String, Integer>> items = new ArrayList<>();

            System.out.println("\nAdicione itens ao pedido (digite 0 para finalizar):");
            while (true) {
                System.out.print("ID do produto: ");
                int productId = Integer.parseInt(scanner.nextLine().trim());

                if (productId == 0) break;

                System.out.print("Quantidade: ");
                int amount = Integer.parseInt(scanner.nextLine().trim());

                if (amount <= 0) {
                    System.out.println("Quantidade deve ser maior que zero.");
                    continue;
                }

                Map<String, Integer> item = new HashMap<>();
                item.put("productId", productId);
                item.put("amount", amount);
                items.add(item);

                System.out.println("Item adicionado! Continue adicionando ou digite 0 para finalizar.");
            }

            if (items.isEmpty()) {
                System.out.println("Pedido deve ter pelo menos um item.");
                return;
            }

            orderData.put("items", items);

            httpService.createOrder(token, orderData);
            System.out.println("Pedido criado com sucesso!");

        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro ao criar pedido: " + e.getMessage());
        }
    }

    private void cancelOrder() {
        System.out.print("Digite o ID do pedido para cancelar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Tem certeza que deseja cancelar este pedido? (s/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("s") || confirmation.equals("sim")) {
                httpService.cancelOrder(token, id);
                System.out.println("Pedido cancelado com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro ao cancelar pedido: " + e.getMessage());
        }
    }

    private String formatDate(String isoDate) {
        // Simple format for display - could be improved with proper date parsing
        return isoDate.length() > 10 ? isoDate.substring(0, 10) : isoDate;
    }

    private String truncate(String str, int length) {
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}
