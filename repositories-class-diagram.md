classDiagram
    class RepositoryInterface~T~ {
        <<interface>>
        +create(T entity) T
        +update(T entity) T
        +list() List~T~
        +get(int id) T
        +delete(int id) void
    }

    class UserRepository {
        +create(User user) User
        +update(User user) User
        +list() List~User~
        +get(int id) User
        +delete(int id) void
        +findByEmail(String email) User
    }

    class ProductRepository {
        +create(Product product) Product
        +update(Product product) Product
        +list() List~Product~
        +get(int id) Product
        +delete(int id) void
        +listActive() List~Product~
    }

    class AddressRepository {
        +create(Address address) Address
        +update(Address address) Address
        +list() List~Address~
        +get(int id) Address
        +delete(int id) void
        +findByUserId(int userId) List~Address~
    }

    class OrderRepository {
        +create(Order order) Order
        +update(Order order) Order
        +list() List~Order~
        +get(int id) Order
        +delete(int id) void
        +findByUserId(int userId) List~Order~
    }

    class OrderItemsRepository {
        +create(OrderItems item) OrderItems
        +update(OrderItems item) OrderItems
        +list() List~OrderItems~
        +get(int id) OrderItems
        +delete(int id) void
        +findByOrderId(int orderId) List~OrderItems~
    }
 
    RepositoryInterface~T~ <|.. UserRepository : implements
    RepositoryInterface~T~ <|.. ProductRepository : implements
    RepositoryInterface~T~ <|.. AddressRepository : implements
    RepositoryInterface~T~ <|.. OrderRepository : implements
    RepositoryInterface~T~ <|.. OrderItemsRepository : implements