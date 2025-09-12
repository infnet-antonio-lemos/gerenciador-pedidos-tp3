package repository;

import entity.Address;

import java.io.*;
import java.util.*;

public class AddressRepository {
    private static final String FILE_NAME = "addresses.csv";

    public AddressRepository() {
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                try (FileWriter fw = new FileWriter(FILE_NAME);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    out.println("id,user_id,street,number,neighborhood,zip_code,complement,city,state");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo CSV: " + e.getMessage());
        }
    }

    public void save(Address address) throws IOException {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.printf("%d,%d,%s,%s,%s,%s,%s,%s,%s\n",
                address.getId(),
                address.getUserId(),
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getZipCode(),
                address.getComplement() != null ? address.getComplement() : "",
                address.getCity(),
                address.getState());
        }
    }

    public void update(Address address) throws IOException {
        List<Address> addresses = findAll();
        try (FileWriter fw = new FileWriter(FILE_NAME);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // Write header
            out.println("id,user_id,street,number,neighborhood,zip_code,complement,city,state");

            // Write all addresses, replacing the updated one
            for (Address a : addresses) {
                if (a.getId() == address.getId()) {
                    out.printf("%d,%d,%s,%s,%s,%s,%s,%s,%s\n",
                        address.getId(),
                        address.getUserId(),
                        address.getStreet(),
                        address.getNumber(),
                        address.getNeighborhood(),
                        address.getZipCode(),
                        address.getComplement() != null ? address.getComplement() : "",
                        address.getCity(),
                        address.getState());
                } else {
                    out.printf("%d,%d,%s,%s,%s,%s,%s,%s,%s\n",
                        a.getId(),
                        a.getUserId(),
                        a.getStreet(),
                        a.getNumber(),
                        a.getNeighborhood(),
                        a.getZipCode(),
                        a.getComplement() != null ? a.getComplement() : "",
                        a.getCity(),
                        a.getState());
                }
            }
        }
    }

    public List<Address> findAll() throws IOException {
        List<Address> addresses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length >= 8) {
                    try {
                        Address address = new Address(
                            Integer.parseInt(parts[0]), // id
                            Integer.parseInt(parts[1]), // user_id
                            parts[2], // street
                            parts[3], // number
                            parts[4], // neighborhood
                            parts[5], // zip_code
                            parts.length > 6 ? parts[6] : "", // complement
                            parts.length > 7 ? parts[7] : "", // city
                            parts.length > 8 ? parts[8] : ""  // state
                        );
                        addresses.add(address);
                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao parsear linha: " + line);
                    }
                }
            }
        }
        return addresses;
    }

    public Address findById(int id) throws IOException {
        for (Address address : findAll()) {
            if (address.getId() == id) {
                return address;
            }
        }
        return null;
    }

    public List<Address> findByUserId(int userId) throws IOException {
        List<Address> userAddresses = new ArrayList<>();
        for (Address address : findAll()) {
            if (address.getUserId() == userId) {
                userAddresses.add(address);
            }
        }
        return userAddresses;
    }

    public void delete(int id) throws IOException {
        List<Address> addresses = findAll();
        try (FileWriter fw = new FileWriter(FILE_NAME);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // Write header
            out.println("id,user_id,street,number,neighborhood,zip_code,complement,city,state");

            // Write all addresses except the one to delete
            for (Address address : addresses) {
                if (address.getId() != id) {
                    out.printf("%d,%d,%s,%s,%s,%s,%s,%s,%s\n",
                        address.getId(),
                        address.getUserId(),
                        address.getStreet(),
                        address.getNumber(),
                        address.getNeighborhood(),
                        address.getZipCode(),
                        address.getComplement() != null ? address.getComplement() : "",
                        address.getCity(),
                        address.getState());
                }
            }
        }
    }

    public int getNextId() throws IOException {
        List<Address> addresses = findAll();
        return addresses.isEmpty() ? 1 : addresses.stream().mapToInt(Address::getId).max().orElse(0) + 1;
    }
}
