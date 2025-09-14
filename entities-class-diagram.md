classDiagram
    class User {
        -int id
        -String name
        -String email
        -String password
        -String document
    }

    class Address {
        -int id
        -User user
        -String street
        -String number
        -String neighborhood
        -String zipCode
        -String complement
        -String city
        -String state
    }

    class Product {
        -int id
        -String name
        -double value
        -String description
        -int availableAmount
        -String image
        -Date deletedAt
    }

    class Order {
        -int id
        -User user
        -Address address
        -OrderStatus orderStatus
        -Date createdAt
        -Date updatedAt
    }

    class OrderStatus {
        <<enumeration>>
        PENDING
        PAID
        PROCESSING
        SHIPPED
        DELIVERED
        CANCELLED
        REFUNDED
    }

    class OrderItems {
        -int id
        -Order order
        -Product product
        -int amount
        -double value
    }

    User "1" --> "0..*" Address
    User "1" --> "0..*" Order
    Address "1" --> "0..*" Order
    Order "1" *-- "1" OrderStatus
    Order "1" --> "1..*" OrderItems
    Product "1" --> "0..*" OrderItems