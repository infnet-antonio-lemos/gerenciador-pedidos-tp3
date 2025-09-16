package controller;

import business.ProductBusiness;
import entity.Product;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ProductHttpController {
    private final ProductBusiness productBusiness;
    private final ObjectMapper objectMapper;

    public ProductHttpController(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
        this.objectMapper = new ObjectMapper();
    }

    // CREATE - POST /products
    public void createProduct(Context ctx) {
        try {
            JsonNode json = objectMapper.readTree(ctx.body());

            // Validate required fields exist
            if (json.get("name") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'name' é obrigatório"));
                return;
            }
            if (json.get("value") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'value' é obrigatório"));
                return;
            }
            if (json.get("description") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'description' é obrigatório"));
                return;
            }
            if (json.get("availableAmount") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'availableAmount' é obrigatório"));
                return;
            }

            String name = json.get("name").asText();
            double value = json.get("value").asDouble();
            String description = json.get("description").asText();
            int availableAmount = json.get("availableAmount").asInt();
            String image = json.has("image") ? json.get("image").asText() : "";

            Product product = productBusiness.createProduct(name, value, description, availableAmount, image);

            ctx.status(201).json(new ProductResponse(product));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    // READ - GET /products
    public void getAllProducts(Context ctx) {
        try {
            List<Product> products = productBusiness.getAllProducts();
            ctx.status(200).json(products.stream()
                .map(ProductResponse::new)
                .toArray(ProductResponse[]::new));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(e.getMessage()));
        }
    }

    // READ - GET /products/:id
    public void getProductById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Product product = productBusiness.getProductById(id);

            ctx.status(200).json(new ProductResponse(product));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID inválido"));
        } catch (Exception e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        }
    }

    // UPDATE - PUT /products/:id
    public void updateProduct(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            JsonNode json = objectMapper.readTree(ctx.body());

            // Validate required fields exist
            if (json.get("name") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'name' é obrigatório"));
                return;
            }
            if (json.get("value") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'value' é obrigatório"));
                return;
            }
            if (json.get("description") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'description' é obrigatório"));
                return;
            }
            if (json.get("availableAmount") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'availableAmount' é obrigatório"));
                return;
            }

            String name = json.get("name").asText();
            double value = json.get("value").asDouble();
            String description = json.get("description").asText();
            int availableAmount = json.get("availableAmount").asInt();
            String image = json.has("image") ? json.get("image").asText() : "";

            Product product = productBusiness.updateProduct(id, name, value, description, availableAmount, image);

            ctx.status(200).json(new ProductResponse(product));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID inválido"));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    // DELETE - DELETE /products/:id
    public void deleteProduct(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            productBusiness.deleteProduct(id);

            ctx.status(200).json(new MessageResponse("Produto deletado com sucesso"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID inválido"));
        } catch (Exception e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        }
    }

    // Response classes
    public static class ProductResponse {
        public int id;
        public String name;
        public double value;
        public String description;
        public int availableAmount;
        public String image;
        public boolean deleted;

        public ProductResponse(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.value = product.getValue();
            this.description = product.getDescription();
            this.availableAmount = product.getAvailableAmount();
            this.image = product.getImage();
            this.deleted = product.isDeleted();
        }
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }
}
