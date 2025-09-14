classDiagram
    class AddressDTO {
        -String street
        -String number
        -String neighborhood
        -String zipCode
        -String complement
        -String city
        -String state
    }

    class OrderItemsDTO {
        -int productId
        -int amount
    }