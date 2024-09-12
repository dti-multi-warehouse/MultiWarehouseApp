package com.dti.multiwarehouse.address.controller;

import com.dti.multiwarehouse.address.dto.UserAddressDTO;
import com.dti.multiwarehouse.address.entity.UserAddress;
import com.dti.multiwarehouse.address.service.AddressService;
import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<?> addAddress(@RequestBody UserAddressDTO userAddressDTO, @PathVariable Long userId) {
        UserAddress savedUserAddress = addressService.saveUserAddress(userAddressDTO, userId);
        return Response.success("Address successfully added", savedUserAddress);
    }

    @PutMapping("/user/{userId}/update/{addressId}")
    public ResponseEntity<?> updateUserAddress(@RequestBody UserAddressDTO userAddressDTO, @PathVariable Long userId, @PathVariable Long addressId) {
        UserAddress updatedUserAddress = addressService.updateUserAddress(addressId, userAddressDTO, userId);
        return Response.success("Address successfully updated", updatedUserAddress);
    }

    @GetMapping("/userAddress/{id}")
    public ResponseEntity<?> getUserAddressById(@PathVariable Long id){
        Optional<UserAddress> userAddress = addressService.getUserAddressById(id);
        return userAddress.map(ua -> Response.success("User address found", ua))
                .orElseThrow(() -> new ResourceNotFoundException("User address not found with id: " + id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response<List<?>>> getUserAddresses(@PathVariable Long userId) {
        List<UserAddress> addresses = addressService.getUserAddresses(userId);
        return Response.success("User addresses retrieved", addresses);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Object>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddressById(id);
        return Response.success("Address and associated records successfully deleted");
    }
}
