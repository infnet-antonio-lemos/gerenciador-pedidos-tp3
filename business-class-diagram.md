classDiagram
    class AddressBusiness {
        +createAddress(int, String, String, String, String, String, String, String) Address
        +updateAddress(int, int, String, String, String, String, String, String, String) Address
        +getAddressesByUserId(int) List~Address~
        +getAddressById(int, int) Address
        +deleteAddress(int, int) void
        +getAllAddresses(int) List~Address~
    }

    class AuthBusiness {
        +createUser(String, String, String, String, String) User
        +login(String, String) User
        +getUserById(int) User
    }

    class OrderBusiness {
        +createOrder(int, AddressDTO, Integer, List~OrderItemsDTO~) Order
        +getOrdersByUserId(int) List~Order~
        +getOrderById(int) Order
        +getOrderItems(int) List~OrderItems~
        +updateOrderStatus(int, String) Order
        +updateOrderStatus(int, OrderStatus) Order
        +cancelOrder(int) void
        +calculateOrderTotal(int) double
    }

    class ProductBusiness {
        +createProduct(String, double, String, int, String) Product
        +updateProduct(int, String, double, String, int, String) Product
        +getAllProducts() List~Product~
        +getProductById(int) Product
        +deleteProduct(int) void
    }