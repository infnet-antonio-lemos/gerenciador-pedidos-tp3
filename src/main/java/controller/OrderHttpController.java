package controller;

import business.OrderBusiness;
import entity.Order;
import entity.OrderItems;
import dto.OrderItemsDTO;
import dto.AddressDTO;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.ArrayList;

public class OrderHttpController {
    private OrderBusiness orderBusiness;
    private ObjectMapper objectMapper;

    public OrderHttpController(OrderBusiness orderBusiness) {
        this.orderBusiness = orderBusiness;
        this.objectMapper = new ObjectMapper();
    }

    // CREATE - POST /orders
    public void createOrder(Context ctx) {
        try {
            JsonNode json = objectMapper.readTree(ctx.body());
            Integer userId = ctx.attribute("userId"); // From authentication middleware

            // Parse address (either new address data or existing address ID)
            AddressDTO addressDTO = null;
            Integer existingAddressId = null;

            if (json.has("addressId") && !json.get("addressId").isNull()) {
                existingAddressId = json.get("addressId").asInt();
            } else if (json.has("address")) {
                JsonNode addressNode = json.get("address");
                if (addressNode.get("street") == null || addressNode.get("number") == null ||
                    addressNode.get("neighborhood") == null || addressNode.get("zipCode") == null ||
                    addressNode.get("city") == null || addressNode.get("state") == null) {
                    ctx.status(400).json(new ErrorResponse("Campos obrigatórios do endereço: street, number, neighborhood, zipCode, city, state"));
                    return;
                }
                addressDTO = new AddressDTO(
                    addressNode.get("street").asText(),
                    addressNode.get("number").asText(),
                    addressNode.get("neighborhood").asText(),
                    addressNode.get("zipCode").asText(),
                    addressNode.has("complement") ? addressNode.get("complement").asText() : "",
                    addressNode.get("city").asText(),
                    addressNode.get("state").asText()
                );
            } else {
                ctx.status(400).json(new ErrorResponse("É necessário fornecer 'addressId' ou dados completos do 'address'"));
                return;
            }

            // Parse order items
            if (!json.has("items") || !json.get("items").isArray()) {
                ctx.status(400).json(new ErrorResponse("Campo 'items' é obrigatório e deve ser um array"));
                return;
            }

            List<OrderItemsDTO> orderItems = new ArrayList<>();
            for (JsonNode itemNode : json.get("items")) {
                if (itemNode.get("productId") == null || itemNode.get("currentValue") == null || itemNode.get("amount") == null) {
                    ctx.status(400).json(new ErrorResponse("Cada item deve conter: productId, currentValue, amount"));
                    return;
                }

                OrderItemsDTO item = new OrderItemsDTO(
                    itemNode.get("productId").asInt(),
                    itemNode.get("currentValue").asDouble(),
                    itemNode.get("amount").asInt()
                );
                orderItems.add(item);
            }

            // Create order
            Order order = orderBusiness.createOrder(userId, addressDTO, existingAddressId, orderItems);

            // Get order items to include in response
            List<OrderItems> createdItems = orderBusiness.getOrderItems(order.getId());
            double totalValue = orderBusiness.calculateOrderTotal(order.getId());

            OrderResponse response = new OrderResponse(order, createdItems, totalValue);
            ctx.status(201).json(response);

        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse("Erro ao criar pedido: " + e.getMessage()));
        }
    }

    // UPDATE - PUT /orders/{id}
    public void updateOrder(Context ctx) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            JsonNode json = objectMapper.readTree(ctx.body());

            String orderStatus = json.has("orderStatus") ? json.get("orderStatus").asText() : null;
            String paymentStatus = json.has("paymentStatus") ? json.get("paymentStatus").asText() : null;
            String shippingStatus = json.has("shippingStatus") ? json.get("shippingStatus").asText() : null;

            Order updatedOrder = orderBusiness.updateOrderStatus(orderId, orderStatus, paymentStatus, shippingStatus);

            List<OrderItems> orderItems = orderBusiness.getOrderItems(orderId);
            double totalValue = orderBusiness.calculateOrderTotal(orderId);

            OrderResponse response = new OrderResponse(updatedOrder, orderItems, totalValue);
            ctx.json(response);

        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID do pedido inválido"));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse("Erro ao atualizar pedido: " + e.getMessage()));
        }
    }

    // LIST BY USER - GET /orders (uses authenticated user)
    public void listOrdersByUser(Context ctx) {
        try {
            Integer userId = ctx.attribute("userId"); // From authentication middleware

            List<Order> orders = orderBusiness.getOrdersByUserId(userId);
            List<OrderSummary> orderSummaries = new ArrayList<>();

            for (Order order : orders) {
                List<OrderItems> items = orderBusiness.getOrderItems(order.getId());
                double totalValue = orderBusiness.calculateOrderTotal(order.getId());
                orderSummaries.add(new OrderSummary(order, items.size(), totalValue));
            }

            ctx.json(orderSummaries);

        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Erro ao buscar pedidos: " + e.getMessage()));
        }
    }

    // LIST BY USER ID - GET /users/{userId}/orders (for admin or user accessing their own orders)
    public void listOrdersByUserId(Context ctx) {
        try {
            Integer currentUserId = ctx.attribute("userId"); // From authentication middleware
            int requestedUserId = Integer.parseInt(ctx.pathParam("userId"));

            // Simple authorization - users can only see their own orders
            if (!currentUserId.equals(requestedUserId)) {
                ctx.status(403).json(new ErrorResponse("Acesso negado"));
                return;
            }

            List<Order> orders = orderBusiness.getOrdersByUserId(requestedUserId);
            List<OrderSummary> orderSummaries = new ArrayList<>();

            for (Order order : orders) {
                List<OrderItems> items = orderBusiness.getOrderItems(order.getId());
                double totalValue = orderBusiness.calculateOrderTotal(order.getId());
                orderSummaries.add(new OrderSummary(order, items.size(), totalValue));
            }

            ctx.json(orderSummaries);

        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID do usuário inválido"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Erro ao buscar pedidos: " + e.getMessage()));
        }
    }

    // CANCEL - DELETE /orders/{id}
    public void cancelOrder(Context ctx) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            Integer userId = ctx.attribute("userId"); // From authentication middleware

            // Verify order belongs to user
            Order order = orderBusiness.getOrderById(orderId);
            if (order == null) {
                ctx.status(404).json(new ErrorResponse("Pedido não encontrado"));
                return;
            }

            if (!userId.equals(order.getUser().getId())) {
                ctx.status(403).json(new ErrorResponse("Acesso negado"));
                return;
            }

            orderBusiness.cancelOrder(orderId);
            ctx.json(new SuccessResponse("Pedido cancelado com sucesso"));

        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID do pedido inválido"));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse("Erro ao cancelar pedido: " + e.getMessage()));
        }
    }

    // GET ORDER BY ID - GET /orders/{id}
    public void getOrderById(Context ctx) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            Integer userId = ctx.attribute("userId"); // From authentication middleware

            Order order = orderBusiness.getOrderById(orderId);
            if (order == null) {
                ctx.status(404).json(new ErrorResponse("Pedido não encontrado"));
                return;
            }

            // Verify order belongs to user
            if (!userId.equals(order.getUser().getId())) {
                ctx.status(403).json(new ErrorResponse("Acesso negado"));
                return;
            }

            List<OrderItems> orderItems = orderBusiness.getOrderItems(orderId);
            double totalValue = orderBusiness.calculateOrderTotal(orderId);

            OrderResponse response = new OrderResponse(order, orderItems, totalValue);
            ctx.json(response);

        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID do pedido inválido"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Erro ao buscar pedido: " + e.getMessage()));
        }
    }

    // Response classes
    private static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    private static class SuccessResponse {
        public String message;
        public SuccessResponse(String message) {
            this.message = message;
        }
    }

    private static class OrderResponse {
        public Order order;
        public List<OrderItems> items;
        public double totalValue;
        public OrderResponse(Order order, List<OrderItems> items, double totalValue) {
            this.order = order;
            this.items = items;
            this.totalValue = totalValue;
        }
    }

    private static class OrderSummary {
        public Order order;
        public int itemCount;
        public double totalValue;
        public OrderSummary(Order order, int itemCount, double totalValue) {
            this.order = order;
            this.itemCount = itemCount;
            this.totalValue = totalValue;
        }
    }
}
