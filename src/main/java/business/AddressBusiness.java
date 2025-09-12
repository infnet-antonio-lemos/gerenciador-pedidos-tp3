package business;

import entity.Address;
import entity.User;
import repository.AddressRepository;
import repository.UserRepository;

import java.util.List;

public class AddressBusiness {
    private AddressRepository addressRepository;
    private UserRepository userRepository;

    public AddressBusiness(AddressRepository addressRepository, UserRepository userRepository) {
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

        // Validate user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new Exception("Usuário não encontrado");
        }

        int id = addressRepository.getNextId();
        Address address = new Address(id, user, street.trim(), number.trim(), neighborhood.trim(),
                                    zipCode.trim(), complement != null ? complement.trim() : "",
                                    city.trim(), state.trim());
        addressRepository.save(address);
        return address;
    }

    public Address updateAddress(int id, String street, String number, String neighborhood,
                                String zipCode, String complement, String city, String state) throws Exception {
        Address existingAddress = addressRepository.findById(id);
        if (existingAddress == null) {
            throw new Exception("Endereço não encontrado");
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

        // Update fields
        existingAddress.setStreet(street.trim());
        existingAddress.setNumber(number.trim());
        existingAddress.setNeighborhood(neighborhood.trim());
        existingAddress.setZipCode(zipCode.trim());
        existingAddress.setComplement(complement != null ? complement.trim() : "");
        existingAddress.setCity(city.trim());
        existingAddress.setState(state.trim());

        addressRepository.update(existingAddress);
        return existingAddress;
    }

    public List<Address> getAllAddresses() throws Exception {
        return addressRepository.findAll();
    }

    public List<Address> getAddressesByUserId(int userId) throws Exception {
        // Validate user exists
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new Exception("Usuário não encontrado");
        }
        return addressRepository.findByUserId(userId);
    }

    public Address getAddressById(int id) throws Exception {
        Address address = addressRepository.findById(id);
        if (address == null) {
            throw new Exception("Endereço não encontrado");
        }
        return address;
    }

    public void deleteAddress(int id) throws Exception {
        Address address = addressRepository.findById(id);
        if (address == null) {
            throw new Exception("Endereço não encontrado");
        }
        addressRepository.delete(id);
    }
}
