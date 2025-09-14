package cli.menu;

import cli.service.HttpClientService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AddressMenu {
    private final HttpClientService httpService;
    private final Scanner scanner;
    private final String token;
    private final ObjectMapper objectMapper;

    public AddressMenu(HttpClientService httpService, Scanner scanner, String token) {
        this.httpService = httpService;
        this.scanner = scanner;
        this.token = token;
        this.objectMapper = new ObjectMapper();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== GERENCIAR ENDEREÇOS ===");
            System.out.println("1. Listar meus endereços");
            System.out.println("2. Adicionar novo endereço");
            System.out.println("3. Atualizar endereço");
            System.out.println("4. Deletar endereço");
            System.out.println("5. Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listAddresses();
                    break;
                case "2":
                    addAddress();
                    break;
                case "3":
                    updateAddress();
                    break;
                case "4":
                    deleteAddress();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void listAddresses() {
        try {
            String response = httpService.getAddresses(token);
            JsonNode addresses = objectMapper.readTree(response);

            if (addresses.isArray() && addresses.size() > 0) {
                System.out.println("\n=== MEUS ENDEREÇOS ===");
                for (JsonNode address : addresses) {
                    displayAddress(address);
                    System.out.println("-".repeat(50));
                }
            } else {
                System.out.println("Você não possui endereços cadastrados.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar endereços: " + e.getMessage());
        }
    }

    private void addAddress() {
        System.out.println("\n=== ADICIONAR NOVO ENDEREÇO ===");
        Map<String, String> addressData = collectAddressData();

        if (addressData != null) {
            try {
                httpService.createAddress(token, addressData);
                System.out.println("Endereço adicionado com sucesso!");
            } catch (Exception e) {
                System.out.println("Erro ao adicionar endereço: " + e.getMessage());
            }
        }
    }

    private void updateAddress() {
        System.out.print("Digite o ID do endereço para atualizar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("\n=== ATUALIZAR ENDEREÇO ===");
            Map<String, String> addressData = collectAddressData();

            if (addressData != null) {
                httpService.updateAddress(token, id, addressData);
                System.out.println("Endereço atualizado com sucesso!");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar endereço: " + e.getMessage());
        }
    }

    private void deleteAddress() {
        System.out.print("Digite o ID do endereço para deletar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Tem certeza que deseja deletar este endereço? (s/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("s") || confirmation.equals("sim")) {
                httpService.deleteAddress(token, id);
                System.out.println("Endereço deletado com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("Erro ao deletar endereço: " + e.getMessage());
        }
    }

    private Map<String, String> collectAddressData() {
        Map<String, String> data = new HashMap<>();

        System.out.print("Rua: ");
        String street = scanner.nextLine().trim();
        if (street.isEmpty()) {
            System.out.println("Rua é obrigatória.");
            return null;
        }
        data.put("street", street);

        System.out.print("Número: ");
        String number = scanner.nextLine().trim();
        if (number.isEmpty()) {
            System.out.println("Número é obrigatório.");
            return null;
        }
        data.put("number", number);

        System.out.print("Bairro: ");
        String neighborhood = scanner.nextLine().trim();
        if (neighborhood.isEmpty()) {
            System.out.println("Bairro é obrigatório.");
            return null;
        }
        data.put("neighborhood", neighborhood);

        System.out.print("CEP: ");
        String zipCode = scanner.nextLine().trim();
        if (zipCode.isEmpty()) {
            System.out.println("CEP é obrigatório.");
            return null;
        }
        data.put("zipCode", zipCode);

        System.out.print("Complemento (opcional): ");
        String complement = scanner.nextLine().trim();
        data.put("complement", complement);

        System.out.print("Cidade: ");
        String city = scanner.nextLine().trim();
        if (city.isEmpty()) {
            System.out.println("Cidade é obrigatória.");
            return null;
        }
        data.put("city", city);

        System.out.print("Estado: ");
        String state = scanner.nextLine().trim();
        if (state.isEmpty()) {
            System.out.println("Estado é obrigatório.");
            return null;
        }
        data.put("state", state);

        return data;
    }

    private void displayAddress(JsonNode address) {
        System.out.println("ID: " + address.get("id").asInt());
        System.out.println("Endereço: " + address.get("street").asText() + ", " +
                          address.get("number").asText());
        System.out.println("Bairro: " + address.get("neighborhood").asText());
        System.out.println("CEP: " + address.get("zipCode").asText());
        if (address.has("complement") && !address.get("complement").asText().isEmpty()) {
            System.out.println("Complemento: " + address.get("complement").asText());
        }
        System.out.println("Cidade: " + address.get("city").asText() + " - " +
                          address.get("state").asText());
    }
}
