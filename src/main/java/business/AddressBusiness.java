package business;

import entity.Address;
import entity.User;
import repository.RepositoryInterface;

import java.util.List;

public class AddressBusiness {
    private RepositoryInterface<Address> addressRepository;
    private RepositoryInterface<User> userRepository;

    public AddressBusiness(RepositoryInterface<Address> addressRepository, RepositoryInterface<User> userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public Address createAddress(int userId, String street, String number, String neighborhood,
                                String zipCode, String complement, String city, String state) throws Exception {
        // Validation
        if (street == null || street.trim().isEmpty()) {
            throw new Exception("Rua é obrigatória");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new Exception("Número é obrigatório");
        }
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new Exception("Bairro é obrigatório");
        }
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new Exception("CEP é obrigatório");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new Exception("Cidade é obrigatória");
        }
        if (state == null || state.trim().isEmpty()) {
            throw new Exception("Estado é obrigatório");
        }

        // Verify user exists
        User user = userRepository.get(userId);
        if (user == null) {
            throw new Exception("Usuário não encontrado");
        }

        Address address = new Address(0, userId, street.trim(), number.trim(), neighborhood.trim(),
                                    zipCode.trim(), complement != null ? complement.trim() : "",
                                    city.trim(), state.trim());
        return addressRepository.create(address);
    }

    public Address updateAddress(int addressId, int userId, String street, String number, String neighborhood,
                                String zipCode, String complement, String city, String state) throws Exception {
        Address existingAddress = addressRepository.get(addressId);
        if (existingAddress == null) {
            throw new Exception("Endereço não encontrado");
        }

        // Verify address belongs to user
        if (existingAddress.getUserId() != userId) {
            throw new Exception("Você não tem permissão para alterar este endereço");
        }

        // Validation
        if (street == null || street.trim().isEmpty()) {
            throw new Exception("Rua é obrigatória");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new Exception("Número é obrigatório");
        }
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new Exception("Bairro é obrigatório");
        }
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new Exception("CEP é obrigatório");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new Exception("Cidade é obrigatória");
        }
        if (state == null || state.trim().isEmpty()) {
            throw new Exception("Estado é obrigatório");
        }

        existingAddress.setStreet(street.trim());
        existingAddress.setNumber(number.trim());
        existingAddress.setNeighborhood(neighborhood.trim());
        existingAddress.setZipCode(zipCode.trim());
        existingAddress.setComplement(complement != null ? complement.trim() : "");
        existingAddress.setCity(city.trim());
        existingAddress.setState(state.trim());

        return addressRepository.update(existingAddress);
    }

    public List<Address> getAddressesByUserId(int userId) throws Exception {
        // First, validate that the user exists
        if (userRepository.get(userId) == null) {
            throw new Exception("Usuário não encontrado");
        }

        // Note: We need to implement filtering by user ID using the generic interface
        List<Address> allAddresses = addressRepository.list();
        List<Address> userAddresses = new java.util.ArrayList<>();
        for (Address address : allAddresses) {
            if (address.getUserId() == userId) {
                userAddresses.add(address);
            }
        }
        return userAddresses;
    }

    public Address getAddressById(int addressId, int userId) throws Exception {
        Address address = addressRepository.get(addressId);
        if (address == null) {
            throw new Exception("Endereço não encontrado");
        }

        // Verify address belongs to user
        if (address.getUserId() != userId) {
            throw new Exception("Você não tem permissão para acessar este endereço");
        }

        return address;
    }

    public void deleteAddress(int addressId, int userId) throws Exception {
        Address address = addressRepository.get(addressId);
        if (address == null) {
            throw new Exception("Endereço não encontrado");
        }

        // Verify address belongs to user
        if (address.getUserId() != userId) {
            throw new Exception("Você não tem permissão para deletar este endereço");
        }

        addressRepository.delete(addressId);
    }

    public List<Address> getAllAddresses(int userId) throws Exception {
        return getAddressesByUserId(userId);
    }
}
