package controller;

import business.AddressBusiness;
import entity.Address;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class AddressHttpController {
    private AddressBusiness addressBusiness;
    private ObjectMapper objectMapper;

    public AddressHttpController(AddressBusiness addressBusiness) {
        this.addressBusiness = addressBusiness;
        this.objectMapper = new ObjectMapper();
    }

    // CREATE - POST /addresses
    public void createAddress(Context ctx) {
        try {
            // Get authenticated user ID from context (set by authentication middleware)
            Integer userId = ctx.attribute("userId");
            if (userId == null) {
                ctx.status(401).json(new ErrorResponse("Usuário não autenticado"));
                return;
            }

            JsonNode json = objectMapper.readTree(ctx.body());

            // Validate required fields exist (userId no longer required in body)
            if (json.get("street") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'street' é obrigatório"));
                return;
            }
            if (json.get("number") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'number' é obrigatório"));
                return;
            }
            if (json.get("neighborhood") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'neighborhood' é obrigatório"));
                return;
            }
            if (json.get("zipCode") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'zipCode' é obrigatório"));
                return;
            }
            if (json.get("city") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'city' é obrigatório"));
                return;
            }
            if (json.get("state") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'state' é obrigatório"));
                return;
            }

            // Use authenticated user ID instead of getting from request body
            String street = json.get("street").asText();
            String number = json.get("number").asText();
            String neighborhood = json.get("neighborhood").asText();
            String zipCode = json.get("zipCode").asText();
            String complement = json.has("complement") ? json.get("complement").asText() : "";
            String city = json.get("city").asText();
            String state = json.get("state").asText();

            Address address = addressBusiness.createAddress(userId, street, number, neighborhood, zipCode, complement, city, state);

            ctx.status(201).json(new AddressResponse(address));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    // READ - GET /addresses
    public void getAllAddresses(Context ctx) {
        try {
            List<Address> addresses = addressBusiness.getAllAddresses();
            ctx.status(200).json(addresses.stream()
                .map(AddressResponse::new)
                .toArray(AddressResponse[]::new));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse(e.getMessage()));
        }
    }

    // READ - GET /addresses/:id
    public void getAddressById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Address address = addressBusiness.getAddressById(id);

            ctx.status(200).json(new AddressResponse(address));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID inválido"));
        } catch (Exception e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        }
    }

    // READ - GET /users/:userId/addresses
    public void getAddressesByUserId(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            List<Address> addresses = addressBusiness.getAddressesByUserId(userId);

            ctx.status(200).json(addresses.stream()
                .map(AddressResponse::new)
                .toArray(AddressResponse[]::new));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID do usuário inválido"));
        } catch (Exception e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        }
    }

    // UPDATE - PUT /addresses/:id
    public void updateAddress(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            JsonNode json = objectMapper.readTree(ctx.body());

            // Validate required fields exist
            if (json.get("street") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'street' é obrigatório"));
                return;
            }
            if (json.get("number") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'number' é obrigatório"));
                return;
            }
            if (json.get("neighborhood") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'neighborhood' é obrigatório"));
                return;
            }
            if (json.get("zipCode") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'zipCode' é obrigatório"));
                return;
            }
            if (json.get("city") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'city' é obrigatório"));
                return;
            }
            if (json.get("state") == null) {
                ctx.status(400).json(new ErrorResponse("Campo 'state' é obrigatório"));
                return;
            }

            String street = json.get("street").asText();
            String number = json.get("number").asText();
            String neighborhood = json.get("neighborhood").asText();
            String zipCode = json.get("zipCode").asText();
            String complement = json.has("complement") ? json.get("complement").asText() : "";
            String city = json.get("city").asText();
            String state = json.get("state").asText();

            Address address = addressBusiness.updateAddress(id, street, number, neighborhood, zipCode, complement, city, state);

            ctx.status(200).json(new AddressResponse(address));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID inválido"));
        } catch (Exception e) {
            ctx.status(400).json(new ErrorResponse(e.getMessage()));
        }
    }

    // DELETE - DELETE /addresses/:id
    public void deleteAddress(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            addressBusiness.deleteAddress(id);

            ctx.status(200).json(new MessageResponse("Endereço deletado com sucesso"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new ErrorResponse("ID inválido"));
        } catch (Exception e) {
            ctx.status(404).json(new ErrorResponse(e.getMessage()));
        }
    }

    // Response classes
    public static class AddressResponse {
        public int id;
        public int userId;
        public String street;
        public String number;
        public String neighborhood;
        public String zipCode;
        public String complement;
        public String city;
        public String state;

        public AddressResponse(Address address) {
            this.id = address.getId();
            this.userId = address.getUserId();
            this.street = address.getStreet();
            this.number = address.getNumber();
            this.neighborhood = address.getNeighborhood();
            this.zipCode = address.getZipCode();
            this.complement = address.getComplement();
            this.city = address.getCity();
            this.state = address.getState();
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
