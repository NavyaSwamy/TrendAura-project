package com.trendaura.service;

import com.trendaura.dto.AddressRequest;
import com.trendaura.dto.AddressResponse;
import com.trendaura.entity.Address;
import com.trendaura.entity.User;
import com.trendaura.repository.AddressRepository;
import com.trendaura.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByDefaultAddressDesc(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Address a = new Address();
        a.setUser(user);
        mapRequestToEntity(req, a);
        if (req.isDefaultAddress()) {
            addressRepository.findByUserIdOrderByDefaultAddressDesc(userId).forEach(ad -> {
                ad.setDefaultAddress(false);
                addressRepository.save(ad);
            });
        }
        a = addressRepository.save(a);
        return toResponse(a);
    }

    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest req) {
        Address a = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));
        if (!a.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");
        mapRequestToEntity(req, a);
        if (req.isDefaultAddress()) {
            addressRepository.findByUserIdOrderByDefaultAddressDesc(userId).forEach(ad -> {
                ad.setDefaultAddress(ad.getId().equals(addressId));
                addressRepository.save(ad);
            });
        }
        a = addressRepository.save(a);
        return toResponse(a);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address a = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));
        if (!a.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");
        addressRepository.delete(a);
    }

    private void mapRequestToEntity(AddressRequest req, Address a) {
        a.setLine1(req.getLine1());
        a.setLine2(req.getLine2());
        a.setCity(req.getCity());
        a.setState(req.getState());
        a.setPincode(req.getPincode());
        a.setCountry(req.getCountry() != null ? req.getCountry() : "India");
        a.setLabel(req.getLabel());
        a.setDefaultAddress(req.isDefaultAddress());
    }

    private AddressResponse toResponse(Address a) {
        AddressResponse r = new AddressResponse();
        r.setId(a.getId());
        r.setLine1(a.getLine1());
        r.setLine2(a.getLine2());
        r.setCity(a.getCity());
        r.setState(a.getState());
        r.setPincode(a.getPincode());
        r.setCountry(a.getCountry());
        r.setLabel(a.getLabel());
        r.setDefaultAddress(a.isDefaultAddress());
        return r;
    }
}
