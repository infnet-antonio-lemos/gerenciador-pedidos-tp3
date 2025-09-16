package cli.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

public class HttpClientService {
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public HttpClientService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
    }

    // Authentication methods
    public String login(String email, String password) throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
            "email", email,
            "password", password
        ));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            return jsonResponse.get("token").asText();
        } else {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void register(String name, String email, String password, String confirmPassword, String document) throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
            "name", name,
            "email", email,
            "password", password,
            "confirmPassword", confirmPassword,
            "document", document
        ));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/auth/register"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void logout(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/auth/logout"))
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // Product methods
    public String getProducts(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/products"))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public String getProduct(String token, int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/products/" + id))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void createProduct(String token, Map<String, Object> productData) throws Exception {
        String json = objectMapper.writeValueAsString(productData);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/products"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void updateProduct(String token, int id, Map<String, Object> productData) throws Exception {
        String json = objectMapper.writeValueAsString(productData);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/products/" + id))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .PUT(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void deleteProduct(String token, int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/products/" + id))
            .header("Authorization", "Bearer " + token)
            .DELETE()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    // Address methods
    public String getAddresses(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/addresses"))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void createAddress(String token, Map<String, String> addressData) throws Exception {
        String json = objectMapper.writeValueAsString(addressData);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/addresses"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void updateAddress(String token, int id, Map<String, String> addressData) throws Exception {
        String json = objectMapper.writeValueAsString(addressData);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/addresses/" + id))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .PUT(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void deleteAddress(String token, int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/addresses/" + id))
            .header("Authorization", "Bearer " + token)
            .DELETE()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    // Order methods
    public String getOrders(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/orders"))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public String getOrder(String token, int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/orders/" + id))
            .header("Authorization", "Bearer " + token)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("error").asText());
        }
    }

    public void createOrder(String token, Map<String, Object> orderData) throws Exception {
        String json = objectMapper.writeValueAsString(orderData);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/orders"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("message").asText());
        }
    }

    public void cancelOrder(String token, int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/orders/" + id))
            .header("Authorization", "Bearer " + token)
            .DELETE()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            JsonNode errorResponse = objectMapper.readTree(response.body());
            throw new Exception(errorResponse.get("message").asText());
        }
    }
}
