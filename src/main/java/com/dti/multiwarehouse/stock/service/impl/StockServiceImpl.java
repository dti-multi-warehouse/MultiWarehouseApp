package com.dti.multiwarehouse.stock.service.impl;

import com.dti.multiwarehouse.cart.dto.CartItem;
import com.dti.multiwarehouse.product.service.ProductService;
import com.dti.multiwarehouse.stock.dao.Stock;
import com.dti.multiwarehouse.stock.dao.StockMutation;
import com.dti.multiwarehouse.stock.dao.enums.StockMutStatus;
import com.dti.multiwarehouse.stock.dao.key.StockCompositeKey;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.repository.StockMutationRepository;
import com.dti.multiwarehouse.stock.repository.StockRepository;
import com.dti.multiwarehouse.stock.service.StockService;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockMutationRepository stockMutationRepository;

    private final WarehouseService warehouseService;
    private final ProductService productService;

    @Override
    public void restock(RestockRequestDto requestDto) {
        var product = productService.findProductById(requestDto.getProductId());
        var warehouseTo = warehouseService.findWarehouseById(requestDto.getWarehouseToId());
        var stockMutation = StockMutation.builder()
                .product(product)
                .warehouseFrom(null)
                .warehouseTo(warehouseTo)
                .quantity(requestDto.getQuantity())
                .status(StockMutStatus.COMPLETED)
                .build();
        stockMutationRepository.save(stockMutation);
        stockMutationRepository.calculateProductStock(requestDto.getProductId());
        calculateWarehouseStock(requestDto.getProductId(), requestDto.getWarehouseToId());
    }

    @Override
    public void requestStockMutation(RequestMutationRequestDto requestDto) {
        var product = productService.findProductById(requestDto.getProductId());
        var warehouseTo = warehouseService.findWarehouseById(requestDto.getWarehouseToId());
        var warehouseFrom = warehouseService.findWarehouseById(requestDto.getWarehouseFromId());
        var stockMutation = StockMutation.builder()
                .product(product)
                .warehouseFrom(warehouseFrom)
                .warehouseTo(warehouseTo)
                .quantity(requestDto.getQuantity())
                .status(StockMutStatus.AWAITING_CONFIRMATION)
                .build();
        stockMutationRepository.save(stockMutation);
       calculateWarehouseStock(requestDto.getProductId(), requestDto.getWarehouseToId());
       calculateWarehouseStock(requestDto.getProductId(), requestDto.getWarehouseFromId());
    }

    @Override
    public void acceptStockMutation(Long stockMutationId) {
        mutateStock(stockMutationId, StockMutStatus.COMPLETED);
    }

    @Override
    public void cancelStockMutation(Long stockMutationId) {
        mutateStock(stockMutationId, StockMutStatus.CANCELLED);
    }

    @Override
    public void rejectStockMutation(Long stockMutationId) {
        mutateStock(stockMutationId, StockMutStatus.REJECTED);
    }

    public void processOrder(Long warehouseId, List<CartItem> cartItems) {
        var closestWarehouse = warehouseService.findWarehouseById(warehouseId);
        Map<Long, Integer> productQuantities = new HashMap<>();

//        other warehouses, assume that they are sorted by closest distance
        var warehouses = warehouseService.getAllWarehouses();
        warehouses.removeFirst();

        for (var item : cartItems) {
            productQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var requiredQuantity = entry.getValue();

            var product = productService.findProductById(productId);
            var stockOptional = stockRepository.findById(new StockCompositeKey(product, closestWarehouse));

            int quantityToMutate = stockOptional
                    .map(stock -> Math.max(0, requiredQuantity - stock.getStock()))
                    .orElse(requiredQuantity);

            if (quantityToMutate > 0) {
                var isFulfilled = autoMutateStock(productId, closestWarehouse, warehouses, quantityToMutate);
                if (!isFulfilled) {
                    greedyAutoMutateStock(productId, closestWarehouse, warehouses, quantityToMutate);
                }
            } else {
                calculateWarehouseStock(productId, closestWarehouse.getId());
            }
            System.out.println("HEREE");
            stockMutationRepository.calculateProductStock(productId);
        }
    }

    private boolean autoMutateStock(Long productId, Warehouse closestWarehouse, List<Warehouse> warehouses, int quantity) {
        for (var warehouse : warehouses) {
            var product = productService.findProductById(productId);
            var stock = stockRepository.findById(new StockCompositeKey(product, warehouse)).orElse(new Stock());
            if (stock.getStock() >= quantity) {
                var stockMutation = StockMutation.builder()
                        .product(product)
                        .warehouseFrom(warehouse)
                        .warehouseTo(closestWarehouse)
                        .quantity(quantity)
                        .status(StockMutStatus.COMPLETED)
                        .build();
                stockMutationRepository.save(stockMutation);
                calculateWarehouseStock(productId, closestWarehouse.getId());
                calculateWarehouseStock(productId, warehouse.getId());
                return true;
            }
        }
        return false;
    }

    private void greedyAutoMutateStock(Long productId, Warehouse closestWarehouse, List<Warehouse> warehouses, int quantity) {
        int accumulator = 0;
        int i = 0;
        var product = productService.findProductById(productId);
        while (accumulator <= quantity) {
            var warehouse = warehouses.get(i);
            var stock = stockRepository.findById(new StockCompositeKey(product, warehouse)).orElse(new Stock());
            if (stock.getStock() > 0) {
                var quantityToMutate = Math.max(stock.getStock(), quantity);
                var stockMutation = StockMutation.builder()
                        .product(product)
                        .warehouseFrom(warehouse)
                        .warehouseTo(closestWarehouse)
                        .quantity(quantityToMutate)
                        .status(StockMutStatus.COMPLETED)
                        .build();
                stockMutationRepository.save(stockMutation);
                calculateWarehouseStock(productId, closestWarehouse.getId());
                calculateWarehouseStock(productId, warehouse.getId());
                accumulator += quantityToMutate;
            }
            i++;
        }
    }

    private void mutateStock(Long stockMutationId, StockMutStatus status) {
        var stockMutation = stockMutationRepository
                .findById(stockMutationId)
                .orElseThrow(() -> new EntityNotFoundException("Stock mutation with id " + stockMutationId + " not found"));
        stockMutation.setStatus(status);
        stockMutationRepository.save(stockMutation);
        calculateWarehouseStock(stockMutation.getProduct().getId(), stockMutation.getWarehouseTo().getId());
        calculateWarehouseStock(stockMutation.getProduct().getId(), stockMutation.getWarehouseFrom().getId());
    }

    private void calculateWarehouseStock(Long productId, Long warehouseId) {
        createStockIfNotExist(productId, warehouseId);
        stockMutationRepository.calculateWarehouseStock(productId, warehouseId);
    }

    private void createStockIfNotExist(Long productId, Long warehouseId) {
        var product = productService.findProductById(productId);
        var warehouse = warehouseService.findWarehouseById(warehouseId);
        var key = new StockCompositeKey();
        key.setProduct(product);
        key.setWarehouse(warehouse);
        var stock = stockRepository.findById(key);
        if (stock.isEmpty()) {
            var newStock = new Stock(key, 0);
            stockRepository.save(newStock);
        }
    }
}
