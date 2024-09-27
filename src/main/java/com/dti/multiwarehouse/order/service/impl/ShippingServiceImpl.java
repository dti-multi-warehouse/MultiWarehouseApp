package com.dti.multiwarehouse.order.service.impl;

import com.dti.multiwarehouse.address.entity.UserAddress;
import com.dti.multiwarehouse.address.repository.UserAddressRepository;
import com.dti.multiwarehouse.address.repository.WarehouseAddressRepository;
import com.dti.multiwarehouse.exceptions.ApplicationException;
import com.dti.multiwarehouse.exceptions.ResourceNotFoundException;
import com.dti.multiwarehouse.order.dto.request.ShippingCostRequestDto;
import com.dti.multiwarehouse.order.dto.response.ShippingCostResponseDto;
import com.dti.multiwarehouse.order.service.ShippingService;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShippingServiceImpl implements ShippingService {
    private static final Logger log = LoggerFactory.getLogger(ShippingServiceImpl.class);

    private final WarehouseRepository warehouseRepository;
    private final UserAddressRepository userAddressRepository;
    private final RestTemplate restTemplate;

    @Value("${rajaongkir.api.key}")
    private String apiKey;

    @Value("${rajaongkir.api.url}")
    private String apiUrl;

    @Override
    public ShippingCostResponseDto calculateShippingCost(ShippingCostRequestDto request) {
        UserAddress userAddress = userAddressRepository.findById(request.getDestinationCityId())
                .orElseThrow(() -> new ResourceNotFoundException("User address not found with ID: " + request.getDestinationCityId()));

        Warehouse nearestWarehouse = findNearestWarehouse(userAddress.getId());
        if (nearestWarehouse == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "No suitable warehouse found for the shipping address");
        }

        String payload = createPayload(nearestWarehouse.getWarehouseAddress().getAddress().getId(), userAddress.getAddress().getId(), request);
        HttpEntity<String> entity = createHttpEntity(payload);

        try {
            ResponseEntity<ShippingCostResponseDto> response = restTemplate.postForEntity(apiUrl, entity, ShippingCostResponseDto.class);
            ShippingCostResponseDto responseBody = response.getBody();
            log.info("Response from RajaOngkir: {}", responseBody);

            if (responseBody == null || responseBody.getRajaOngkir() == null) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid response from shipping API");
            }

            if (responseBody.getRajaOngkir().getResults() == null || responseBody.getRajaOngkir().getResults().isEmpty()) {
                log.warn("No shipping options available for the given parameters");
                return responseBody;
            }

            if (responseBody.getRajaOngkir().getResults().get(0).getCosts() == null || responseBody.getRajaOngkir().getResults().get(0).getCosts().isEmpty()) {
                log.warn("No cost information available for the selected courier");
                return responseBody;
            }

            return responseBody;

        } catch (RestClientException e) {
            log.error("Error calling RajaOngkir API", e);
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Error calculating shipping cost: " + e.getMessage());
        }
    }


    private String createPayload(Long warehouseId, Long userAddressId, ShippingCostRequestDto request) {
        return String.format(
                "origin=%s&destination=%s&weight=%d&courier=%s",
                warehouseId,
                userAddressId,
                request.getWeight(),
                request.getCourier()
        );
    }

    private HttpEntity<String> createHttpEntity(String payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("key", apiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(payload, headers);
    }

    @Override
    public Warehouse findNearestWarehouse(Long userAddressId) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(() -> new ResourceNotFoundException("User address not found with ID: " + userAddressId));

        Warehouse nearestWarehouse = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            double distance = haversineDistance(
                    userAddress.getAddress().getLatitude(),
                    userAddress.getAddress().getLongitude(),
                    warehouse.getWarehouseAddress().getAddress().getLatitude(),
                    warehouse.getWarehouseAddress().getAddress().getLongitude()
            );

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestWarehouse = warehouse;
            }
        }

        return nearestWarehouse;
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
