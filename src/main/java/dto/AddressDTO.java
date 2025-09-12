package dto;

public class AddressDTO {
    private String street;
    private String number;
    private String neighborhood;
    private String zipCode;
    private String complement;
    private String city;
    private String state;

    public AddressDTO() {}

    public AddressDTO(String street, String number, String neighborhood, String zipCode, String complement, String city, String state) {
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.zipCode = zipCode;
        this.complement = complement;
        this.city = city;
        this.state = state;
    }

    // Getters
    public String getStreet() { return street; }
    public String getNumber() { return number; }
    public String getNeighborhood() { return neighborhood; }
    public String getZipCode() { return zipCode; }
    public String getComplement() { return complement; }
    public String getCity() { return city; }
    public String getState() { return state; }

    // Setters
    public void setStreet(String street) { this.street = street; }
    public void setNumber(String number) { this.number = number; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setComplement(String complement) { this.complement = complement; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
}
