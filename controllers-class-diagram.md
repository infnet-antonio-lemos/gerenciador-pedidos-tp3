classDiagram
    class AddressHttpController {
        +createAddress(Context) Address
        +getAllAddresses(Context) List~Address~
        +getAddressById(Context) Address
        +updateAddress(Context) Address
        +deleteAddress(Context) void
    }

    class AuthHttpController {
        +createUser(Context) User
        +login(Context) User
        +logout(Context) void
        +getProfile(Context) User
    }

    class OrderHttpController {
        +createOrder(Context) Order
        +updateOrder(Context) Order
        +listOrdersByUser(Context) List~Order~
        +listOrdersByUserId(Context) Order
        +cancelOrder(Context) Order
        +getOrderById(Context) Order
    }

    class ProductHttpController {
        +createProduct(Context) Product
        +getAllProducts(Context) List~Product~
        +getProductById(Context) Product
        +updateProduct(Context) Product
        +deleteProduct(Context) void
    }